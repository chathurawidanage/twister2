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
package edu.iu.dsc.tws.examples.tset.batch.row;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import edu.iu.dsc.tws.api.JobConfig;
import edu.iu.dsc.tws.api.config.Config;
import edu.iu.dsc.tws.api.tset.fn.ApplyFunc;
import edu.iu.dsc.tws.api.tset.fn.PartitionFunc;
import edu.iu.dsc.tws.api.tset.fn.SourceFunc;
import edu.iu.dsc.tws.api.tset.link.batch.BatchTLink;
import edu.iu.dsc.tws.api.tset.table.Row;
import edu.iu.dsc.tws.api.tset.table.TableSchema;
import edu.iu.dsc.tws.examples.tset.batch.AllGatherExample;
import edu.iu.dsc.tws.examples.tset.batch.BatchTsetExample;
import edu.iu.dsc.tws.rsched.core.ResourceAllocator;
import edu.iu.dsc.tws.tset.env.BatchTSetEnvironment;
import edu.iu.dsc.tws.tset.sets.batch.row.RowSourceTSet;

public class RowTSetExample extends BatchTsetExample {
  private static final Logger LOG = Logger.getLogger(RowTSetExample.class.getName());

  private static final long serialVersionUID = -2753072757838198105L;

  @Override
  public void execute(BatchTSetEnvironment env) {
    RowSourceTSet src = env.createRowSource("row", new SourceFunc<Row>() {
      @Override
      public boolean hasNext() {
        return false;
      }

      @Override
      public Row next() {
        return null;
      }
    }, 4, TableSchema.make());

    BatchTLink<Iterator<Row>, Row> partition = src.partition(new PartitionFunc<Row>() {
      @Override
      public void prepare(Set<Integer> sources, Set<Integer> destinations) {

      }

      @Override
      public int partition(int sourceIndex, Row val) {
        return 0;
      }

      @Override
      public void commit(int source, int partition) {

      }
    }, 4);

    partition.forEach(new ApplyFunc<Row>() {
      @Override
      public void apply(Row data) {

      }
    });
  }


  public static void main(String[] args) {
    Config config = ResourceAllocator.loadConfig(new HashMap<>());

    JobConfig jobConfig = new JobConfig();
    BatchTsetExample.submitJob(config, PARALLELISM, jobConfig, AllGatherExample.class.getName());
  }
}
