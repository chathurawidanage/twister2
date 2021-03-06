//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
package edu.iu.dsc.tws.rsched.schedulers.k8s;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.iu.dsc.tws.api.config.Config;
import edu.iu.dsc.tws.api.config.SchedulerContext;
import edu.iu.dsc.tws.api.exceptions.Twister2Exception;
import edu.iu.dsc.tws.api.scheduler.ILauncher;
import edu.iu.dsc.tws.api.scheduler.Twister2JobState;
import edu.iu.dsc.tws.checkpointing.util.CheckpointingContext;
import edu.iu.dsc.tws.master.JobMasterContext;
import edu.iu.dsc.tws.master.server.JobMaster;
import edu.iu.dsc.tws.proto.jobmaster.JobMasterAPI;
import edu.iu.dsc.tws.proto.system.job.JobAPI;
import edu.iu.dsc.tws.proto.utils.NodeInfoUtils;
import edu.iu.dsc.tws.rsched.schedulers.k8s.driver.K8sScaler;
import edu.iu.dsc.tws.rsched.schedulers.k8s.logger.JobLogger;
import edu.iu.dsc.tws.rsched.schedulers.k8s.master.JobMasterRequestObject;
import edu.iu.dsc.tws.rsched.schedulers.k8s.master.JobTerminator;
import edu.iu.dsc.tws.rsched.utils.JobUtils;

import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaim;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1StatefulSet;

public class KubernetesLauncher implements ILauncher {

  private static final Logger LOG = Logger.getLogger(KubernetesLauncher.class.getName());

  private Config config;
  private KubernetesController controller;
  private String namespace;
  private JobSubmissionStatus jobSubmissionStatus;
  private JobLogger jobLogger;

  public KubernetesLauncher() {
  }

  @Override
  public void initialize(Config conf) {
    this.config = conf;
    namespace = KubernetesContext.namespace(config);
    controller = KubernetesController.init(namespace);
    jobSubmissionStatus = new JobSubmissionStatus();
  }

  /**
   * Launch the processes according to the resource plan.
   *
   * @return true if the request is granted
   */
  @Override
  public Twister2JobState launch(JobAPI.Job job) {

    Twister2JobState state = new Twister2JobState(false);

    if (!configParametersOK(job)) {
      return state;
    }

    String jobID = job.getJobId();

    String jobPackageFile = SchedulerContext.temporaryPackagesPath(config) + "/"
        + SchedulerContext.jobPackageFileName(config);

    File jobFile = new File(jobPackageFile);
    if (!jobFile.exists()) {
      LOG.log(Level.SEVERE, "Can not access job package file: " + jobPackageFile
          + "\n++++++++++++++++++ Aborting submission ++++++++++++++++++");
      return state;
    }

    long jobFileSize = jobFile.length();

    // check all relevant entities on Kubernetes master
    boolean allEntitiesOK = checkEntitiesForJob(job);
    if (!allEntitiesOK) {
      return state;
    }

    long jobSubmitTime = System.currentTimeMillis();
    String encodedNodeList = getNodeInfoList();
    RequestObjectBuilder.init(config, job.getJobId(), jobFileSize, jobSubmitTime, encodedNodeList);
    JobMasterRequestObject.init(config, job.getJobId());

    // initialize the service in Kubernetes master
    boolean servicesCreated = initServices(jobID);
    if (!servicesCreated) {
      clearupWhenSubmissionFails(jobID);
      return state;
    }

    // create the ConfigMap
    V1ConfigMap configMap = RequestObjectBuilder.createConfigMap(job);
    boolean cmCreated = controller.createConfigMap(configMap);
    if (cmCreated) {
      jobSubmissionStatus.setConfigMapCreated(true);
    } else {
      LOG.severe("Following ConfigMap could not be created: " + configMap.getMetadata().getName()
          + "\n++++++++++++++++++ Aborting submission ++++++++++++++++++");
      clearupWhenSubmissionFails(jobID);
      return state;
    }

    // if persistent volume is requested, create a persistent volume claim
    if (SchedulerContext.persistentVolumeRequested(config)) {

      // if the job is not restarting from a checkpoint or
      // it is not restoring from nfs
      // create pvc
      if (!CheckpointingContext.startingFromACheckpoint(config)
          || !CheckpointingContext.isNfsUsed(config)) {
        boolean volumesSetup = initPersistentVolumeClaim(job);
        if (!volumesSetup) {
          clearupWhenSubmissionFails(jobID);
          return state;
        }
      }
    }

    // initialize StatefulSets for this job
    boolean statefulSetInitialized = initStatefulSets(job);
    if (!statefulSetInitialized) {
      clearupWhenSubmissionFails(jobID);
      return state;
    }

    // start the Job Master locally if requested
    if (JobMasterContext.jobMasterRunsInClient(config)) {
      boolean jobMasterCompleted = startJobMasterOnClient(job);
      if (!jobMasterCompleted) {
        LOG.log(Level.SEVERE, "JobMaster can not be started. "
            + "\n++++++++++++++++++ Aborting submission ++++++++++++++++++");
        clearupWhenSubmissionFails(jobID);
        return state;
      }
    }

    if (KubernetesContext.logInClient(config)) {
      jobLogger = new JobLogger(namespace, job);
      jobLogger.start();
    }

    state.setRequestGranted(true);
    return state;
  }

