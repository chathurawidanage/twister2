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
package edu.iu.dsc.tws.examples.ml.svm.constant;

public final class Constants {

  private Constants() {
  }

  public abstract class SimpleGraphConfig {

    public static final String DATA_EDGE = "data-edge";

    public static final String REDUCE_EDGE = "reduce-edge";

    public static final String STREAMING_EDGE = "streaming-edge";

    public static final String DATASTREAMER_SOURCE = "datastreamer_source";

    public static final String ITERATIVE_DATASTREAMER_SOURCE = "iterative_datastreamer_source";

    public static final String ITERATIVE_STREAMING_DATASTREAMER_SOURCE = "iterative_streaming"
        + "_datastreamer_source";

    public static final String SVM_COMPUTE = "svm_compute";

    public static final String SVM_REDUCE = "svm_reduce";

    public static final String ITERATIVE_SVM_REDUCE = "itr_svm_reduce";

    public static final String ITERATIVE_STREAMING_SVM_COMPUTE = "itr_streaming_svm_compute";

    public static final String INPUT_DATA = "input_data";

    public static final String INPUT_WEIGHT_VECTOR = "input_weight_vector";

    public static final String DATA_OBJECT_SOURCE = "data_source";

    public static final String DATA_OBJECT_COMPUTE = "data_compute";

    public static final String DATA_OBJECT_SINK = "data_sink";

    public static final String WEIGHT_VECTOR_OBJECT_SOURCE = "weight_vector_source";

    public static final String WEIGHT_VECTOR_OBJECT_COMPUTE = "weight_vector_compute";

    public static final String WEIGHT_VECTOR_OBJECT_SINK = "weight_vector_sink";

    public static final String INPUT_TESTING_DATA = "input_testing_data";

    public static final String DATA_OBJECT_SOURCE_TESTING = "data_source_test";

    public static final String DATA_OBJECT_COMPUTE_TESTING = "data_compute_test";

    public static final String DATA_OBJECT_SINK_TESTING = "data_sink_test";

    public static final String DELIMITER = ",";

    public static final String PREDICTION_SOURCE_TASK = "prediction_source_task";

    public static final String PREDICTION_REDUCE_TASK = "prediction_reduce_task";

    public static final String PREDICTION_EDGE = "prediction_edge";

    public static final String TEST_DATA = "test_data";

    public static final String FINAL_WEIGHT_VECTOR = "final_weight_vector";

    public static final String CROSS_VALIDATION_DATA = "test_data";

    public static final String SVM_RUN_TYPE = "svm_run_type"; // tset, task, comms, etc

    public static final String TSET_RUNNER = "tset";

    public static final String TASK_RUNNER = "task";

    public static final String ITERATIVE_TASK_RUNNER = "itr-task";

    public static final String ITERATIVE_TASK_STREAMING_RUNNER = "itr-str-task";

    public static final String COMMS_RUNNER = "comms";

    public static final String IO_ACCURACY = "svm-accuracy";

    public static final String IO_DATA = "svm-data";

    public static final String IO_PRIMARY_DATA = "svm-primary-data";

    public static final String IO_DATA_REDUCE = "svm-data-reduce";

    public static final String IO_WEIGHT_VECTOR = "svm-weight-vector";

    public static final String IO_WEIGHT_VECTOR_REDUCE = "svm-weight-vector-reduce";

    public static final String IO_ITR_STREAM = "svm-itr-stream";
  }
}
