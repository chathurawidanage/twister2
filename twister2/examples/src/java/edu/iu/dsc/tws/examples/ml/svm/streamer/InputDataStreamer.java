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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.iu.dsc.tws.api.compute.TaskContext;
import edu.iu.dsc.tws.api.compute.graph.OperationMode;
import edu.iu.dsc.tws.api.compute.modifiers.IONames;
import edu.iu.dsc.tws.api.compute.modifiers.Receptor;
import edu.iu.dsc.tws.api.compute.nodes.BaseSource;
import edu.iu.dsc.tws.api.config.Config;
import edu.iu.dsc.tws.api.dataset.DataObject;
import edu.iu.dsc.tws.api.dataset.DataPartition;
import edu.iu.dsc.tws.examples.ml.svm.constant.Constants;
import edu.iu.dsc.tws.examples.ml.svm.exceptions.InputDataFormatException;
import edu.iu.dsc.tws.examples.ml.svm.util.BinaryBatchModel;
import edu.iu.dsc.tws.examples.ml.svm.util.DataUtils;

public class InputDataStreamer extends BaseSource implements Receptor {

  private static final Logger LOG = Logger.getLogger(InputDataStreamer.class.getName());
  private final double[] labels = {-1, +1};
  private int features = 10;
  private OperationMode operationMode;
  private boolean isDummy = false;

  private BinaryBatchModel binaryBatchModel;

  private DataPartition dataPointsObject = null;

  private DataPartition weightVectorObject = null;

  private Object datapoints = null;

  private Object weightVector = null;

  private double[][] datapointArray = null;

  private double[][] weightVectorArray = null;

  private boolean debug = false;

  public InputDataStreamer(OperationMode operationMode) {
    this.operationMode = operationMode;
  }

  public InputDataStreamer(int features, OperationMode operationMode) {
    this.features = features;
    this.operationMode = operationMode;
  }

  public InputDataStreamer(OperationMode operationMode, boolean isDummy,
                           BinaryBatchModel binaryBatchModel) {
    this.operationMode = operationMode;
    this.isDummy = isDummy;
    this.binaryBatchModel = binaryBatchModel;
  }

  @Override
  public void execute() {

    if (this.isDummy) {
      try {
        dummyDataStreamer();
      } catch (InputDataFormatException e) {
        e.printStackTrace();
      }
    } else {
      realDataStreamer();
    }

  }

  @Override
  public void prepare(Config cfg, TaskContext ctx) {
    super.prepare(cfg, ctx);
  }

  /**
   * This method is used to deal with dummy data based data stream generation
   * Here data points are generated using a Gaussian Distribution and labels are assigned
   * +1 or -1 randomly for a given data point.
   */
  public void dummyDataStreamer() throws InputDataFormatException {
    if (this.operationMode.equals(OperationMode.STREAMING)) {
      double[] x = DataUtils.seedDoubleArray(this.binaryBatchModel.getFeatures());
      Random random = new Random();
      int index = random.nextInt(2);
      double label = labels[index];
      double[] data = DataUtils.combineLabelAndData(x, label);
      if (!(data.length == this.binaryBatchModel.getFeatures() + 1)) {
        throw
            new InputDataFormatException(String
                .format("Input Data Format Exception : [data length : %d, feature length +1 : %d]",
                    data.length, this.binaryBatchModel.getFeatures() + 1));
      }
      this.context.write(Constants.SimpleGraphConfig.DATA_EDGE, data);
    }

    if (this.operationMode.equals(OperationMode.BATCH)) {
      double[][] data = DataUtils.generateDummyDataPoints(this.binaryBatchModel.getSamples(),
          this.binaryBatchModel.getFeatures());
      this.context.write(Constants.SimpleGraphConfig.DATA_EDGE, data);
      this.context.end(Constants.SimpleGraphConfig.DATA_EDGE);
    }

  }

  /**
   * This method is used to retrieve real data from
   * Data Source Task
   */
  public void realDataStreamer() {
    // do real data streaming
    if (this.operationMode.equals(OperationMode.BATCH)) {
      getData();
      this.context.write(Constants.SimpleGraphConfig.DATA_EDGE, this.datapointArray);
      this.context.end(Constants.SimpleGraphConfig.DATA_EDGE);
    }
  }

  @Override
  public void add(String name, DataPartition data) {
    if (debug) {
      LOG.log(Level.INFO, String.format("Received input: %s ", name));
    }

    if (Constants.SimpleGraphConfig.INPUT_DATA.equals(name)) {
      this.dataPointsObject = data;
    }

    if (Constants.SimpleGraphConfig.INPUT_WEIGHT_VECTOR.equals(name)) {
      this.weightVectorObject = data;
    }
  }

  @Override
  public IONames getReceivableNames() {
    return IONames.declare(Constants.SimpleGraphConfig.INPUT_DATA,
        Constants.SimpleGraphConfig.INPUT_WEIGHT_VECTOR);
  }

  public Object getDataPointsByTaskIndex(int taskIndex) {
    //todo data objct out from data partition??

    if (dataPointsObject != null) {
      DataObject<?> dataObject
          = (DataObject<?>) dataPointsObject.first();
      datapoints = getDataPointsByDataObject(taskIndex, dataObject);
    }

    return datapoints;
  }

  public Object getWeightVectorByTaskIndex(int taskIndex) {

    if (weightVectorObject != null) {
      DataObject<?> weightVectorObjectLocal
          = (DataObject<?>) weightVectorObject.first();
      weightVector = getDataPointsByDataObject(taskIndex, weightVectorObjectLocal);
    }
    return weightVector;
  }

  public Object getDataPointsByDataObject(int taskIndex, DataObject<?> datapointsDataObject) {
    Iterator<ArrayList> arrayListIterator = (Iterator<ArrayList>)
        datapointsDataObject.getPartition(taskIndex).getConsumer().next();
    List<Object> items = new ArrayList<>();
    while (arrayListIterator.hasNext()) {
      Object object = arrayListIterator.next();
      items.add(object);
    }
    return items;
  }

  public void getData() {
    this.datapoints = getDataPointsByTaskIndex(context.taskIndex());
    this.weightVector = getWeightVectorByTaskIndex(context.taskIndex());

    if (debug) {
      LOG.info(String.format("Recieved Input Data : %s ", this.datapoints.getClass().getName()));
    }
    this.datapointArray = DataUtils.getDataPointsFromDataObject(this.datapoints);
    this.weightVectorArray = DataUtils.getDataPointsFromDataObject(this.weightVector);

    LOG.info(String.format("Data Point TaskIndex[%d], Size : %d ", context.taskIndex(),
        this.datapointArray.length));
    LOG.info(String.format("Weight Vector TaskIndex[%d], Size : %d ", context.taskIndex(),
        this.weightVectorArray.length));
    //LOG.info(String.format("Data Points : %s", Arrays.deepToString(this.datapointArray)));
  }
}