  private String getJobMasterIP(JobAPI.Job job) {
    if (JobMasterContext.jobMasterRunsInClient(config)) {
      try {
        return InetAddress.getLocalHost().getHostAddress();
      } catch (UnknownHostException e) {
        throw new RuntimeException("Exception when getting local host address: ", e);
      }
    }

    String jobMasterIP = PodWatchUtils.getJobMasterIpByWatchingPodToRunning(
        KubernetesContext.namespace(config), job.getJobId(), 100);

    if (jobMasterIP == null) {
      throw new RuntimeException("Job master is running in a separate pod, but "
          + "this worker can not get the job master IP address from Kubernetes master.\n"
          + "Job master address: " + jobMasterIP);
    }

    LOG.info("Job master address: " + jobMasterIP);
    return jobMasterIP;
  }

  /**
   * start the JobMaster locally on submitting client
   * this is a blocking call
   * it finishes after the job has completed
   */
  private boolean startJobMasterOnClient(JobAPI.Job job) {

    // get Dashboard IP address from dashboard service name
    String dashAddress = JobMasterContext.dashboardHost(config);

    // if dashboard address is a Kubernetes service name
    // get the IP address for the service name by querying Kubernetes master
    if (dashAddress.endsWith("svc.cluster.local")) {
      String dashIP = getDashboardIP(dashAddress);
      String dashURL = "http://" + dashIP;
      if (dashIP == null) {
        LOG.warning("Could not get Dashboard server IP address from dashboard service name: "
            + dashAddress + " will not connect to Dashboard. *****");
        dashURL = null;
      }
      config = JobMasterContext.updateDashboardHost(config, dashURL);
      LOG.info("Dashboard server HTTP URL: " + dashURL);
    }

    String hostAdress = RequestObjectBuilder.getJobMasterIP();

    JobMasterAPI.NodeInfo nodeInfo = NodeInfoUtils.createNodeInfo(hostAdress, null, null);
    K8sScaler k8sScaler = new K8sScaler(config, job, controller);
    JobMasterAPI.JobMasterState initialState = JobMasterAPI.JobMasterState.JM_STARTED;
    JobTerminator jobTerminator = new JobTerminator(config, controller);

    JobMaster jobMaster =
        new JobMaster(config, hostAdress, jobTerminator, job, nodeInfo, k8sScaler, initialState);
    jobMaster.addShutdownHook(true);
//    jobMaster.startJobMasterThreaded();
    try {
      jobMaster.startJobMasterBlocking();
    } catch (Twister2Exception e) {
      LOG.log(Level.SEVERE, e.getMessage(), e);
      return false;
    }

    return true;
  }

