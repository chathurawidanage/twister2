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
package edu.iu.dsc.tws.api.tset.sets.streaming;


import java.util.Collection;

import edu.iu.dsc.tws.api.comms.structs.Tuple;
import edu.iu.dsc.tws.api.tset.Cacheable;
import edu.iu.dsc.tws.api.tset.env.StreamingTSetEnvironment;
import edu.iu.dsc.tws.api.tset.fn.MapFunc;
import edu.iu.dsc.tws.api.tset.fn.MapIterCompute;
import edu.iu.dsc.tws.api.tset.fn.PartitionFunc;
import edu.iu.dsc.tws.api.tset.fn.ReduceFunc;
import edu.iu.dsc.tws.api.tset.link.streaming.SAllGatherTLink;
import edu.iu.dsc.tws.api.tset.link.streaming.SAllReduceTLink;
import edu.iu.dsc.tws.api.tset.link.streaming.SDirectTLink;
import edu.iu.dsc.tws.api.tset.link.streaming.SGatherTLink;
import edu.iu.dsc.tws.api.tset.link.streaming.SPartitionTLink;
import edu.iu.dsc.tws.api.tset.link.streaming.SReduceTLink;
import edu.iu.dsc.tws.api.tset.link.streaming.SReplicateTLink;
import edu.iu.dsc.tws.api.tset.ops.ComputeCollectorOp;
import edu.iu.dsc.tws.api.tset.sets.BaseTSet;
import edu.iu.dsc.tws.api.tset.sets.TSet;
import edu.iu.dsc.tws.api.tset.sets.batch.BBaseTSet;

public abstract class SBaseTSet<T> extends BaseTSet<T> implements StreamingTSet<T> {

  SBaseTSet(StreamingTSetEnvironment tSetEnv, String name, int parallelism) {
    super(tSetEnv, name, parallelism);
  }

  @Override
  public StreamingTSetEnvironment getTSetEnv() {
    return (StreamingTSetEnvironment) super.getTSetEnv();
  }

  @Override
  public SDirectTLink<T> direct() {
    SDirectTLink<T> direct = new SDirectTLink<>(getTSetEnv(), getParallelism());
    addChildToGraph(direct);
    return direct;
  }

  @Override
  public SReduceTLink<T> reduce(ReduceFunc<T> reduceFn) {
    SReduceTLink<T> reduce = new SReduceTLink<>(getTSetEnv(), reduceFn, getParallelism());
    addChildToGraph(reduce);
    return reduce;
  }

  @Override
  public SPartitionTLink<T> partition(PartitionFunc<T> partitionFn, int targetParallelism) {
    SPartitionTLink<T> partition = new SPartitionTLink<>(getTSetEnv(),
        partitionFn, getParallelism(), targetParallelism);
    addChildToGraph(partition);
    return partition;
  }

  @Override
  public SPartitionTLink<T> partition(PartitionFunc<T> partitionFn) {
    return partition(partitionFn, getParallelism());
  }

  @Override
  public SGatherTLink<T> gather() {
    SGatherTLink<T> gather = new SGatherTLink<>(getTSetEnv(), getParallelism());
    addChildToGraph(gather);
    return gather;
  }

  @Override
  public SAllReduceTLink<T> allReduce(ReduceFunc<T> reduceFn) {
    SAllReduceTLink<T> allreduce = new SAllReduceTLink<>(getTSetEnv(), reduceFn,
        getParallelism());
    addChildToGraph(allreduce);
    return allreduce;
  }

  @Override
  public SAllGatherTLink<T> allGather() {
    SAllGatherTLink<T> allgather = new SAllGatherTLink<>(getTSetEnv(),
        getParallelism());
    addChildToGraph(allgather);
    return allgather;
  }

  @Override
  public TSet<T> union(TSet<T> other) {

    if (this.getParallelism() != ((SBaseTSet) other).getParallelism()) {
      throw new IllegalStateException("Parallelism of the TSets need to be the same in order to"
          + "perform a union operation");
    }
    SDirectTLink<T> directCurrent = new SDirectTLink<>(getTSetEnv(), getParallelism());
    SDirectTLink<T> directOther = new SDirectTLink<>(getTSetEnv(), getParallelism());
    addChildToGraph(this, directCurrent);
    addChildToGraph((BBaseTSet) other, directCurrent);
    SComputeTSet<T, T> unionTSet = new SComputeTSet<T, T>(getTSetEnv(), "union",
        new ComputeCollectorOp<T, T>(new MapIterCompute(input -> input)),
        getParallelism());
    addChildToGraph(directCurrent, unionTSet);
    addChildToGraph(directOther, unionTSet);
    return unionTSet;
  }

  @Override
  public TSet<T> union(Collection<TSet<T>> tSets) {
    SBaseTSet<T> other;
    SDirectTLink<T> directCurrent = new SDirectTLink<>(getTSetEnv(), getParallelism());
    addChildToGraph(this, directCurrent);

    SComputeTSet<T, T> unionTSet = new SComputeTSet<T, T>(getTSetEnv(), "union",
        new ComputeCollectorOp<T, T>(new MapIterCompute(input -> input)),
        getParallelism());

    addChildToGraph(directCurrent, unionTSet);
    for (TSet<T> tSet : tSets) {
      other = (SBaseTSet) tSet;
      if (this.getParallelism() != other.getParallelism()) {
        throw new IllegalStateException("Parallelism of the TSets need to be the same in order to"
            + "perform a union operation");
      }
      SDirectTLink<T> directOther = new SDirectTLink<>(getTSetEnv(), getParallelism());
      addChildToGraph(other, directOther);
      addChildToGraph(directOther, unionTSet);
    }
    return null;
  }

  @Override
  public <K, V> SKeyedTSet<K, V> mapToTuple(MapFunc<Tuple<K, V>, T> mapToTupleFn) {
    return direct().mapToTuple(mapToTupleFn);
//    throw new UnsupportedOperationException("Groupby is not avilable in streaming operations");
  }

  @Override
  public SReplicateTLink<T> replicate(int replications) {
    if (getParallelism() != 1) {
      throw new RuntimeException("Only tsets with parallelism 1 can be replicated: "
          + getParallelism());
    }

    SReplicateTLink<T> cloneTSet = new SReplicateTLink<>(getTSetEnv(),
        replications);
    addChildToGraph(cloneTSet);
    return cloneTSet;
  }

  @Override
  public boolean addInput(String key, Cacheable<?> input) {
    getTSetEnv().addInput(getId(), key, input);
    return true;
  }

}
