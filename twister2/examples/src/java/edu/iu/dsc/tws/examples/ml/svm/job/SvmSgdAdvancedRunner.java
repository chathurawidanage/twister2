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
package edu.iu.dsc.tws.examples.ml.svm.job;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;

import edu.iu.dsc.tws.api.comms.messaging.types.MessageTypes;
import edu.iu.dsc.tws.api.compute.executor.ExecutionPlan;
import edu.iu.dsc.tws.api.compute.graph.ComputeGraph;
import edu.iu.dsc.tws.api.compute.graph.OperationMode;
import edu.iu.dsc.tws.api.config.Context;
import edu.iu.dsc.tws.api.dataset.DataObject;
import edu.iu.dsc.tws.api.dataset.DataPartition;
import edu.iu.dsc.tws.api.dataset.DataPartitionConsumer;
import edu.iu.dsc.tws.examples.ml.svm.aggregate.ReduceAggregator;
import edu.iu.dsc.tws.examples.ml.svm.aggregate.SVMReduce;
import edu.iu.dsc.tws.examples.ml.svm.compute.IterativeSVMCompute;
import edu.iu.dsc.tws.examples.ml.svm.compute.SVMCompute;
import edu.iu.dsc.tws.examples.ml.svm.constant.Constants;
import edu.iu.dsc.tws.examples.ml.svm.constant.TimingConstants;
import edu.iu.dsc.tws.examples.ml.svm.streamer.InputDataStreamer;
import edu.iu.dsc.tws.examples.ml.svm.test.PredictionAggregator;
import edu.iu.dsc.tws.examples.ml.svm.test.PredictionReduceTask;
import edu.iu.dsc.tws.examples.ml.svm.test.PredictionSourceTask;
import edu.iu.dsc.tws.examples.ml.svm.util.BinaryBatchModel;
import edu.iu.dsc.tws.examples.ml.svm.util.DataUtils;
import edu.iu.dsc.tws.examples.ml.svm.util.ResultsSaver;
import edu.iu.dsc.tws.examples.ml.svm.util.SVMJobParameters;
import edu.iu.dsc.tws.task.dataobjects.DataObjectSink;
import edu.iu.dsc.tws.task.dataobjects.DataObjectSource;
import edu.iu.dsc.tws.task.impl.ComputeConnection;
import edu.iu.dsc.tws.task.impl.ComputeGraphBuilder;
import edu.iu.dsc.tws.task.impl.TaskWorker;

public class SvmSgdAdvancedRunner extends TaskWorker {

  private static final Logger LOG = Logger.getLogger(SvmSgdAdvancedRunner.class.getName());
  private static final double NANO_TO_SEC = 1000000000;
  private static final double B2MB = 1024.0 * 1024.0;
  private final int reduceParallelism = 1;
  private int dataStreamerParallelism = 4;
  private int svmComputeParallelism = 4;
  private int features = 10;
  private OperationMode operationMode;
  private SVMJobParameters svmJobParameters;
  private BinaryBatchModel binaryBatchModel;
  private ComputeGraphBuilder trainingBuilder;
  private ComputeGraphBuilder testingBuilder;
  private InputDataStreamer dataStreamer;
  private SVMCompute svmCompute;
  private IterativeSVMCompute iterativeSVMCompute;
  private SVMReduce svmReduce;
  private DataObject<Object> trainingData;
  private DataObject<Object> inputWeightVector;
  private DataObject<Object> testingData;
  private DataObject<Object> testingResults;
  private DataObject<double[]> trainedWeightVector;
  private PredictionSourceTask predictionSourceTask;
  private PredictionReduceTask predictionReduceTask;
  private PredictionAggregator predictionAggregator;
  private double dataLoadingTime = 0;
  private double trainingTime = 0;
  private double testingTime = 0;
  private double accuracy = 0;
  private boolean debug = false;
  private String experimentName = "";

  @Override
  public void execute() {
    initializeParameters();
    initializeExecute();
  }