  /**
   * if dashboard address is a Kubernetes service name
   * get the IP address from service name by querying Kubernetes master
   */
  private String getDashboardIP(String dashAddress) {
    // first get dashboard service name from dashboard address

    // delete "http://" at the beginning if exist
    String dashServiceName = dashAddress;
    int prefixIndex = dashAddress.indexOf("://");
    if (prefixIndex != -1) {
      int serviceNameStartIndex = prefixIndex + 3;
      dashServiceName = dashAddress.substring(serviceNameStartIndex);
    }

    // remove all suffixes starting with dot
    int dotIndex = dashServiceName.indexOf(".");
    dashServiceName = dashServiceName.substring(0, dotIndex);

    return controller.getServiceIP(dashServiceName);
  }

  /**
   * check whether there are any entities with the same name on Kubernetes master or ZooKeeper
   * check the existence of all entities that will be created for this job
   * if any one of them exist, return false,
   * otherwise return true
   * <p>
   * for OpenMPI enabled jobs, check whether the Secret object exist on Kubernetes master
   * if it does not exist, return false
   * <p>
   * for fault tolerant jobs, check whether job znode exist in ZooKeeper server
   * if it exists, return false
   */
  private boolean checkEntitiesForJob(JobAPI.Job job) {

    String jobID = job.getJobId();

    // first check the existence of services that will be created for this job
    // if any one of them exists, return false
    String workersServiceName = KubernetesUtils.createServiceName(job.getJobId());
    ArrayList<String> serviceNames = new ArrayList<>();
    serviceNames.add(workersServiceName);

    String jobMasterServiceName = KubernetesUtils.createJobMasterServiceName(jobID);
    if (!JobMasterContext.jobMasterRunsInClient(config)) {
      serviceNames.add(jobMasterServiceName);
    }

    String existingService = controller.existServices(serviceNames);
    if (existingService != null) {
      LOG.severe("There is already a service with the name: " + existingService
          + "\nAnother job might be running. "
          + "\nFirst terminate that job or create a job with a different name."
          + "\n++++++++++++++++++ Aborting submission ++++++++++++++++++");
      return false;
    }

    boolean cmExists = controller.existConfigMap(jobID);
    if (cmExists) {
      LOG.severe("There is already a ConfigMap with the name: " + jobID
          + "\nAnother job might be running. "
          + "\nFirst terminate that job or create a job with a different name."
          + "\n++++++++++++++++++ Aborting submission ++++++++++++++++++");
      return false;
    }

    // if persistent volume is requested,
    // check whether a PersistentVolumeClaim with the same name exist
    if (SchedulerContext.persistentVolumeRequested(config)) {
      boolean pvcExists = controller.existPersistentVolumeClaim(jobID);

      // if it is restoring a job from nfs persistent directory, PVC must exist
      // otherwise, it must not exist
      if (CheckpointingContext.startingFromACheckpoint(config)
          && CheckpointingContext.isNfsUsed(config)) {

        if (!pvcExists) {
          LOG.severe("Previous PersistentVolumeClaim[" + jobID + "] does not exist. "
              + "It can not restore a job from NFS. "
              + "\n++++++++++++++++++ Aborting submission ++++++++++++++++++");
          return false;
        }

      } else if (pvcExists) {
        LOG.severe("Another job might be running. "
            + "\nFirst terminate that job or create a job with a different name."
            + "\n++++++++++++++++++ Aborting submission ++++++++++++++++++");
        return false;
      }
    }

    // check whether there is already a StatefulSet with the same name,
    ArrayList<String> statefulSetNames = new ArrayList<>();
    for (int i = 0; i < job.getComputeResourceList().size(); i++) {
      statefulSetNames.add(KubernetesUtils.createWorkersStatefulSetName(jobID, i));
    }

    if (!JobMasterContext.jobMasterRunsInClient(config)) {
      String jobMasterStatefulSetName = KubernetesUtils.createJobMasterStatefulSetName(jobID);
      statefulSetNames.add(jobMasterStatefulSetName);
    }

    boolean statefulSetExists = controller.existStatefulSets(statefulSetNames);

    if (statefulSetExists) {
      LOG.severe("First terminate the previously running job with the same name. "
          + "\nOr submit the job with a different job name"
          + "\n++++++++++++++++++ Aborting submission ++++++++++++++++++");
      return false;
    }

    // check the existence of Secret object on Kubernetes master
    // when OpenMPI is enabled, a Secret object has to be available in the cluster
    if (SchedulerContext.usingOpenMPI(config)) {
      String secretName = KubernetesContext.secretName(config);
      boolean secretExists = controller.existSecret(secretName);

      if (!secretExists) {
        LOG.severe("No Secret object is available in the cluster with the name: " + secretName
            + "\nFirst create this object or make that object created by your cluster admin."
            + "\n++++++++++++++++++ Aborting submission ++++++++++++++++++");
        return false;
      }
    }

    return true;
  }

