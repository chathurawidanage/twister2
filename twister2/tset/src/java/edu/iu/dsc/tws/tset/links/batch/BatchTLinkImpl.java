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

package edu.iu.dsc.tws.tset.links.batch;

import edu.iu.dsc.tws.api.comms.structs.Tuple;
import edu.iu.dsc.tws.api.tset.fn.ComputeCollectorFunc;
import edu.iu.dsc.tws.api.tset.fn.ComputeFunc;
import edu.iu.dsc.tws.api.tset.fn.SinkFunc;
import edu.iu.dsc.tws.api.tset.fn.TFunction;
import edu.iu.dsc.tws.api.tset.link.batch.BatchTLink;
import edu.iu.dsc.tws.api.tset.schema.Schema;
import edu.iu.dsc.tws.api.tset.sets.StorableTBase;
import edu.iu.dsc.tws.tset.env.BatchChkPntEnvironment;
import edu.iu.dsc.tws.tset.env.BatchEnvironment;
import edu.iu.dsc.tws.tset.links.BaseTLinkWithSchema;
import edu.iu.dsc.tws.tset.sets.BaseTSet;
import edu.iu.dsc.tws.tset.sets.batch.CheckpointedTSet;
import edu.iu.dsc.tws.tset.sets.batch.ComputeTSet;
import edu.iu.dsc.tws.tset.sets.batch.KeyedTSet;
import edu.iu.dsc.tws.tset.sets.batch.SinkTSet;
import edu.iu.dsc.tws.tset.sources.DiskPartitionBackedSource;

public abstract class BatchTLinkImpl<T1, T0> extends BaseTLinkWithSchema<T1, T0>
    implements BatchTLink<T1, T0> {

  BatchTLinkImpl(BatchEnvironment env, String n, int sourceP, int targetP, Schema schema) {
    super(env, n, sourceP, targetP, schema);
  }

  protected BatchTLinkImpl() {
  }

  @Override
  public BatchEnvironment getTSetEnv() {
    return (BatchEnvironment) super.getTSetEnv();
  }

  public <P> ComputeTSet<P> compute(String name, TFunction<T1, P> computeFunction) {
    ComputeTSet<P> set = new ComputeTSet<>(getTSetEnv(), name, computeFunction,
        getTargetParallelism(), getSchema());
    addChildToGraph(set);

    return set;
  }

  public <K, O> KeyedTSet<K, O> computeToTuple(String n, TFunction<T1, Tuple<K, O>> genTupleFn) {
    KeyedTSet<K, O> set = new KeyedTSet<>(getTSetEnv(), n, genTupleFn, getTargetParallelism(),
        getSchema());
    addChildToGraph(set);

    return set;
  }

  @Override
  public <P> ComputeTSet<P> compute(ComputeFunc<T1, P> computeFunction) {
    return compute(null, computeFunction);
  }

  @Override
  public <P> ComputeTSet<P> compute(ComputeCollectorFunc<T1, P> computeFunction) {
    return compute(null, computeFunction);
  }

  @Override
  public <K, V> KeyedTSet<K, V> computeToTuple(ComputeFunc<T1, Tuple<K, V>> computeFunc) {
    return computeToTuple(null, computeFunc);
  }

  @Override
  public <K, V> KeyedTSet<K, V> computeToTuple(ComputeCollectorFunc<T1, Tuple<K, V>>
                                                   computeFunc) {
    return computeToTuple(null, computeFunc);
  }

  @Override
  public SinkTSet<T1> sink(SinkFunc<T1> sinkFunction) {
    SinkTSet<T1> sinkTSet = new SinkTSet<>(getTSetEnv(), sinkFunction, getTargetParallelism(),
        getSchema());
    addChildToGraph(sinkTSet);

    return sinkTSet;
  }

  /*
   * Returns the superclass @Storable<T> because, this class is used by both keyed and non-keyed
   * TSets. Hence, it produces both CachedTSet<T> as well as KeyedCachedTSet<K, V>
   */
  @Override
  public StorableTBase<T0> cache() {
    StorableTBase<T0> cacheTSet = lazyCache();
    getTSetEnv().run((BaseTSet) cacheTSet);
    return cacheTSet;
  }

  /*
   * Similar to cache, but stores data in disk rather than in memory.
   */
  @Override
  public StorableTBase<T0> persist() {
    // handling checkpointing
    if (getTSetEnv().isCheckpointingEnabled()) {
      String persistVariableName = this.getId() + "-persisted";
      BatchChkPntEnvironment chkEnv = (BatchChkPntEnvironment) getTSetEnv();
      Boolean persisted = chkEnv.initVariable(persistVariableName, false);
      if (persisted) {
        // create a source function with the capability to read from disk
        DiskPartitionBackedSource<T0> sourceFn = new DiskPartitionBackedSource<>(this.getId());

        // pass the source fn to the checkpointed tset (that would create a source tset from the
        // source function, the same way as a persisted tset. This preserves the order of tsets
        // that are being created in the checkpointed env)
        CheckpointedTSet<T0> checkTSet = new CheckpointedTSet<>(getTSetEnv(), sourceFn,
            this.getTargetParallelism(), getSchema());

        // adding checkpointed tset to the graph, so that the IDs would not change
        addChildToGraph(checkTSet);

        // run only the checkpointed tset so that it would populate the inputs in the executor
        getTSetEnv().runOne(checkTSet);

        return checkTSet;
      } else {
        StorableTBase<T0> storable = this.doPersist();
        chkEnv.updateVariable(persistVariableName, true);
        chkEnv.commit();
        return storable;
      }
    }
    return doPersist();
  }

  private StorableTBase<T0> doPersist() {
    StorableTBase<T0> lazyPersist = lazyPersist();
    getTSetEnv().run((BaseTSet) lazyPersist);
    return lazyPersist;
  }
}
