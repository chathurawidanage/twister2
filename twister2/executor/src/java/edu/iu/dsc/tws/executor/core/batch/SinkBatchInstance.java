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
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import edu.iu.dsc.tws.common.config.Config;
import edu.iu.dsc.tws.executor.api.INodeInstance;
import edu.iu.dsc.tws.executor.api.IParallelOperation;
import edu.iu.dsc.tws.executor.comm.tasks.batch.SinkBatchTask;
import edu.iu.dsc.tws.task.api.IMessage;
import edu.iu.dsc.tws.task.api.ISink;
import edu.iu.dsc.tws.task.api.TaskContext;

public class SinkBatchInstance  implements INodeInstance {
  /**
   * The actual batchTask executing
   */
  private ISink batchTask;

  /**
   * All the inputs will come through a single queue, otherwise we need to look
   * at different queues for messages
   */
  private BlockingQueue<IMessage> batchInQueue;

  /**
   * Inward parallel operations
   */
  private Map<String, IParallelOperation> batchInParOps = new HashMap<>();

  /**
   * The configuration
   */
  private Config config;

  /**
   * The globally unique batchTask id
   */
  private int batchTaskId;

  /**
   * Task index that goes from 0 to parallism - 1
   */
  private int batchTaskIndex;

  /**
   * Number of parallel tasks
   */
  private int parallelism;

  /**
   * Name of the batchTask
   */
  private String taskName;

  /**
   * Node configurations
   */
  private Map<String, Object> nodeConfigs;

  /**
   * The worker id
   */
  private int workerId;

  public SinkBatchInstance(ISink batchTask, BlockingQueue<IMessage> batchInQueue, Config config,
                           int tId, int tIndex, int parallel, int wId, Map<String, Object> cfgs) {
    this.batchTask = batchTask;
    this.batchInQueue = batchInQueue;
    this.config = config;
    this.batchTaskId = tId;
    this.batchTaskIndex = tIndex;
    this.parallelism = parallel;
    this.nodeConfigs = cfgs;
    this.workerId = wId;

  }

  public void prepare() {
    batchTask.prepare(config, new TaskContext(batchTaskIndex, batchTaskId, taskName,
        parallelism, workerId, nodeConfigs));
  }

  @Override
  public void interrupt() {

  }

  public void execute() {

    //SinkBatchTask sinkBatchTask = (SinkBatchTask) batchTask;

    while (!batchInQueue.isEmpty()) {
      IMessage m = batchInQueue.poll();
      batchTask.execute(m);
    }

    for (Map.Entry<String, IParallelOperation> e : batchInParOps.entrySet()) {
      e.getValue().progress();
    }
  }

  public void registerInParallelOperation(String edge, IParallelOperation op) {
    batchInParOps.put(edge, op);
  }

  public BlockingQueue<IMessage> getBatchInQueue() {
    return batchInQueue;
  }
}