  private boolean initServices(String jobID) {

    String workersServiceName = KubernetesUtils.createServiceName(jobID);
    String jobMasterServiceName = KubernetesUtils.createJobMasterServiceName(jobID);

    // if NodePort service is requested start one,
    // otherwise start a headless service
    V1Service serviceForWorkers = null;
    if (KubernetesContext.nodePortServiceRequested(config)) {
      serviceForWorkers = RequestObjectBuilder.createNodePortServiceObject();
    } else {
      serviceForWorkers = RequestObjectBuilder.createJobServiceObject();
    }

    boolean serviceCreated = controller.createService(serviceForWorkers);
    if (serviceCreated) {
      jobSubmissionStatus.setServiceForWorkersCreated(true);
    } else {
      LOG.severe("Following service could not be created: " + workersServiceName
          + "\n++++++++++++++++++ Aborting submission ++++++++++++++++++");
      return false;
    }

    // if Job Master runs as a separate pod, initialize a service for that
    if (!JobMasterContext.jobMasterRunsInClient(config)) {

      V1Service serviceForJobMaster = JobMasterRequestObject.createJobMasterHeadlessServiceObject();
//      V1Service serviceForJobMaster = JobMasterRequestObject.createJobMasterServiceObject();
      serviceCreated = controller.createService(serviceForJobMaster);
      if (serviceCreated) {
        jobSubmissionStatus.setServiceForJobMasterCreated(true);
      } else {
        LOG.severe("Following service could not be created: " + jobMasterServiceName
            + "\n++++++++++++++++++ Aborting submission ++++++++++++++++++");
        return false;
      }
    }

    return true;
  }

  /**
   * initialize a PersistentVolumeClaim on Kubernetes master
   */
  private boolean initPersistentVolumeClaim(JobAPI.Job job) {

    V1PersistentVolumeClaim pvc =
        RequestObjectBuilder.createPersistentVolumeClaimObject(job.getNumberOfWorkers());

    boolean claimCreated = controller.createPersistentVolumeClaim(pvc);
    if (claimCreated) {
      jobSubmissionStatus.setPvcCreated(true);
    } else {
      LOG.log(Level.SEVERE, "PersistentVolumeClaim could not be created. "
          + "\n++++++++++++++++++ Aborting submission ++++++++++++++++++");
      return false;
    }

    return true;
  }

  private String getNodeInfoList() {

    String encodedNodeInfoList = null;
    // if node locations will be retrieved from Kubernetes master
    if (!KubernetesContext.nodeLocationsFromConfig(config)) {
      // first get Node list and build encoded NodeInfoUtils Strings
      String rackLabelKey = KubernetesContext.rackLabelKeyForK8s(config);
      String dcLabelKey = KubernetesContext.datacenterLabelKeyForK8s(config);
      ArrayList<JobMasterAPI.NodeInfo> nodeInfoList =
          controller.getNodeInfo(rackLabelKey, dcLabelKey);
      encodedNodeInfoList = NodeInfoUtils.encodeNodeInfoList(nodeInfoList);
      LOG.fine("NodeInfo objects: size " + nodeInfoList.size()
          + "\n" + NodeInfoUtils.listToString(nodeInfoList));
    }

    return encodedNodeInfoList;
  }

