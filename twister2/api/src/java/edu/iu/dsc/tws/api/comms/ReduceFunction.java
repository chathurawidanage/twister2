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
package edu.iu.dsc.tws.api.comms;

import java.util.List;
import java.util.Map;

import edu.iu.dsc.tws.api.config.Config;

/**
 * Functions that are used for reduce operations have to be implementations of this interface.
 * The reduce operation takes in two objects and returns the reduced value.
 * Note: make sure not to reuse one of the incoming objects when returning the reduced values since
 * this can cause errors
 */
public interface ReduceFunction {
  /**
   * Initialize the function
   * @param cfg configuration
   * @param op options
   * @param expectedIds for each target, expected source ids
   */
  void init(Config cfg, DataFlowOperation op, Map<Integer, List<Integer>> expectedIds);

  /**
   * Reduce t1 and t2 to a single value
   * @param t1 first value
   * @param t2 second value
   * @return the reduced value
   */
  Object reduce(Object t1, Object t2);
}
