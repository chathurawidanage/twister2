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
package edu.iu.dsc.tws.common.table.arrow;

import org.apache.arrow.memory.RootAllocator;

/**
 * Table runtime holds objects that are common to the table runtime. This object is set to
 * worker environment and accessible through it.
 */
public class TableRuntime {
  public static final String TABLE_RUNTIME_CONF = "__table_runtime__";

  private RootAllocator rootAllocator;

  public TableRuntime(RootAllocator rootAllocator) {
    this.rootAllocator = rootAllocator;
  }

  public RootAllocator getRootAllocator() {
    return rootAllocator;
  }
}