  /**
   * This method initializes the parameters in running SVM
   */
  public void initializeParameters() {
    this.svmJobParameters = SVMJobParameters.build(config);
    this.binaryBatchModel = new BinaryBatchModel();
    this.dataStreamerParallelism = this.svmJobParameters.getParallelism();
    this.experimentName = this.svmJobParameters.getExperimentName();
    // svm compute parallelism can be set as a configurable parameter
    this.svmComputeParallelism = this.dataStreamerParallelism;
    this.binaryBatchModel.setIterations(this.svmJobParameters.getIterations());
    this.binaryBatchModel.setAlpha(this.svmJobParameters.getAlpha());
    this.binaryBatchModel.setFeatures(this.svmJobParameters.getFeatures());
    this.binaryBatchModel.setSamples(this.svmJobParameters.getSamples());
    this.binaryBatchModel.setW(DataUtils.seedDoubleArray(this.svmJobParameters.getFeatures()));
    LOG.info(this.binaryBatchModel.toString());
  }

  /**
   * Initializing the execute method
   */
  public void initializeExecute() {
    trainingBuilder = ComputeGraphBuilder.newBuilder(config);
    testingBuilder = ComputeGraphBuilder.newBuilder(config);

    this.operationMode = this.svmJobParameters.isStreaming()
        ? OperationMode.STREAMING : OperationMode.BATCH;

    inputWeightVector = executeWeightVectorLoadingTaskGraph();

    Long t1 = System.nanoTime();
    trainingData = executeTrainingDataLoadingTaskGraph();
    dataLoadingTime = (double) (System.nanoTime() - t1) / NANO_TO_SEC;

    t1 = System.nanoTime();
    trainedWeightVector = executeTrainingGraph();
    trainingTime = (double) (System.nanoTime() - t1) / NANO_TO_SEC;

    t1 = System.nanoTime();
    testingData = executeTestingDataLoadingTaskGraph();
    dataLoadingTime += (double) (System.nanoTime() - t1) / NANO_TO_SEC;

    //TODO : Handle Distributed Weight Vector Loading

//    if (operationMode.equals(OperationMode.BATCH)) {
//      t1 = System.nanoTime();
//      testingResults = executeTestingTaskGraph();
//      accuracy = retriveFinalTestingAccuracy(testingResults);
//      testingTime += (double) (System.nanoTime() - t1) / NANO_TO_SEC;
//      printTaskSummary();
//    }
    printTaskSummary();
    if (workerId == 0) {
      try {
        saveResults();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    LOG.info(String.format("Rank[%d] Total Memory %f MB, Max Memory %f MB", workerId,
        ((double) Runtime.getRuntime().totalMemory()) / B2MB,
        ((double) Runtime.getRuntime().maxMemory()) / B2MB));

    if (operationMode.equals(OperationMode.STREAMING)) {
      LOG.info("Not Yet Implemented");
    }
  }

  /**
   * This method loads the training data in a distributed mode
   * dataStreamerParallelism is the amount of parallelism used
   * in loaded the data in parallel.
   *
   * @return twister2 DataObject containing the training data
   */
  public DataObject<Object> executeTrainingDataLoadingTaskGraph() {
    DataObject<Object> data = null;
    DataObjectSource sourceTask = new DataObjectSource(Context.TWISTER2_DIRECT_EDGE,
        this.svmJobParameters.getTrainingDataDir());
    DataObjectSink sinkTask = new DataObjectSink();
    trainingBuilder.addSource(Constants.SimpleGraphConfig.DATA_OBJECT_SOURCE,
        sourceTask, dataStreamerParallelism);
    ComputeConnection firstGraphComputeConnection = trainingBuilder.addSink(
        Constants.SimpleGraphConfig.DATA_OBJECT_SINK, sinkTask, dataStreamerParallelism);
    firstGraphComputeConnection.direct(Constants.SimpleGraphConfig.DATA_OBJECT_SOURCE)
        .viaEdge(Context.TWISTER2_DIRECT_EDGE).withDataType(MessageTypes.OBJECT);
    trainingBuilder.setMode(OperationMode.BATCH);

    ComputeGraph datapointsTaskGraph = trainingBuilder.build();
    datapointsTaskGraph.setGraphName("training-data-loading-graph");
    ExecutionPlan firstGraphExecutionPlan = taskExecutor.plan(datapointsTaskGraph);
    taskExecutor.execute(datapointsTaskGraph, firstGraphExecutionPlan);
    data = taskExecutor.getOutput(DataObjectSink.IO_DATA_POINTS);
    if (data == null) {
      throw new NullPointerException("Something Went Wrong in Loading Training Data");
    } else {
      LOG.info("Training Data Total Partitions : " + data.getPartitions().length);
    }
    return data;
  }

  /**
   * This method loads the training data in a distributed mode
   * dataStreamerParallelism is the amount of parallelism used
   * in loaded the data in parallel.
   *
   * @return twister2 DataObject containing the training data
   */
  public DataObject<Object> executeWeightVectorLoadingTaskGraph() {
    DataObject<Object> data = null;
    DataObjectSource sourceTask = new DataObjectSource(Context.TWISTER2_DIRECT_EDGE,
        this.svmJobParameters.getWeightVectorDataDir());
    DataObjectSink sinkTask = new DataObjectSink();
    trainingBuilder.addSource(Constants.SimpleGraphConfig.DATA_OBJECT_SOURCE,
        sourceTask, dataStreamerParallelism);
    ComputeConnection firstGraphComputeConnection = trainingBuilder.addSink(
        Constants.SimpleGraphConfig.DATA_OBJECT_SINK, sinkTask, dataStreamerParallelism);
    firstGraphComputeConnection.direct(Constants.SimpleGraphConfig.DATA_OBJECT_SOURCE)
        .viaEdge(Context.TWISTER2_DIRECT_EDGE).withDataType(MessageTypes.OBJECT);
    trainingBuilder.setMode(OperationMode.BATCH);

    ComputeGraph datapointsTaskGraph = trainingBuilder.build();
    datapointsTaskGraph.setGraphName("weight-vector-loading-graph");
    ExecutionPlan firstGraphExecutionPlan = taskExecutor.plan(datapointsTaskGraph);
    taskExecutor.execute(datapointsTaskGraph, firstGraphExecutionPlan);
    data = taskExecutor.getOutput(DataObjectSink.IO_DATA_POINTS);
    if (data == null) {
      throw new NullPointerException("Something Went Wrong in Loading Weight Vector");
    } else {
      LOG.info("Training Data Total Partitions : " + data.getPartitions().length);
    }
    return data;
  }

  /**
   * This method loads the testing data
   * The loaded test data is used to evaluate the trained data
   * Testing data is loaded in parallel depending on the parallelism parameter given
   * There are partitions created equal to the parallelism
   * Later this will be used to do the testing in parallel in the testing task graph
   *
   * @return twister2 DataObject containing the testing data
   */
  public DataObject<Object> executeTestingDataLoadingTaskGraph() {
    DataObject<Object> data = null;
    final String TEST_DATA_LOAD_EDGE_DIRECT = "direct2";
    DataObjectSource sourceTask1 = new DataObjectSource(TEST_DATA_LOAD_EDGE_DIRECT,
        this.svmJobParameters.getTestingDataDir());
    DataObjectSink sinkTask1 = new DataObjectSink();
    testingBuilder.addSource(Constants.SimpleGraphConfig.DATA_OBJECT_SOURCE_TESTING,
        sourceTask1, dataStreamerParallelism);
    ComputeConnection firstGraphComputeConnection1 = testingBuilder.addSink(
        Constants.SimpleGraphConfig.DATA_OBJECT_SINK_TESTING, sinkTask1, dataStreamerParallelism);
    firstGraphComputeConnection1.direct(Constants.SimpleGraphConfig.DATA_OBJECT_SOURCE_TESTING)
        .viaEdge(TEST_DATA_LOAD_EDGE_DIRECT).withDataType(MessageTypes.OBJECT);
    testingBuilder.setMode(OperationMode.BATCH);

    ComputeGraph datapointsTaskGraph1 = testingBuilder.build();
    datapointsTaskGraph1.setGraphName("testing-data-loading-graph");
    ExecutionPlan firstGraphExecutionPlan1 = taskExecutor.plan(datapointsTaskGraph1);
    taskExecutor.execute(datapointsTaskGraph1, firstGraphExecutionPlan1);
    data = taskExecutor.getOutput(DataObjectSink.IO_DATA_POINTS);
    if (data == null) {
      throw new NullPointerException("Something Went Wrong in Loading Testing Data");
    } else {
      LOG.info("Testing Data Total Partitions : " + data.getPartitions().length);
    }
    return data;
  }


  /**
   * This method executes the training graph
   * Training is done in parallel depending on the parallelism factor given
   * In this implementation the data loading parallelism and data computing or
   * training parallelism is same. It is the general model to keep them equal. But
   * you can increase the parallelism the way you want. But it is adviced to keep these
   * values equal. Dynamic parallelism in training is not yet tested fully in Twister2 Framework.
   *
   * @return Twister2 DataObject<double[]> containing the reduced weight vector
   */
  public DataObject<double[]> executeTrainingGraph() {

    DataObject<double[]> trainedWeight = null;

    dataStreamer = new InputDataStreamer(this.operationMode,
        svmJobParameters.isDummy(), this.binaryBatchModel);
    svmCompute = new SVMCompute(this.binaryBatchModel, this.operationMode);
    svmReduce = new SVMReduce(this.operationMode);

    trainingBuilder.addSource(Constants.SimpleGraphConfig.DATASTREAMER_SOURCE, dataStreamer,
        dataStreamerParallelism);
    ComputeConnection svmComputeConnection = trainingBuilder
        .addCompute(Constants.SimpleGraphConfig.SVM_COMPUTE, svmCompute, svmComputeParallelism);
    ComputeConnection svmReduceConnection = trainingBuilder
        .addSink(Constants.SimpleGraphConfig.SVM_REDUCE, svmReduce, reduceParallelism);

    svmComputeConnection
        .direct(Constants.SimpleGraphConfig.DATASTREAMER_SOURCE)
        .viaEdge(Constants.SimpleGraphConfig.DATA_EDGE)
        .withDataType(MessageTypes.OBJECT);
//    svmReduceConnection
//        .reduce(Constants.SimpleGraphConfig.SVM_COMPUTE, Constants.SimpleGraphConfig.REDUCE_EDGE,
//            new ReduceAggregator(), DataType.OBJECT);
    svmReduceConnection
        .allreduce(Constants.SimpleGraphConfig.SVM_COMPUTE)
        .viaEdge(Constants.SimpleGraphConfig.REDUCE_EDGE)
        .withReductionFunction(new ReduceAggregator())
        .withDataType(MessageTypes.OBJECT);

    trainingBuilder.setMode(operationMode);
    ComputeGraph graph = trainingBuilder.build();
    graph.setGraphName("training-graph");
    ExecutionPlan plan = taskExecutor.plan(graph);

    taskExecutor.addInput(
        graph, plan, Constants.SimpleGraphConfig.DATASTREAMER_SOURCE,
        Constants.SimpleGraphConfig.INPUT_DATA, trainingData);
    taskExecutor.addInput(graph, plan, Constants.SimpleGraphConfig.DATASTREAMER_SOURCE,
        Constants.SimpleGraphConfig.INPUT_WEIGHT_VECTOR, inputWeightVector);

    taskExecutor.execute(graph, plan);

    LOG.info("Task Graph Executed !!! ");
    if (workerId == 0) {
      trainedWeight = retrieveWeightVectorFromTaskGraph(graph, plan);
      this.trainedWeightVector = trainedWeight;
    }

    return trainedWeight;
  }

  /**
   * This method executes the iterative training graph
   * Training is done in parallel depending on the parallelism factor given
   * In this implementation the data loading parallelism and data computing or
   * training parallelism is same. It is the general model to keep them equal. But
   * you can increase the parallelism the way you want. But it is adviced to keep these
   * values equal. Dynamic parallelism in training is not yet tested fully in Twister2 Framework.
   *
   * @return Twister2 DataObject<double[]> containing the reduced weight vector
   */
  public DataObject<double[]> executeIterativeTrainingGraph() {

    DataObject<double[]> trainedWeight = null;

    dataStreamer = new InputDataStreamer(this.operationMode,
        svmJobParameters.isDummy(), this.binaryBatchModel);
    iterativeSVMCompute = new IterativeSVMCompute(this.binaryBatchModel, this.operationMode);
    svmReduce = new SVMReduce(this.operationMode);

    trainingBuilder.addSource(Constants.SimpleGraphConfig.DATASTREAMER_SOURCE, dataStreamer,
        dataStreamerParallelism);
    ComputeConnection svmComputeConnection = trainingBuilder
        .addCompute(Constants.SimpleGraphConfig.SVM_COMPUTE, iterativeSVMCompute,
            svmComputeParallelism);
    ComputeConnection svmReduceConnection = trainingBuilder
        .addSink(Constants.SimpleGraphConfig.SVM_REDUCE, svmReduce, reduceParallelism);

    svmComputeConnection
        .direct(Constants.SimpleGraphConfig.DATASTREAMER_SOURCE)
        .viaEdge(Constants.SimpleGraphConfig.DATA_EDGE)
        .withDataType(MessageTypes.OBJECT);
//    svmReduceConnection
//        .reduce(Constants.SimpleGraphConfig.SVM_COMPUTE, Constants.SimpleGraphConfig.REDUCE_EDGE,
//            new ReduceAggregator(), DataType.OBJECT);
    svmReduceConnection
        .allreduce(Constants.SimpleGraphConfig.SVM_COMPUTE)
        .viaEdge(Constants.SimpleGraphConfig.REDUCE_EDGE)
        .withReductionFunction(new ReduceAggregator())
        .withDataType(MessageTypes.OBJECT);

    trainingBuilder.setMode(operationMode);
    ComputeGraph graph = trainingBuilder.build();
    graph.setGraphName("training-graph");


    ExecutionPlan plan = taskExecutor.plan(graph);

    // iteration is being decoupled from the computation task
    for (int i = 0; i < this.binaryBatchModel.getIterations(); i++) {

      taskExecutor.addInput(
          graph, plan, Constants.SimpleGraphConfig.DATASTREAMER_SOURCE,
          Constants.SimpleGraphConfig.INPUT_DATA, trainingData);
      taskExecutor.addInput(graph, plan, Constants.SimpleGraphConfig.DATASTREAMER_SOURCE,
          Constants.SimpleGraphConfig.INPUT_WEIGHT_VECTOR, inputWeightVector);

      inputWeightVector = taskExecutor.getOutput(graph, plan,
          Constants.SimpleGraphConfig.SVM_REDUCE);

      taskExecutor.itrExecute(graph, plan);

    }
    taskExecutor.waitFor(graph, plan);

    LOG.info("Task Graph Executed !!! ");
    if (workerId == 0) {
      trainedWeight = retrieveWeightVectorFromTaskGraph(graph, plan);
      this.trainedWeightVector = trainedWeight;
    }

    return trainedWeight;
  }

  /**
   * This method returns the final weight vector from the trained model
   *
   * @param graph1 DataflowTaskGraph from which we retrieve the final weight vector
   * @param plan1 ExecutionPlan from which we retrive the final weight vector
   * @return Twister2 DataObject containing the final reduced weight vector is retrieved
   */
  public DataObject<double[]> retrieveWeightVectorFromTaskGraph(ComputeGraph graph1,
                                                                ExecutionPlan plan1) {

    DataObject<double[]> dataSet = taskExecutor.getOutput(graph1, plan1,
        Constants.SimpleGraphConfig.SVM_REDUCE);
    if (debug) {
      LOG.info(String.format("Number of Partitions : %d ", dataSet.getPartitions().length));
    }

    DataPartition<double[]> values = dataSet.getPartitions()[0];
    DataPartitionConsumer<double[]> dataPartitionConsumer = values.getConsumer();

    while (dataPartitionConsumer.hasNext()) {
      LOG.info("Final Weight Vector:"
          + Arrays.toString(dataPartitionConsumer.next()));
    }

    if (dataSet == null) {
      throw new NullPointerException(" Something went wrong in retrieving trained weights "
          + "from training task graph");
    }

    return dataSet;
  }

  /**
   * This method executes the testing taskgraph with testing data loaded from testing taskgraph
   * and uses the final weight vector obtained from the training task graph
   * Testing is also done in a parallel way. At the testing data loading stage we load the data
   * in parallel with reference to the given parallelism and testing is also in in parallel
   * Then we get test results for all these testing data partitions
   *
   * @return Returns the Accuracy value obtained
   */
  public DataObject<Object> executeTestingTaskGraph() {
    DataObject<Object> data = null;
    predictionSourceTask
        = new PredictionSourceTask(svmJobParameters.isDummy(), this.binaryBatchModel,
        operationMode);
    predictionReduceTask = new PredictionReduceTask(operationMode);

    testingBuilder.addSource(Constants.SimpleGraphConfig.PREDICTION_SOURCE_TASK,
        predictionSourceTask,
        dataStreamerParallelism);
    ComputeConnection predictionReduceConnection = testingBuilder
        .addSink(Constants.SimpleGraphConfig.PREDICTION_REDUCE_TASK, predictionReduceTask,
            reduceParallelism);
    predictionReduceConnection
        .reduce(Constants.SimpleGraphConfig.PREDICTION_SOURCE_TASK)
        .viaEdge(Constants.SimpleGraphConfig.PREDICTION_EDGE)
        .withReductionFunction(new PredictionAggregator())
        .withDataType(MessageTypes.OBJECT);
    testingBuilder.setMode(operationMode);
    ComputeGraph predictionGraph = testingBuilder.build();
    predictionGraph.setGraphName("testing-graph");
    ExecutionPlan predictionPlan = taskExecutor.plan(predictionGraph);
    // adding test data set
    taskExecutor
        .addInput(predictionGraph, predictionPlan,
            Constants.SimpleGraphConfig.PREDICTION_SOURCE_TASK,
            Constants.SimpleGraphConfig.TEST_DATA, testingData);
    // adding final weight vector
    taskExecutor
        .addInput(predictionGraph, predictionPlan,
            Constants.SimpleGraphConfig.PREDICTION_SOURCE_TASK,
            Constants.SimpleGraphConfig.FINAL_WEIGHT_VECTOR, trainedWeightVector);

    taskExecutor.execute(predictionGraph, predictionPlan);

    data = retrieveTestingAccuracyObject(predictionGraph, predictionPlan);

    return data;
  }

  /**
   * This method retrieves the accuracy data object from the prediction task graph
   *
   * @param predictionGraph ComputeGraph from which the final accuracy is retrieved
   * @param predictionPlan PredictionTaskGraph from which the final accuracy is retrieved
   * @return returns the Twister2 DataObject containing the accuracy object
   */
  public DataObject<Object> retrieveTestingAccuracyObject(ComputeGraph predictionGraph,
                                                          ExecutionPlan predictionPlan) {
    DataObject<Object> finalRes = taskExecutor.getOutput(predictionGraph, predictionPlan,
        Constants.SimpleGraphConfig.PREDICTION_REDUCE_TASK);

    return finalRes;
  }


  /**
   * Calculates the final accuracy by taking the dataParallelism in to consideration
   * Here the parallelism is vital as we need to know the average accuracy produced by
   * each testing data set.
   *
   * @param finalRes DataObject which contains the final accuracy
   */
  public double retriveFinalTestingAccuracy(DataObject<Object> finalRes) {
    double avgAcc = 0;
    Object o = finalRes.getPartitions()[0].getConsumer().next();
    if (o instanceof Double) {
      avgAcc = ((double) o) / dataStreamerParallelism;
      LOG.info(String.format("Testing Accuracy  : %f ", avgAcc));
    } else {
      LOG.severe("Something Went Wrong In Calculating Testing Accuracy");
    }
    return avgAcc;
  }

  public void printTaskSummary() {
    double totalMemory = ((double) Runtime.getRuntime().totalMemory()) / TimingConstants.B2MB;
    double maxMemory = ((double) Runtime.getRuntime().totalMemory()) / TimingConstants.B2MB;
    String s = "\n\n";
    s += "======================================================================================\n";
    s += "\t\t\tSVM Task Summary : [" + this.experimentName + "]\n";
    s += "======================================================================================\n";
    s += "Training Dataset [" + this.svmJobParameters.getTrainingDataDir() + "] \n";
    s += "Testing  Dataset [" + this.svmJobParameters.getTestingDataDir() + "] \n";
    s += "Total Memory [ " + totalMemory + " MB] \n";
    s += "Maximum Memory [ " + maxMemory + " MB] \n";
    s += "Data Loading Time (Training + Testing) \t\t\t\t= " + String.format("%3.9f",
        dataLoadingTime) + "  s \n";
    s += "Training Time \t\t\t\t\t\t\t= " + String.format("%3.9f", trainingTime) + "  s \n";
    s += "Testing Time  \t\t\t\t\t\t\t= " + String.format("%3.9f", testingTime) + "  s \n";
    s += "Total Time (Data Loading Time + Training Time + Testing Time) \t="
        + String.format(" %.9f", dataLoadingTime + trainingTime + testingTime) + "  s \n";
    s += String.format("Accuracy of the Trained Model \t\t\t\t\t= %2.9f", accuracy) + " %%\n";
    s += "======================================================================================\n";
    LOG.info(String.format(s));
  }

  public void saveResults() throws IOException {
    ResultsSaver resultsSaver = new ResultsSaver(this.trainingTime, this.testingTime,
        this.dataLoadingTime, this.dataLoadingTime + this.trainingTime + this.testingTime,
        this.svmJobParameters, "task");
    resultsSaver.save();
  }


}
