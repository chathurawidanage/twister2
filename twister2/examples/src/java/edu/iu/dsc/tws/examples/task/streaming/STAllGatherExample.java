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
package edu.iu.dsc.tws.examples.task.streaming;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import edu.iu.dsc.tws.api.comms.messaging.types.MessageType;
import edu.iu.dsc.tws.api.comms.messaging.types.MessageTypes;
import edu.iu.dsc.tws.api.comms.structs.Tuple;
import edu.iu.dsc.tws.api.compute.TaskContext;
import edu.iu.dsc.tws.api.compute.nodes.BaseSource;
import edu.iu.dsc.tws.api.compute.nodes.ICompute;
import edu.iu.dsc.tws.api.config.Config;
import edu.iu.dsc.tws.examples.task.BenchTaskWorker;
import edu.iu.dsc.tws.examples.task.streaming.verifiers.GatherVerifier;
import edu.iu.dsc.tws.examples.utils.bench.BenchmarkConstants;
import edu.iu.dsc.tws.examples.utils.bench.BenchmarkUtils;
import edu.iu.dsc.tws.examples.utils.bench.Timing;
import edu.iu.dsc.tws.examples.verification.ResultsVerifier;
import edu.iu.dsc.tws.task.impl.ComputeGraphBuilder;
import edu.iu.dsc.tws.task.typed.AllGatherCompute;

public class STAllGatherExample extends BenchTaskWorker {

  private static final Logger LOG = Logger.getLogger(STAllGatherExample.class.getName());

  @Override
  public ComputeGraphBuilder buildTaskGraph() {
    List<Integer> taskStages = jobParameters.getTaskStages();
    int psource = taskStages.get(0);
    int psink = taskStages.get(1);
    MessageType dataType = MessageTypes.INTEGER_ARRAY;
    String edge = "edge";
    BaseSource g = new SourceTask(edge);
    ICompute r = new AllGatherSinkTask();
    computeGraphBuilder.addSource(SOURCE, g, psource);
    computeConnection = computeGraphBuilder.addCompute(SINK, r, psink);
    computeConnection.allgather(SOURCE).viaEdge(edge).withDataType(dataType);
    return computeGraphBuilder;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  protected static class AllGatherSinkTask extends AllGatherCompute<int[]> {
    private static final long serialVersionUID = -254264903510284798L;

    private ResultsVerifier<int[], Iterator<Tuple<Integer, int[]>>> resultsVerifier;
    private boolean verified = true;
    private boolean timingCondition;

    private int count = 0;

    @Override
    public void prepare(Config cfg, TaskContext ctx) {
      super.prepare(cfg, ctx);
      this.timingCondition = getTimingCondition(SINK, context);
      resultsVerifier = new GatherVerifier(inputDataArray, ctx, SOURCE);
      receiversInProgress.incrementAndGet();
    }

    @Override
    public boolean allGather(Iterator<Tuple<Integer, int[]>> itr) {
      count++;
      if (count > jobParameters.getWarmupIterations()) {
        Timing.mark(BenchmarkConstants.TIMING_MESSAGE_RECV, this.timingCondition);
      }

      if (count == jobParameters.getTotalIterations()) {
        LOG.info(String.format("%d received all-gather %d",
            context.getWorkerId(), context.globalTaskId()));
        Timing.mark(BenchmarkConstants.TIMING_ALL_RECV, this.timingCondition);
        BenchmarkUtils.markTotalAndAverageTime(resultsRecorder, this.timingCondition);
        resultsRecorder.writeToCSV();
        receiversInProgress.decrementAndGet();
      }
      this.verified = verifyResults(resultsVerifier, itr, null, verified);
      return true;
    }
  }
}
