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
package edu.iu.dsc.tws.data;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;

import edu.iu.dsc.tws.api.data.FileSystem;
import edu.iu.dsc.tws.api.data.Path;
import edu.iu.dsc.tws.data.api.out.CSVOutputWriter;

public class OutputCSVWriterTest {

  private static final Logger LOG = Logger.getLogger(OutputCSVWriterTest.class.getName());

  private final Charset defaultCharset = StandardCharsets.UTF_8;

  private String path = null;

  @Before
  public void createFile() throws Exception {
    path = File.createTempFile("OutputFile", ".csv").getAbsolutePath();
  }

  @Test
  public void test() throws Exception {
    final CSVOutputWriter csvOutputWriter
        = new CSVOutputWriter(FileSystem.WriteMode.OVERWRITE, new Path(path));
    csvOutputWriter.writeRecord(0, "hello");
    csvOutputWriter.writeRecord(0, "1");
    csvOutputWriter.writeRecord(0, "8");
  }
}
