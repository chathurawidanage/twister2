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
package edu.iu.dsc.tws.graphapi.sssp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import edu.iu.dsc.tws.api.compute.IMessage;
import edu.iu.dsc.tws.api.compute.TaskContext;
import edu.iu.dsc.tws.api.compute.modifiers.Collector;
import edu.iu.dsc.tws.api.compute.modifiers.IONames;
import edu.iu.dsc.tws.api.compute.nodes.BaseSink;
import edu.iu.dsc.tws.api.config.Config;
import edu.iu.dsc.tws.api.dataset.DataPartition;
import edu.iu.dsc.tws.dataset.partition.EntityPartition;
import edu.iu.dsc.tws.graphapi.vertex.SsspVertexStatus;


public class SsspInitialSink extends BaseSink implements Collector {

  public static String IO_SSSP_DATA_POINTS = "sssp-data-points";

  private static final long serialVersionUID = -1L;

  private HashMap<String, SsspVertexStatus> dataPointsLocal;


  /**
   * This method add the received message from the DataObject Source into the data objects.
   */
  @Override
  public boolean execute(IMessage message) {
    List<HashMap<String, SsspVertexStatus>> values = new ArrayList<>();
    while (((Iterator) message.getContent()).hasNext()) {
      values.add((HashMap<String, SsspVertexStatus>) ((Iterator) message.getContent()).next());

    }

    for (HashMap<String, SsspVertexStatus> value : values) {
      dataPointsLocal = value;
    }

    return true;
  }

  @Override
  public void prepare(Config cfg, TaskContext context) {
    super.prepare(cfg, context);
  }

  @Override
  public DataPartition<HashMap<String, SsspVertexStatus>> get(String name) {
    return new EntityPartition<>(dataPointsLocal);
  }

  @Override
  public IONames getCollectibleNames() {
    return IONames.declare(IO_SSSP_DATA_POINTS);
  }
}
