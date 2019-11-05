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

package edu.iu.dsc.tws.tset.sinks;

import edu.iu.dsc.tws.api.tset.TSetContext;
import edu.iu.dsc.tws.api.tset.fn.BaseSinkFunc;
import edu.iu.dsc.tws.dataset.partition.DiskBackedCollectionPartition;

public class DiskPersistSingleSink<T> extends BaseSinkFunc<T> {
  private DiskBackedCollectionPartition<T> partition;

  @Override
  public void prepare(TSetContext ctx) {
    super.prepare(ctx);
    partition = new DiskBackedCollectionPartition<>(0, ctx.getConfig());
  }

  @Override
  public boolean add(T value) {
    this.partition.add(value);
    return true;
  }

  @Override
  public void close() {
    partition.close();
  }

  @Override
  public DiskBackedCollectionPartition<T> get() {
    return partition;
  }
}