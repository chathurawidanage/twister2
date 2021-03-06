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
package edu.iu.dsc.tws.checkpointing.task;


import edu.iu.dsc.tws.api.checkpointing.Snapshot;

public interface CheckpointableTask {

  /**
   * This method will be called when task is initializing
   */
  void restoreSnapshot(Snapshot snapshot);

  /**
   * {@link Snapshot} should be updated in this method with new values
   */
  void takeSnapshot(Snapshot snapshot);

  /**
   * This method could be used to predefine packers
   */
  void initSnapshot(Snapshot snapshot);

  /**
   * This method will be called when task persist it's local state to the store
   */
  default void onSnapshotPersisted(Snapshot snapshot) {

  }

  /**
   * This method will be called immediately after task report it's version to the checkpoint master
   */
  default void onCheckpointPropagated(Snapshot snapshot) {

  }
}
