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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import edu.iu.dsc.tws.api.JobConfig;
import edu.iu.dsc.tws.api.comms.messaging.types.MessageTypes;
import edu.iu.dsc.tws.api.config.Config;
import edu.iu.dsc.tws.api.resource.WorkerEnvironment;
import edu.iu.dsc.tws.api.tset.TSetContext;
import edu.iu.dsc.tws.api.tset.fn.ApplyFunc;
import edu.iu.dsc.tws.api.tset.fn.PartitionFunc;
import edu.iu.dsc.tws.api.tset.fn.SourceFunc;
import edu.iu.dsc.tws.api.tset.link.batch.BatchRowTLink;
import edu.iu.dsc.tws.api.tset.schema.RowSchema;
import edu.iu.dsc.tws.common.table.Row;
import edu.iu.dsc.tws.common.table.TField;
import edu.iu.dsc.tws.common.table.TwoRow;
import edu.iu.dsc.tws.examples.tset.batch.BatchTsetExample;
import edu.iu.dsc.tws.rsched.core.ResourceAllocator;
import edu.iu.dsc.tws.tset.env.BatchEnvironment;
import edu.iu.dsc.tws.tset.env.TSetEnvironment;
import edu.iu.dsc.tws.tset.sets.batch.row.RowSourceTSet;

public class PartitionExample extends BatchTsetExample {
  private static final Logger LOG = Logger.getLogger(PartitionExample.class.getName());
  private static final long serialVersionUID = -2753072757838198105L;

  @Override
  public void execute(WorkerEnvironment workerEnvironment) {
    BatchEnvironment env = TSetEnvironment.initBatch(workerEnvironment);
    List<TField> fieldList = new ArrayList<>();
    fieldList.add(new TField("first", MessageTypes.INTEGER));
    fieldList.add(new TField("second", MessageTypes.DOUBLE));

    RowSourceTSet src = env.createRowSource("row", new SourceFunc<Row>() {
      private int count = 0;
      @Override
      public boolean hasNext() {
        return count++ < 1000;
      }

      @Override
      public Row next() {
        return new TwoRow(1, 4.1);
      }
    }, 4).withSchema(new RowSchema(fieldList));

    BatchRowTLink partition = src.partition(new PartitionFunc<Row>() {
      private List<Integer> targets;
      private Random random;
      private int c = 0;
      private Map<Integer, Integer> counts = new HashMap<>();
      @Override
      public void prepare(Set<Integer> sources, Set<Integer> destinations) {
        targets = new ArrayList<>(destinations);
        random = new Random();
        for (int t : targets) {
          counts.put(t, 0);
        }
      }

      @Override
      public int partition(int sourceIndex, Row val) {
        int index = random.nextInt(targets.size());
        int count = counts.get(index);
        counts.put(index, count + 1);
        c++;
        if (c == 1000) {
          LOG.info("COUNTS " + counts);
        }
        return targets.get(index);
      }
    }, 4, 0);

    partition.forEach(new ApplyFunc<Row>() {
      private TSetContext ctx;
      private int count;
      @Override
      public void prepare(TSetContext context) {
        ctx = context;
      }
      @Override
      public void apply(Row data) {
        LOG.info(ctx.getIndex() + " Data " + data.get(0) + ", " + data.get(1)
            + ", count " + count++);
      }
    });
  }


  public static void main(String[] args) {
    Config config = ResourceAllocator.loadConfig(new HashMap<>());
    JobConfig jobConfig = new JobConfig();
    BatchTsetExample.submitJob(config, PARALLELISM, jobConfig, PartitionExample.class.getName());
  }
}
