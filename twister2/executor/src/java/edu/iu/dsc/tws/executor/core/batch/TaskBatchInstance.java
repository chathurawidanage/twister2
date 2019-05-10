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
package edu.iu.dsc.tws.executor.core.batch;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import edu.iu.dsc.tws.common.config.Config;
import edu.iu.dsc.tws.executor.api.INodeInstance;
import edu.iu.dsc.tws.executor.api.IParallelOperation;
import edu.iu.dsc.tws.executor.api.ISync;
import edu.iu.dsc.tws.executor.core.DefaultOutputCollection;
import edu.iu.dsc.tws.executor.core.ExecutorContext;
import edu.iu.dsc.tws.executor.core.TaskContextImpl;
import edu.iu.dsc.tws.task.api.Closable;
import edu.iu.dsc.tws.task.api.ICompute;
import edu.iu.dsc.tws.task.api.IMessage;
import edu.iu.dsc.tws.task.api.INode;
import edu.iu.dsc.tws.task.api.OutputCollection;
import edu.iu.dsc.tws.task.api.TaskContext;
import edu.iu.dsc.tws.tsched.spi.taskschedule.TaskSchedulePlan;

/**
 * The class represents the instance of the executing task
 */
public class TaskBatchInstance implements INodeInstance, ISync {
  /**
   * The actual task executing
   */
  private ICompute task;

  /**
   * All the inputs will come through a single queue, otherwise we need to look
   * at different queues for messages
   */
  private BlockingQueue<IMessage> inQueue;

  /**
   * Output will go throuh a single queue
   */
  private BlockingQueue<IMessage> outQueue;

  /**
   * The configuration
   */
  private Config config;

  /**
   * The output collection to be used
   */
  private OutputCollection outputCollection;

  /**
   * The globally unique task id
   */
  private int globalTaskId;

  /**
   * Task id
   */
  private int taskId;

  /**
   * Task index that goes from 0 to parallism - 1
   */
  private int taskIndex;

  /**
   * Number of parallel tasks
   */
  private int parallelism;

  /**
   * Name of the task
   */
  private String taskName;

  /**
   * Node configurations
   */
  private Map<String, Object> nodeConfigs;

  /**
   * Parallel operations
   */
  private Map<String, IParallelOperation> outParOps = new HashMap<>();

  /**
   * Inward parallel operations
   */
  private Map<String, IParallelOperation> inParOps = new HashMap<>();

  /**
   * The worker id
   */
  private int workerId;

  /**
   * The instance state
   */
  private InstanceState state = new InstanceState(InstanceState.INIT);

  /**
   * Output edges
   */
  private Map<String, String> outputEdges;

  /**
   * Input edges
   */
  private Map<String, String> inputEdges;

  /**
   * Task context
   */
  private TaskContext taskContext;

  /**
   * The low watermark for queued messages
   */
  private int lowWaterMark;

  /**
   * The high water mark for messages
   */
  private int highWaterMark;

  /**
   * The task schedule
   */
  private TaskSchedulePlan taskSchedule;

  /**
   * Keep track of syncs received
   */
  private Set<String> syncReceived = new HashSet<>();

  public TaskBatchInstance(ICompute task, BlockingQueue<IMessage> inQueue,
                           BlockingQueue<IMessage> outQueue, Config config, String tName,
                           int taskId, int globalTaskId, int tIndex, int parallel,
                           int wId, Map<String, Object> cfgs,
                           Map<String, String> inEdges, Map<String, String> outEdges,
                           TaskSchedulePlan taskSchedule) {
    this.task = task;
    this.inQueue = inQueue;
    this.outQueue = outQueue;
    this.config = config;
    this.globalTaskId = globalTaskId;
    this.taskId = taskId;
    this.taskIndex = tIndex;
    this.parallelism = parallel;
    this.taskName = tName;
    this.nodeConfigs = cfgs;
    this.workerId = wId;
    this.inputEdges = inEdges;
    this.outputEdges = outEdges;
    this.lowWaterMark = ExecutorContext.instanceQueueLowWaterMark(config);
    this.highWaterMark = ExecutorContext.instanceQueueHighWaterMark(config);
    this.taskSchedule = taskSchedule;
  }

