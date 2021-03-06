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

package edu.iu.dsc.tws.api.compute.executor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Execution plan to keep track of the tasks
 */
public class ExecutionPlan {
  /**
   * The unique id for execution plan
   */
  private String id;

  private Map<Integer, INodeInstance> nodes = new HashMap<>();

  private Map<String, Map<Integer, INodeInstance>> nodesByName = new HashMap<>();

  private List<IParallelOperation> parallelOperations = new ArrayList<>();

  /**
   * Keep track of the execution state of this plan
   */
  private ExecutionState executionState;

  public ExecutionPlan() {
    this.id = UUID.randomUUID().toString();
    this.executionState = ExecutionState.INIT;
  }

  /**
   * Add a node instance of this execution
   *
   * @param taskId the global task id
   * @param node the instance
   */
  public void addNodes(String name, int taskId, INodeInstance node) {
    nodes.put(taskId, node);
    Map<Integer, INodeInstance> instances;
    if (!nodesByName.containsKey(name)) {
      instances = new HashMap<>();
      nodesByName.put(name, instances);
    } else {
      instances = nodesByName.get(name);
    }
    instances.put(taskId, node);
  }

  public Map<Integer, INodeInstance> getNodes() {
    return nodes;
  }

  public void addOps(IParallelOperation op) {
    parallelOperations.add(op);
  }

  public List<IParallelOperation> getParallelOperations() {
    return parallelOperations;
  }

  public boolean isNodeAvailable(String taskName) {
    return nodesByName.containsKey(taskName);
  }

  public Map<Integer, INodeInstance> getNodes(String taskName) {
    return nodesByName.get(taskName);
  }

  public Set<String> getNodeNames() {
    return nodesByName.keySet();
  }

  public ExecutionState getExecutionState() {
    return executionState;
  }

  public void setExecutionState(ExecutionState executionState) {
    this.executionState = executionState;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExecutionPlan plan = (ExecutionPlan) o;
    return Objects.equals(id, plan.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "ExecutionPlan{"
        + "nodes=" + nodes
        + ", nodesName=" + nodesByName
        + ", parOps=" + parallelOperations
        + "}";
  }
}
