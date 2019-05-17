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
package edu.iu.dsc.tws.executor.core.streaming;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.iu.dsc.tws.checkpointmanager.utils.CheckpointContext;
import edu.iu.dsc.tws.common.config.Config;
import edu.iu.dsc.tws.comms.api.MessageFlags;
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
import edu.iu.dsc.tws.task.api.checkpoint.Checkpointable;
import edu.iu.dsc.tws.task.api.checkpoint.Snapshot;
import edu.iu.dsc.tws.tsched.spi.taskschedule.TaskSchedulePlan;

/**
 * The class represents the instance of the executing task
 */
public class TaskStreamingInstance implements INodeInstance, ISync {
  /**
   * The actual task executing
   */
  private ICompute task;

  private static final Logger LOG = Logger.getLogger(TaskStreamingInstance.class.getName());

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
   * The task id
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
   * The low watermark for queued messages
   */
  private int lowWaterMark;

  /**
   * The high water mark for messages
   */
  private int highWaterMark;

  /**
   * Output edges
   */
  private Map<String, String> outputEdges;
  private TaskSchedulePlan taskSchedule;

  /**
   * Input edges
   */
  private Map<String, String> inputEdges;

  public TaskStreamingInstance(ICompute task, BlockingQueue<IMessage> inQueue,
                               BlockingQueue<IMessage> outQueue, Config config, String tName,
                               int taskId, int globalTaskId, int tIndex,
                               int parallel, int wId, Map<String, Object> cfgs,
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
    this.lowWaterMark = ExecutorContext.instanceQueueLowWaterMark(config);
    this.highWaterMark = ExecutorContext.instanceQueueHighWaterMark(config);
    this.inputEdges = inEdges;
    this.outputEdges = outEdges;
    this.taskSchedule = taskSchedule;
    if (CheckpointContext.getCheckpointRecovery(config)) {
      try {
        LocalStreamingStateBackend fsStateBackend = new LocalStreamingStateBackend();
        Snapshot snapshot = (Snapshot) fsStateBackend.readFromStateBackend(config,
            taskId, workerId);
        ((Checkpointable) this.task).restoreSnapshot(snapshot);
      } catch (Exception e) {
        LOG.log(Level.WARNING, "Could not read checkpoint", e);
      }
    }
  }

  public void prepare(Config cfg) {
    outputCollection = new DefaultOutputCollection(outQueue);
    task.prepare(cfg, new TaskContextImpl(taskIndex, taskId, globalTaskId, taskName, parallelism,
        workerId, outputCollection, nodeConfigs, inputEdges, outputEdges, taskSchedule));
  }

  public void registerOutParallelOperation(String edge, IParallelOperation op) {
    outParOps.put(edge, op);
  }

  public void registerInParallelOperation(String edge, IParallelOperation op) {
    inParOps.put(edge, op);
  }

  /**
   * Execution Method calls the SourceTasks run method to get context
   **/
  public boolean execute() {
    // execute if there are incoming messages
    while (!inQueue.isEmpty() && outQueue.size() < lowWaterMark) {
      IMessage message = inQueue.poll();
      if (message != null) {
        if ((message.getFlag() & MessageFlags.SYNC_BARRIER) != MessageFlags.SYNC_BARRIER) {
          task.execute(message);
        } else {
          if (storeSnapshot()) {
            outQueue.add(message);
          }
        }
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
        if ((message.getFlag() & MessageFlags.SYNC_BARRIER) == MessageFlags.SYNC_BARRIER) {
          message.setFlag(MessageFlags.SYNC_BARRIER);
          flags = MessageFlags.SYNC_BARRIER;
        }
        // if we successfully send remove
        if (op.send(globalTaskId, message, flags)) {
          outQueue.poll();
        } else {
          break;
        }
      }
    }

    for (Map.Entry<String, IParallelOperation> e : outParOps.entrySet()) {
      e.getValue().progress();
    }

    for (Map.Entry<String, IParallelOperation> e : inParOps.entrySet()) {
      e.getValue().progress();
    }

    return true;
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

  public BlockingQueue<IMessage> getInQueue() {
    return inQueue;
  }

  public BlockingQueue<IMessage> getOutQueue() {
    return outQueue;
  }

  public boolean storeSnapshot() {
    try {
      LocalStreamingStateBackend fsStateBackend = new LocalStreamingStateBackend();
      fsStateBackend.writeToStateBackend(config, taskId, workerId, (Checkpointable) task, 1);
      return true;
    } catch (Exception e) {
      LOG.log(Level.WARNING, " Could not store checkpoint", e);
      return false;
    }
  }

  @Override
  public boolean sync(String edge, byte[] value) {
    return true;
  }
}
