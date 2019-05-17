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

package edu.iu.dsc.tws.executor.core.streaming;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.iu.dsc.tws.checkpointmanager.state_backend.FsCheckpointStreamFactory;
import edu.iu.dsc.tws.common.config.Config;
import edu.iu.dsc.tws.common.kryo.KryoSerializer;
import edu.iu.dsc.tws.data.fs.Path;
import edu.iu.dsc.tws.data.fs.local.LocalDataInputStream;
import edu.iu.dsc.tws.data.fs.local.LocalFileSystem;
import edu.iu.dsc.tws.executor.core.Runtime;
import edu.iu.dsc.tws.task.api.checkpoint.Checkpointable;

public class LocalStreamingStateBackend {

  private static final Logger LOG = Logger.getLogger(LocalStreamingStateBackend.class.getName());

  public Object readFromStateBackend(Config config, int streamingTaskId,
                                     int workerId) throws IOException {

    Runtime runtime = (Runtime) config.get(Runtime.RUNTIME);
    Path path1 = new Path(runtime.getParentpath(), runtime.getJobName());
    Path path2 = new Path(path1, String.valueOf(2));
    LocalFileSystem localFileSystem = (LocalFileSystem) runtime.getFileSystem();
    FsCheckpointStreamFactory fs = new FsCheckpointStreamFactory(path2, path2,
        0, localFileSystem);
    FsCheckpointStreamFactory.FsCheckpointStateOutputStream stream =
        fs.createCheckpointStateOutputStream();

    LocalDataInputStream localDataReadStream = (LocalDataInputStream)
        stream.openStateHandle(String.valueOf(streamingTaskId),
            String.valueOf(workerId)).openInputStream();
    byte[] checkpoint;
    synchronized (this) {
      checkpoint = stream.readCheckpoint(localDataReadStream);
    }
    KryoSerializer kryoSerializer = new KryoSerializer();
    LOG.log(Level.INFO, String.valueOf(streamingTaskId) + "_" + String.valueOf(workerId)
        + " StreamTask is resumed");
    return kryoSerializer.deserialize(checkpoint);
  }

  public void writeToStateBackend(Config config, int streamingTaskId,
                                  int workerId, Checkpointable streamingTask,
                                  int checkpointID) throws Exception {
    synchronized (this) {
      File snapshots = new File("/tmp/snapshots/" + workerId);
      if (!snapshots.exists()) {
        snapshots.mkdirs();
      }


      Path path1 = new Path(snapshots.getPath());
      Path path2 = new Path(path1, String.valueOf(checkpointID));
//      LocalFileSystem localFileSystem = new LocalFileSystem();
//      FsCheckpointStreamFactory fs = new FsCheckpointStreamFactory(path2, path2,
//          0, localFileSystem);
      KryoSerializer kryoSerializer = new KryoSerializer();
//      byte[] checkpoint = kryoSerializer.serialize(streamingTask.getSnapshot());
//
//      FileOutputStream fos = new FileOutputStream(new File(snapshots, checkpointID + ""));
//      fos.write(checkpoint);
//      fos.close();
//      FsCheckpointStreamFactory.FsCheckpointStateOutputStream stream =
//          fs.createCheckpointStateOutputStream();
//      stream.initialize(String.valueOf(streamingTaskId), String.valueOf(workerId));
//      stream.write(checkpoint);
//      stream.closeWriting();
    }
  }

}
