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
package edu.iu.dsc.tws.tsched.taskscheduler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import edu.iu.dsc.tws.api.compute.exceptions.TaskSchedulerException;
import edu.iu.dsc.tws.api.compute.graph.ComputeGraph;
import edu.iu.dsc.tws.api.compute.schedule.ITaskScheduler;
import edu.iu.dsc.tws.api.compute.schedule.elements.TaskInstancePlan;
import edu.iu.dsc.tws.api.compute.schedule.elements.TaskSchedulePlan;
import edu.iu.dsc.tws.api.compute.schedule.elements.WorkerPlan;
import edu.iu.dsc.tws.api.compute.schedule.elements.WorkerSchedulePlan;
import edu.iu.dsc.tws.api.config.Config;
import edu.iu.dsc.tws.api.exceptions.Twister2RuntimeException;
import edu.iu.dsc.tws.tsched.spi.common.TaskSchedulerContext;

/**
 * This class invokes the appropriate task schedulers based on the 'streaming' or 'batch' task types
 * and scheduling modes 'roundrobin', 'firstfit', and 'datalocality'.
 */
public class TaskScheduler implements ITaskScheduler {

  private static final Logger LOG = Logger.getLogger(TaskScheduler.class.getName());

  private Config config;

  private ComputeGraph computeGraph;

  private ComputeGraph[] computeGraphs;

  private WorkerPlan workerPlan;

  private String schedulingType;

  private int workerId;

  @Override
  public void initialize(Config cfg) {
    this.config = cfg;
  }

  @Override
  public void initialize(Config cfg, int workerid) {
    this.config = cfg;
    this.workerId = workerid;
  }

  /**
   * This is the base method for the task scheduler to invoke the appropriate task schedulers
   * either "batch" or "streaming" based on the task type.
   */
  @Override
  public TaskSchedulePlan schedule(ComputeGraph graph, WorkerPlan plan) {

    this.computeGraph = graph;
    this.workerPlan = plan;

    TaskSchedulePlan taskSchedulePlan = null;

    if ("STREAMING".equals(graph.getOperationMode().toString())) {
      taskSchedulePlan = scheduleStreamingTask();
    } else if ("BATCH".equals(graph.getOperationMode().toString())) {
      taskSchedulePlan = scheduleBatchTask();
    }
    return taskSchedulePlan;
  }

  /**
   * This is the base method for the task scheduler to invoke the appropriate task schedulers
   * either "batch" or "streaming" based on the task type.
   */
  public Map<String, TaskSchedulePlan> schedule(WorkerPlan plan, ComputeGraph... graphs) {

    this.computeGraphs = graphs;
    this.workerPlan = plan;

    Map<String, TaskSchedulePlan> schedulePlanMap = scheduleBatchGraphs();
    return schedulePlanMap;
  }

  /**
   * This method invokes the appropriate streaming task schedulers based on the scheduling mode
   * specified in the task configuration by the user or else from the default configuration value.
   */
  private TaskSchedulePlan scheduleStreamingTask() {

    if (config.getStringValue("SchedulingMode") != null) {
      this.schedulingType = config.getStringValue("SchedulingMode");
    } else {
      this.schedulingType = TaskSchedulerContext.streamingTaskSchedulingMode(config);
    }
    if (workerId == 0) {
      LOG.fine("Task Scheduling Type:" + schedulingType + "(" + "streaming task" + ")");
    }
    return generateTaskSchedulePlan(TaskSchedulerContext.streamingTaskSchedulingClass(config));
  }

  /**
   * This method invokes the appropriate batch task schedulers based on the scheduling mode
   * specified in the task configuration by the user or else from the default configuration value.
   *
   * @return Task Schedule Plan
   */
  private TaskSchedulePlan scheduleBatchTask() {
    if (config.getStringValue("SchedulingMode") != null) {
      this.schedulingType = config.getStringValue("SchedulingMode");
    } else {
      this.schedulingType = TaskSchedulerContext.batchTaskSchedulingMode(config);
    }
    if (workerId == 0) {
      LOG.fine("Task Scheduling Type:" + schedulingType + "(" + "batch task" + ")");
    }
    return generateTaskSchedulePlan(TaskSchedulerContext.batchTaskSchedulingClass(config));
  }

  private Map<String, TaskSchedulePlan> scheduleBatchGraphs() {
    if (config.getStringValue("SchedulingMode") != null) {
      this.schedulingType = config.getStringValue("SchedulingMode");
    } else {
      this.schedulingType = TaskSchedulerContext.batchTaskSchedulingMode(config);
    }
    if (workerId == 0) {
      LOG.fine("Task Scheduling Type:" + schedulingType + "(" + "batch task" + ")");
    }
    return generateTaskSchedulePlans(TaskSchedulerContext.batchTaskSchedulingClass(config));
  }


  private TaskSchedulePlan generateTaskSchedulePlan(String className) {
    Class<?> taskSchedulerClass;
    Method method;
    TaskSchedulePlan taskSchedulePlan;

    try {
      taskSchedulerClass = getClass().getClassLoader().loadClass(className);
      Object newInstance = taskSchedulerClass.newInstance();
      method = taskSchedulerClass.getMethod("initialize", new Class<?>[]{Config.class});
      method.invoke(newInstance, config);
      method = taskSchedulerClass.getMethod("schedule",
          new Class<?>[]{ComputeGraph.class, WorkerPlan.class});
      taskSchedulePlan = (TaskSchedulePlan) method.invoke(newInstance, computeGraph,
          workerPlan);
    } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException
        | InstantiationException | ClassNotFoundException | TaskSchedulerException e) {
      throw new Twister2RuntimeException(e);
    }

    if (taskSchedulePlan != null) {
      Map<Integer, WorkerSchedulePlan> containersMap
          = taskSchedulePlan.getContainersMap();
      for (Map.Entry<Integer, WorkerSchedulePlan> entry : containersMap.entrySet()) {
        Integer integer = entry.getKey();
        WorkerSchedulePlan workerSchedulePlan = entry.getValue();
        Set<TaskInstancePlan> containerPlanTaskInstances
            = workerSchedulePlan.getTaskInstances();
        LOG.fine("Task Details for Container Id:" + integer);
        for (TaskInstancePlan ip : containerPlanTaskInstances) {
          LOG.fine("Task Id:" + ip.getTaskId()
              + "\tTask Index" + ip.getTaskIndex()
              + "\tTask Name:" + ip.getTaskName());
        }
      }
    }
    return taskSchedulePlan;
  }

  private Map<String, TaskSchedulePlan> generateTaskSchedulePlans(String className) {
    Class<?> taskSchedulerClass;
    Method method;
    Map<String, TaskSchedulePlan> taskSchedulePlanMap;
    try {
      taskSchedulerClass = getClass().getClassLoader().loadClass(className);
      Object newInstance = taskSchedulerClass.newInstance();
      method = taskSchedulerClass.getMethod("initialize", new Class<?>[]{Config.class});
      method.invoke(newInstance, config);
      method = taskSchedulerClass.getMethod("schedule",
          new Class<?>[]{WorkerPlan.class, ComputeGraph[].class});
      taskSchedulePlanMap = (Map<String, TaskSchedulePlan>) method.invoke(
          newInstance, new Object[]{workerPlan, computeGraphs});
    } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException
        | InstantiationException | ClassNotFoundException | TaskSchedulerException e) {
      throw new Twister2RuntimeException(e);
    }
    return taskSchedulePlanMap;
  }
}
