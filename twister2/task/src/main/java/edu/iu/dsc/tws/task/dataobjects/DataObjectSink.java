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
package edu.iu.dsc.tws.task.dataobjects;

import edu.iu.dsc.tws.api.compute.IMessage;
import edu.iu.dsc.tws.api.compute.TaskContext;
import edu.iu.dsc.tws.api.compute.modifiers.Collector;
import edu.iu.dsc.tws.api.compute.modifiers.IONames;
import edu.iu.dsc.tws.api.compute.nodes.BaseSink;
import edu.iu.dsc.tws.api.config.Config;
import edu.iu.dsc.tws.api.dataset.DataObject;
import edu.iu.dsc.tws.api.dataset.DataPartition;
import edu.iu.dsc.tws.dataset.DataObjectImpl;
import edu.iu.dsc.tws.dataset.partition.EntityPartition;

/**
 * This class receives the message from the DataObjectSource and writes the output into the
 * DataObject.
 */
public class DataObjectSink<T> extends BaseSink implements Collector {

  public static final String IO_DATA_POINTS = "datapoints";

  private static final long serialVersionUID = -1L;

  private DataObject<Object> datapoints = null;

  /**
   * This method add the received message from the DataObject Source into the data objects.
   */
  @Override
  public boolean execute(IMessage message) {
    datapoints.addPartition(new EntityPartition<>(message.getContent()));
    return true;
  }

  @Override
  public void prepare(Config cfg, TaskContext context) {
    super.prepare(cfg, context);
    this.datapoints = new DataObjectImpl<>(config);
  }

  @Override
  public DataPartition<Object> get(String name) {
    return new EntityPartition<>(datapoints);
  }

  @Override
  public IONames getCollectibleNames() {
    return IONames.declare(IO_DATA_POINTS);
  }
}