  private boolean initStatefulSets(JobAPI.Job job) {

    // create StatefulSet for the job master
    if (!JobMasterContext.jobMasterRunsInClient(config)) {

      // create the StatefulSet object for this job
      V1StatefulSet jobMasterStatefulSet =
          JobMasterRequestObject.createStatefulSetObject();
      if (jobMasterStatefulSet == null) {
        return false;
      }

      boolean statefulSetCreated = controller.createStatefulSet(jobMasterStatefulSet);
      if (statefulSetCreated) {
        jobSubmissionStatus.addCreatedStatefulSetName(jobMasterStatefulSet.getMetadata().getName());
      } else {
        LOG.severe("Please run terminate job to clear up any artifacts from previous jobs."
            + "\n++++++++++++++++++ Aborting submission ++++++++++++++++++");
        return false;
      }
    }

    // create StatefulSets for workers
    for (int i = 0; i < job.getComputeResourceList().size(); i++) {

      JobAPI.ComputeResource computeResource = JobUtils.getComputeResource(job, i);
      if (computeResource == null) {
        LOG.severe("Something wrong with the job object. Can not get ComputeResource from job"
            + "\n++++++++++++++++++ Aborting submission ++++++++++++++++++");
        return false;
      }

      // create the StatefulSet object for this job
      V1StatefulSet statefulSet = RequestObjectBuilder.createStatefulSetForWorkers(computeResource);

      if (statefulSet == null) {
        return false;
      }

      boolean statefulSetCreated = controller.createStatefulSet(statefulSet);
      if (statefulSetCreated) {
        jobSubmissionStatus.addCreatedStatefulSetName(statefulSet.getMetadata().getName());
      } else {
        LOG.severe("Please run terminate job to clear up any artifacts from previous jobs."
            + "\n++++++++++++++++++ Aborting submission ++++++++++++++++++");
        return false;
      }
    }

    return true;
  }

  /**
   * check whether configuration parameters are accurate
   */
  private boolean configParametersOK(JobAPI.Job job) {

    // check whether the uploader is supported
    List<String> uploaders = Arrays.asList(
        "edu.iu.dsc.tws.rsched.uploaders.k8s.K8sUploader",
        "edu.iu.dsc.tws.rsched.uploaders.s3.S3Uploader");

    if (!uploaders.contains(SchedulerContext.uploaderClass(config))) {
      LOG.log(Level.SEVERE, String.format("Provided uploader is not supported: "
          + SchedulerContext.uploaderClass(config)
          + ". \nSupported uploaders: " + uploaders
          + "\n++++++++++++++++++ Aborting submission ++++++++++++++++++"));
      return false;
    }

    // when OpenMPI is enabled, all pods need to have equal numbers workers
    // we check whether all workersPerPod values are equal to first ComputeResource workersPerPod
    if (SchedulerContext.usingOpenMPI(config)) {
      int workersPerPod = job.getComputeResource(0).getWorkersPerPod();
      for (int i = 1; i < job.getComputeResourceList().size(); i++) {
        if (workersPerPod != job.getComputeResource(i).getWorkersPerPod()) {
          LOG.log(Level.SEVERE, String.format("When OpenMPI is used, all workersPerPod values "
              + "in ComputeResources have to be the same. "
              + "\n++++++++++++++++++ Aborting submission ++++++++++++++++++"));
          return false;
        }
      }
    }

    // if statically binding requested, number for CPUs per worker has to be an integer
    if (KubernetesContext.bindWorkerToCPU(config)) {
      for (JobAPI.ComputeResource computeResource : job.getComputeResourceList()) {
        double cpus = computeResource.getCpu();
        if (cpus % 1 != 0) {
          LOG.log(Level.SEVERE, String.format("When %s is true, the value of cpu has to be an int"
                  + " cpu= " + cpus
                  + "\n++++++++++++++++++ Aborting submission ++++++++++++++++++",
              KubernetesContext.K8S_BIND_WORKER_TO_CPU));
          return false;
        }
      }
    }

    // when worker to nodes mapping is requested
    // if the operator is either Exists or DoesNotExist,
    // values list must be empty
    if (KubernetesContext.workerToNodeMapping(config)) {
      String operator = KubernetesContext.workerMappingOperator(config);
      List<String> values = KubernetesContext.workerMappingValues(config);
      if (("Exists".equalsIgnoreCase(operator) || "DoesNotExist".equalsIgnoreCase(operator))
          && values != null && values.size() != 0) {
        LOG.log(Level.SEVERE, String.format("When the value of %s is either Exists or DoesNotExist"
                + "\n%s list must be empty. Current content of this list: " + values
                + "\n++++++++++++++++++ Aborting submission ++++++++++++++++++",
            KubernetesContext.K8S_WORKER_MAPPING_OPERATOR,
            KubernetesContext.K8S_WORKER_MAPPING_VALUES));
        return false;
      }
    }

    // When nodePort service requested, WORKERS_PER_POD value must be 1
    if (KubernetesContext.nodePortServiceRequested(config)) {
      for (JobAPI.ComputeResource computeResource : job.getComputeResourceList()) {
        if (computeResource.getWorkersPerPod() != 1) {
          LOG.log(Level.SEVERE, "workersPerPod value must be 1, when starting NodePort service. "
              + "Please change the config value and resubmit the job"
              + "\n++++++++++++++++++ Aborting submission ++++++++++++++++++");
          return false;
        }
      }
    }

    return true;
  }

