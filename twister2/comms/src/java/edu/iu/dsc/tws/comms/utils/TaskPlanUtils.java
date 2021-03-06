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
package edu.iu.dsc.tws.comms.utils;

import java.util.HashSet;
import java.util.Set;

import edu.iu.dsc.tws.api.comms.LogicalPlan;

public final class TaskPlanUtils {
  private TaskPlanUtils() {
  }

  public static Set<Integer> getTasksOfThisWorker(LogicalPlan plan, Set<Integer> tasks) {
    Set<Integer> allTasksOfThisExec = plan.getLogicalIdsOfThisWorker();
    Set<Integer> tasksOfThisExec = new HashSet<>();

    for (int t : tasks) {
      if (allTasksOfThisExec != null && allTasksOfThisExec.contains(t)) {
        tasksOfThisExec.add(t);
      }
    }
    return tasksOfThisExec;
  }

  public static Set<Integer> getTasksOfWorker(LogicalPlan plan, int worker, Set<Integer> tasks) {
    Set<Integer> workerTasks = plan.getLogicalIdsOfWorker(worker);
    Set<Integer> newTasks = new HashSet<>(workerTasks);
    newTasks.retainAll(tasks);
    return newTasks;
  }

  public static Set<Integer> getWorkersOfTasks(LogicalPlan plan, Set<Integer> tasks) {
    Set<Integer> workersOfTasks = new HashSet<>();

    for (int t : tasks) {
      int w = plan.getWorkerForForLogicalId(t);
      workersOfTasks.add(w);
    }
    return workersOfTasks;
  }

  public static Set<Integer> getThisWorkerTasks(LogicalPlan plan) {
    return plan.getLogicalIdsOfWorker(plan.getThisWorker());
  }
}
