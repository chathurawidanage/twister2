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
package edu.iu.dsc.tws.examples.ml.svm.streamer;

import java.util.Arrays;
import java.util.logging.Logger;

import edu.iu.dsc.tws.api.compute.graph.OperationMode;
import edu.iu.dsc.tws.api.compute.modifiers.IONames;
import edu.iu.dsc.tws.api.compute.modifiers.Receptor;
import edu.iu.dsc.tws.api.compute.nodes.BaseSource;
import edu.iu.dsc.tws.api.dataset.DataPartition;
import edu.iu.dsc.tws.examples.ml.svm.constant.Constants;
import edu.iu.dsc.tws.examples.ml.svm.exceptions.MatrixMultiplicationException;
import edu.iu.dsc.tws.examples.ml.svm.sgd.pegasos.PegasosSgdSvm;
import edu.iu.dsc.tws.examples.ml.svm.test.Predict;
import edu.iu.dsc.tws.examples.ml.svm.util.BinaryBatchModel;
import edu.iu.dsc.tws.examples.ml.svm.util.DataUtils;

public class IterativePredictionDataStreamer extends BaseSource implements Receptor {
  private static final long serialVersionUID = -5619263102396811849L;

  private static final Logger LOG = Logger.getLogger(IterativePredictionDataStreamer.class
      .getName());

  private final double[] labels = {-1, +1};
  private int features = 10;
  private OperationMode operationMode;

  private boolean isDummy = false;

  private BinaryBatchModel binaryBatchModel;

  private DataPartition<double[][]> dataPointsObject = null;

  private DataPartition<double[]> weightVectorObject = null;

  private double[][] datapoints = null;

  private double[] weightVector = null;

  private PegasosSgdSvm pegasosSgdSvm = null;

  private boolean debug = false;

  private double finalAccuracy = 0.0;

  public IterativePredictionDataStreamer(OperationMode operationMode) {
    this.operationMode = operationMode;
  }

  public IterativePredictionDataStreamer(int features, OperationMode operationMode,
                                         boolean isDummy, BinaryBatchModel binaryBatchModel) {
    this.features = features;
    this.operationMode = operationMode;
    this.isDummy = isDummy;
    this.binaryBatchModel = binaryBatchModel;
  }

  @Override
  public void add(String name, DataPartition data) {
    if (Constants.SimpleGraphConfig.TEST_DATA.equals(name)) {
      this.dataPointsObject = (DataPartition<double[][]>) data;
    }
    if (Constants.SimpleGraphConfig.INPUT_WEIGHT_VECTOR.equals(name)) {
      this.weightVectorObject = (DataPartition<double[]>) data;
    }
  }

  @Override
  public IONames getReceivableNames() {
    return IONames.declare(Constants.SimpleGraphConfig.TEST_DATA,
        Constants.SimpleGraphConfig.INPUT_WEIGHT_VECTOR);
  }

  @Override
  public void execute() {
    // do prediction on real data
    if (!this.isDummy) {
      realDataStreamer();
    } else {
      LOG.info(String.format("Dummy Data Training Doesn't support prediction"));
    }
  }

  public void realDataStreamer() {
    if (this.operationMode.equals(OperationMode.BATCH)) {
      getData();
      initializeBatchModel();
      compute();
    }

    if (this.operationMode.equals(OperationMode.STREAMING)) {
      // TODO : implement online data streaming
    }
  }

  public void getData() {
    this.datapoints = dataPointsObject.first();
    this.weightVector = weightVectorObject.first();

    if (debug) {
      LOG.info(String.format("Recieved Input Data : %s ", this.datapoints.getClass().getName()));
    }
//    LOG.info(String.format("Data Point TaskIndex[%d], Size : %d ", context.taskIndex(),
//        this.datapoints.length));
//    LOG.info(String.format("Weight Vector TaskIndex[%d], Size : %d ", context.taskIndex(),
//        weightVector.length));
  }

  public void initializeBatchModel() {
    this.initializeBinaryModel(this.datapoints);
    this.binaryBatchModel.setW(this.weightVector);
//    LOG.info(String.format("Features in X : %d, Features in W : %d",
//        this.binaryBatchModel.getFeatures(), this.binaryBatchModel.getW().length));
    pegasosSgdSvm = new PegasosSgdSvm(this.binaryBatchModel.getW(), this.binaryBatchModel.getX(),
        this.binaryBatchModel.getY(), this.binaryBatchModel.getAlpha(),
        this.binaryBatchModel.getIterations(), this.binaryBatchModel.getFeatures());
  }

  /**
   * Binary Model is updated with received batch data
   *
   * @param xy data points included with label and features
   */
  public void initializeBinaryModel(double[][] xy) {
    if (binaryBatchModel == null) {
      throw new NullPointerException("Binary Batch Model is Null !!!");
    }
    if (debug) {
      LOG.info("Binary Batch Model Before Updated : " + this.binaryBatchModel.toString());
    }
    this.binaryBatchModel = DataUtils.updateModelData(this.binaryBatchModel, xy);
    if (debug) {
      LOG.info("Binary Batch Model After Updated : " + this.binaryBatchModel.toString());
      LOG.info(String.format("Updated Data [%d,%d] ",
          this.binaryBatchModel.getX().length, this.binaryBatchModel.getX()[0].length));
    }

  }

  public void compute() {
    double[][] x = this.binaryBatchModel.getX();
    double[] w = this.binaryBatchModel.getW();
    double[] y = this.binaryBatchModel.getY();
    double accuracy = 0.0;
    Predict predict = new Predict(this.binaryBatchModel.getX(), this.binaryBatchModel.getY(), w);
    try {
      accuracy = predict.predict();
    } catch (MatrixMultiplicationException e) {
      e.printStackTrace();
    }
    if (debug) {
      LOG.info(String.format("Accuracy : %f, Context Id : %d, Weight Vector : %s, Data Size : %d",
          accuracy, context.taskIndex(), Arrays.toString(w), x.length));
    }
    finalAccuracy = accuracy / (double) context.getParallelism();
    this.context.write(Constants.SimpleGraphConfig
        .PREDICTION_EDGE, finalAccuracy);
    this.context.end(Constants.SimpleGraphConfig.PREDICTION_EDGE);
  }
}
