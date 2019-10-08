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
package edu.iu.dsc.tws.examples.ml.svm.aggregate;

import java.util.logging.Logger;

import edu.iu.dsc.tws.api.compute.IMessage;
import edu.iu.dsc.tws.api.compute.graph.OperationMode;
import edu.iu.dsc.tws.api.compute.modifiers.Collector;
import edu.iu.dsc.tws.api.compute.modifiers.IONames;
import edu.iu.dsc.tws.api.compute.nodes.BaseSink;
import edu.iu.dsc.tws.api.dataset.DataPartition;
import edu.iu.dsc.tws.dataset.partition.EntityPartition;

import static edu.iu.dsc.tws.examples.ml.svm.constant.Constants.SimpleGraphConfig.IO_ACCURACY;

public class IterativeSVMAccuracyReduce extends BaseSink<Double> implements Collector<Double> {

  private static final long serialVersionUID = 4268361215513644139L;

  private static final Logger LOG = Logger.getLogger(IterativeSVMAccuracyReduce.class.getName());

  private double newAccuracy;

  private boolean debug = false;

  private boolean status = false;

  private OperationMode operationMode;

  public IterativeSVMAccuracyReduce(OperationMode operationMode) {
    this.operationMode = operationMode;
  }

  @Override
  public DataPartition<Double> get(String name) {
    return new EntityPartition<>(newAccuracy);
  }

  @Override
  public IONames getCollectibleNames() {
    return IONames.declare(IO_ACCURACY);
  }

  @Override
  public boolean execute(IMessage<Double> message) {
    if (message.getContent() == null) {
      LOG.info("Null Accuracy Object, Something Went Wrong !!!");
      this.status = false;
    } else {
      this.newAccuracy = message.getContent();
    }
    return true;
  }
}