  public void prepare(Config cfg) {
    outputCollection = new DefaultOutputCollection(outQueue);
    taskContext = new TaskContextImpl(taskIndex, taskId, globalTaskId, taskName, parallelism,
        workerId, outputCollection, nodeConfigs, inputEdges, outputEdges, taskSchedule);
    task.prepare(cfg, taskContext);
  }

  public void registerOutParallelOperation(String edge, IParallelOperation op) {
    outParOps.put(edge, op);
  }

  public void registerInParallelOperation(String edge, IParallelOperation op) {
    inParOps.put(edge, op);
  }

  @Override
  public boolean execute() {
    // we started the executio
    if (state.isSet(InstanceState.INIT) && state.isNotSet(InstanceState.EXECUTION_DONE)) {
      while (!inQueue.isEmpty() && outQueue.size() < lowWaterMark) {
        IMessage m = inQueue.poll();
        task.execute(m);
        state.addState(InstanceState.EXECUTING);
      }

      // for compute we don't have to have the context done as when the inputs finish and execution
      // is done, we are done executing
      // progress in communication
      boolean needsFurther = progressCommunication(inParOps);
      // if we no longer needs to progress comm and input is empty
      if (state.isSet(InstanceState.EXECUTING) && inQueue.isEmpty()) {
        state.addState(InstanceState.EXECUTION_DONE);
      }
    }

    // now check the output queue
    while (!outQueue.isEmpty()) {
      IMessage message = outQueue.peek();
      if (message != null) {
        String edge = message.edge();

        // invoke the communication operation
        IParallelOperation op = outParOps.get(edge);
        int flags = 0;
        if (op.send(globalTaskId, message, flags)) {
          outQueue.poll();
        } else {
          // no point progressing further
          break;
        }
      }
    }

    // if execution is done and outqueue is emput, we have put everything to communication
    if (state.isSet(InstanceState.EXECUTION_DONE) && outQueue.isEmpty()
        && state.isNotSet(InstanceState.OUT_COMPLETE)) {
      for (IParallelOperation op : outParOps.values()) {
        op.finish(globalTaskId);
      }
      state.addState(InstanceState.OUT_COMPLETE);
    }

    // lets progress the communication
    boolean needsFurther = progressCommunication(outParOps);
    // after we have put everything to communication and no progress is required, lets finish
    if (state.isSet(InstanceState.OUT_COMPLETE)) {
      state.addState(InstanceState.SENDING_DONE);
    }
    return !state.isSet(InstanceState.SENDING_DONE);
  }

  public boolean sync(String edge, byte[] value) {
    syncReceived.add(edge);
    if (syncReceived.equals(inParOps.keySet())) {
      state.addState(InstanceState.SYNCED);
      syncReceived.clear();
    }
    return true;
  }

  /**
   * Progress the communication and return weather we need to further progress
   *
   * @return true if further progress is needed
   */
  public boolean progressCommunication(Map<String, IParallelOperation> ops) {
    boolean allDone = true;
    for (Map.Entry<String, IParallelOperation> e : ops.entrySet()) {
      if (e.getValue().progress()) {
        allDone = false;
      }
    }
    return !allDone;
  }

  @Override
  public boolean isComplete() {
    boolean complete = true;
    for (Map.Entry<String, IParallelOperation> e : outParOps.entrySet()) {
      if (!e.getValue().isComplete()) {
        complete = false;
      }
    }

    for (Map.Entry<String, IParallelOperation> e : inParOps.entrySet()) {
      if (!e.getValue().isComplete()) {
        complete = false;
      }
    }

    return complete;
  }

  @Override
  public int getId() {
    return globalTaskId;
  }

  @Override
  public INode getNode() {
    return task;
  }

  @Override
  public void close() {
    if (task instanceof Closable) {
      ((Closable) task).close();
    }
  }

  @Override
  public void reset() {
    this.taskContext.reset();
    if (task instanceof Closable) {
      ((Closable) task).refresh();
    }
    state = new InstanceState(InstanceState.INIT);
  }

  public BlockingQueue<IMessage> getInQueue() {
    return inQueue;
  }

  public BlockingQueue<IMessage> getOutQueue() {
    return outQueue;
  }
}
