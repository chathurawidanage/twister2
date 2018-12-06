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
package edu.iu.dsc.tws.examples.internal.jobmaster;

import java.net.InetAddress;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.iu.dsc.tws.api.job.Twister2Job;
import edu.iu.dsc.tws.common.config.Config;
import edu.iu.dsc.tws.common.config.ConfigLoader;
import edu.iu.dsc.tws.common.controller.IWorkerController;
import edu.iu.dsc.tws.common.exceptions.TimeoutException;
import edu.iu.dsc.tws.common.resource.NodeInfoUtils;
import edu.iu.dsc.tws.common.resource.WorkerInfoUtils;
import edu.iu.dsc.tws.master.JobMasterContext;
import edu.iu.dsc.tws.master.client.JMWorkerController;
import edu.iu.dsc.tws.master.client.JobMasterClient;
import edu.iu.dsc.tws.proto.jobmaster.JobMasterAPI;
import edu.iu.dsc.tws.proto.system.job.JobAPI;

public final class JobMasterClientExample {
  private static final Logger LOG = Logger.getLogger(JobMasterClientExample.class.getName());

  private JobMasterClientExample() { }

  /**
   * a test class to run JobMasterClient
   * First, a JobMaster instance should be started on a machine
   * This client should connect to that server
   *
   * NumberOfWorkers to join must be given as a command line parameter
   * Each worker waits for all workers to join
   *
   * When all workers joined, they get the full worker list
   * Then, each worker sends a barrier message
   * Then, each worker sends a completed message and closes
   *
   * @param args
   */
  public static void main(String[] args) {

    // we assume that the twister2Home is the current directory
    String configDir = "conf/kubernetes/";
    String twister2Home = Paths.get("").toAbsolutePath().toString();
    Config config = ConfigLoader.loadConfig(twister2Home, configDir);
    config = updateConfig(config);
    LOG.info("Loaded: " + config.size() + " parameters from configuration directory: " + configDir);

    Twister2Job twister2Job = Twister2Job.loadTwister2Job(config, null);
    JobAPI.Job job = twister2Job.serialize();

    simulateClient(config, job);
  }

  /**
   * a method to simulate JobMasterClient running in workers
   */
  public static void simulateClient(Config config, JobAPI.Job job) {

    InetAddress workerIP = JMWorkerController.convertStringToIP("localhost");
    int workerPort = 10000 + (int) (Math.random() * 10000);

    JobMasterAPI.NodeInfo nodeInfo = NodeInfoUtils.createNodeInfo("node.ip", "rack01", null);

    int workerTempID = 0;
    JobAPI.ComputeResource computeResource = job.getComputeResource(0);

    JobMasterAPI.WorkerInfo workerInfo = WorkerInfoUtils.createWorkerInfo(
        workerTempID, workerIP.getHostAddress(), workerPort, nodeInfo, computeResource);

    String jobMasterAddress = "localhost";
    JobMasterClient client = new JobMasterClient(config, workerInfo, jobMasterAddress);
    Thread clientThread = client.startThreaded();
    if (clientThread == null) {
      LOG.severe("JobMasterClient can not initialize. Exiting ...");
      return;
    }

//    client.registerWorker();

    IWorkerController workerController = client.getJMWorkerController();

    // wait up to 2sec
    sleeeep((long) (Math.random() * 2000));

    client.sendWorkerRunningMessage();

    List<JobMasterAPI.WorkerInfo> workerList = workerController.getJoinedWorkers();
    LOG.info(WorkerInfoUtils.workerListAsString(workerList));

    try {
      workerList = workerController.getAllWorkers();
    } catch (TimeoutException timeoutException) {
      LOG.log(Level.SEVERE, timeoutException.getMessage(), timeoutException);
      return;
    }
    LOG.info(WorkerInfoUtils.workerListAsString(workerList));

    // wait up to 10sec
    sleeeep((long) (Math.random() * 10000));
    try {
      client.getJMWorkerController().waitOnBarrier();
      LOG.info("All workers reached the barrier. Proceeding.");
    } catch (TimeoutException timeoutException) {
      LOG.log(Level.SEVERE, timeoutException.getMessage(), timeoutException);
    }

    // wait up to 3sec
    sleeeep((long) (Math.random() * 3000));

    client.sendWorkerCompletedMessage();

    client.close();

    System.out.println("Client has finished the computation. Client exiting.");
  }

  /**
   * construct a Config object
   * @return
   */
  public static Config updateConfig(Config config) {
    return Config.newBuilder()
        .putAll(config)
        .put(JobMasterContext.JOB_MASTER_ASSIGNS_WORKER_IDS, true)
        .build();
  }

  public static void sleeeep(long duration) {

    LOG.info("Sleeping " + duration + "ms............");

    try {
      Thread.sleep(duration);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public static void printUsage() {
    LOG.info("Usage:\n"
        + "java JobMasterClientExample numberOfWorkers");
  }

}