  /**
   * Close up any resources
   */
  @Override
  public void close() {
    if (KubernetesContext.logInClient(config)
        || RequestObjectBuilder.uploadMethod.equals("client-to-pods")) {
      return;
    }

    controller.close();
  }

  /**
   * We clear up the resources that we created during the job submission process
   * we don't delete the resources that may be left from previous jobs
   * They have to be deleted explicitly by the user
   */
  private void clearupWhenSubmissionFails(String jobID) {

    LOG.info("Will clear up any resources created during the job submission process.");

    // first delete the service objects
    // delete the job service
    if (jobSubmissionStatus.isServiceForWorkersCreated()) {
      String serviceName = KubernetesUtils.createServiceName(jobID);
      controller.deleteService(serviceName);
    }

    // delete the job master service
    if (jobSubmissionStatus.isServiceForJobMasterCreated()) {
      String jobMasterServiceName = KubernetesUtils.createJobMasterServiceName(jobID);
      controller.deleteService(jobMasterServiceName);
    }

    // delete the job master service
    if (jobSubmissionStatus.isConfigMapCreated()) {
      controller.deleteConfigMap(jobID);
    }

    // delete created StatefulSet objects
    ArrayList<String> ssNameLists = jobSubmissionStatus.getCreatedStatefulSetNames();
    for (String ssName : ssNameLists) {
      controller.deleteStatefulSet(ssName);
    }

    // delete the persistent volume claim
    if (jobSubmissionStatus.isPvcCreated()) {
      boolean claimDeleted = controller.deletePersistentVolumeClaim(jobID);
    }
  }

  /**
   * Kill the Kubernetes Job
   */
  @Override
  public boolean killJob(String jobID) {
    // get the list of resources in the job from k8s master:
    //   services, statefulsets, cm, pvc
    // post a parameter to configmap to signall job killing to JobMaster
    // periodically check whether all resources are deleted
    // if all resources are not deleted on the time limit,
    // list undeleted resources and return false

    // get job resources from Kubernetes master
    ClientJobKillHandler killHandler = new ClientJobKillHandler(jobID, controller);
    killHandler.getInitialState();

    // signall kill to JobMaster
    controller.addConfigMapParam(jobID, "KILL_JOB", "true");
    LOG.info("Waiting JobMaster to delete job resources........");

    // check whether they are all killed
    final long maxWaitTime = 15000; // wait 15 seconds for resources to be deleted by Job Master
    final long start = System.currentTimeMillis();
    final long logInterval = 3000; // log every 5 seconds
    int logCount = 1;
    long duration;

    do {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
      }
      duration = System.currentTimeMillis() - start;

      if (duration > logInterval * logCount) {
        logCount++;
        LOG.info("Still waiting for the job master to delete job resources.....");
      }

    } while (!killHandler.allDeleted() && duration < maxWaitTime);

    // if not all resources are deleted in the time limit
    if (duration > maxWaitTime) {
      LOG.warning("Following job resources are not deleted by Job Master in "
          + (maxWaitTime / 1000) + " seconds. We will delete them now: " + System.lineSeparator()
          + killHandler.getUndeletedResources());
      return killHandler.deleteUndeletedResources();
    }

    LOG.info("Job is killed in " + duration + "ms");
    return true;
  }

}
