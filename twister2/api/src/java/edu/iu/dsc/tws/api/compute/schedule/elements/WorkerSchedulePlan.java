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
package edu.iu.dsc.tws.api.compute.schedule.elements;

import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class is responsible for assigning the container id, task instances, required
 * resource, and scheduled resource for the task instances.
 */
public class WorkerSchedulePlan implements Comparable<WorkerSchedulePlan> {
  /**
   * The worker id
   */
  private final int containerId;
  /**
   * Task instances assigned to this worker
   */
  private final Set<TaskInstancePlan> taskInstances;

  /**
   * Resources of this worker
   */
  private final Resource requiredResource;

  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  private final Optional<Resource> scheduledResource;

  /**
   * Create a schedule plan for a worker
   * @param id worker id
   * @param instances instances assigned to the worker
   * @param requiredResource resources
   */
  public WorkerSchedulePlan(int id, Set<TaskInstancePlan> instances, Resource requiredResource) {
    this(id, instances, requiredResource, null);
  }

  /**
   * Create a worker schedule plan
   * @param id worker id
   * @param taskInstances instances
   * @param requiredResource required resources
   * @param scheduledResource scheduled resource
   */
  public WorkerSchedulePlan(int id,
                            Set<TaskInstancePlan> taskInstances,
                            Resource requiredResource,
                            Resource scheduledResource) {
    this.containerId = id;
    this.taskInstances = new TreeSet<>(taskInstances);
    this.requiredResource = requiredResource;
    this.scheduledResource = Optional.ofNullable(scheduledResource);
  }

  public int getContainerId() {
    return containerId;
  }

  public Set<TaskInstancePlan> getTaskInstances() {
    return taskInstances;
  }

  public Resource getRequiredResource() {
    return requiredResource;
  }

  public Optional<Resource> getScheduledResource() {
    return scheduledResource;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    WorkerSchedulePlan that = (WorkerSchedulePlan) o;

    return containerId == that.containerId
        && getRequiredResource().equals(that.getRequiredResource())
        && getScheduledResource().equals(that.getScheduledResource());
  }


  @Override
  public int hashCode() {
    int result = containerId;
    result = 31 * result + getTaskInstances().hashCode();
    result = 31 * result + getRequiredResource().hashCode();
    if (scheduledResource.isPresent()) {
      result = (31 * result) + getScheduledResource().get().hashCode();
    }
    return result;
  }

  @Override
  public int compareTo(WorkerSchedulePlan o) {
    return Integer.compare(this.containerId, o.containerId);
  }
}
