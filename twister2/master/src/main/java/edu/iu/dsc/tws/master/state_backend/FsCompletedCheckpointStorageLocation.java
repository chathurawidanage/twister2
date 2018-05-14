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
package edu.iu.dsc.tws.master.state_backend;

import java.io.IOException;

import edu.iu.dsc.tws.data.fs.FileSystem;
import edu.iu.dsc.tws.data.fs.Path;
import edu.iu.dsc.tws.master.CompletedCheckpointStorageLocation;
import edu.iu.dsc.tws.master.state.StreamStateHandle;

public class FsCompletedCheckpointStorageLocation implements CompletedCheckpointStorageLocation {

  private static final long serialVersionUID = 1L;

  private final Path exclusiveCheckpointDir;

  private final String externalPointer;

  private transient FileSystem fs;

  public FsCompletedCheckpointStorageLocation(Path exclusiveCheckpointDir, String externalPointer, FileSystem fs) {
    this.exclusiveCheckpointDir = exclusiveCheckpointDir;
    this.externalPointer = externalPointer;
    this.fs = fs;
  }


  @Override
  public String getExternalPointer() {
    return null;
  }

  @Override
  public StreamStateHandle getMetadataHandle() {
    return null;
  }

  @Override
  public void disposeStorageLocation() throws IOException {

  }
}