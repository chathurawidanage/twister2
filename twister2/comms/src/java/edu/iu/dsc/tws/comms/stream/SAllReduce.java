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
package edu.iu.dsc.tws.comms.stream;

import java.util.Set;

import edu.iu.dsc.tws.api.comms.BaseOperation;
import edu.iu.dsc.tws.api.comms.CommunicationContext;
import edu.iu.dsc.tws.api.comms.Communicator;
import edu.iu.dsc.tws.api.comms.LogicalPlan;
import edu.iu.dsc.tws.api.comms.ReduceFunction;
import edu.iu.dsc.tws.api.comms.SingularReceiver;
import edu.iu.dsc.tws.api.comms.messaging.types.MessageType;
import edu.iu.dsc.tws.api.comms.packing.MessageSchema;
import edu.iu.dsc.tws.comms.dfw.AllReduce;
import edu.iu.dsc.tws.comms.utils.LogicalPlanBuilder;

/**
 * Streaming ALLReduce Operation
 */
public class SAllReduce extends BaseOperation {
  /**
   * Construct a Streaming AllReduce operation
   *
   * @param comm the communicator
   * @param plan task plan
   * @param sources source tasks
   * @param targets target tasks
   * @param rcvr receiver
   * @param dataType data type
   */
  public SAllReduce(Communicator comm, LogicalPlan plan,
                    Set<Integer> sources, Set<Integer> targets, MessageType dataType,
                    ReduceFunction fnc, SingularReceiver rcvr, int reduceEdgeId,
                    int bcastEdgeId, MessageSchema messageSchema) {
    super(comm, true, CommunicationContext.ALLREDUCE);
    if (sources.size() == 0) {
      throw new IllegalArgumentException("The sources cannot be empty");
    }

    if (targets.size() == 0) {
      throw new IllegalArgumentException("The destination cannot be empty");
    }

    int middleTask = comm.nextId();
    int firstSource = sources.iterator().next();
    plan.addLogicalIdToWorker(plan.getWorkerForForLogicalId(firstSource), middleTask);

    op = new AllReduce(comm.getConfig(), comm.getChannel(), plan, sources, targets,
        middleTask, fnc, rcvr, dataType, reduceEdgeId, bcastEdgeId, true, messageSchema);
  }

  public SAllReduce(Communicator comm, LogicalPlan plan,
                    Set<Integer> sources, Set<Integer> targets, MessageType dataType,
                    ReduceFunction fnc, SingularReceiver rcvr) {
    this(comm, plan, sources, targets, dataType, fnc, rcvr, comm.nextEdge(),
        comm.nextEdge(), MessageSchema.noSchema());
  }

  public SAllReduce(Communicator comm, LogicalPlanBuilder logicalPlanBuilder, MessageType dataType,
                    ReduceFunction fnc, SingularReceiver rcvr) {
    this(comm, logicalPlanBuilder.build(), logicalPlanBuilder.getSources(),
        logicalPlanBuilder.getTargets(), dataType, fnc, rcvr, comm.nextEdge(),
        comm.nextEdge(), MessageSchema.noSchema());
  }

  /**
   * Send a message to be reduced
   *
   * @param src source
   * @param message message
   * @param flags message flag
   * @return true if the message is accepted
   */
  public boolean reduce(int src, Object message, int flags) {
    return op.send(src, message, flags);
  }
}
