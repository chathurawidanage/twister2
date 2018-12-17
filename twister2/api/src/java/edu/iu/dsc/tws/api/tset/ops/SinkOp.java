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
package edu.iu.dsc.tws.api.tset.ops;

import java.util.Iterator;

import edu.iu.dsc.tws.api.tset.Sink;
import edu.iu.dsc.tws.api.tset.TSetContext;
import edu.iu.dsc.tws.common.config.Config;
import edu.iu.dsc.tws.task.api.IMessage;
import edu.iu.dsc.tws.task.api.ISink;
import edu.iu.dsc.tws.task.api.TaskContext;

public class SinkOp<T> implements ISink {
  private static final long serialVersionUID = -9398832570L;

  private Sink<T> sink;

  private boolean iterable;

  public SinkOp() {
  }

  public SinkOp(Sink<T> sink, boolean itr) {
    this.sink = sink;
    this.iterable = itr;
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean execute(IMessage message) {
    if (!iterable) {
      T data = (T) message.getContent();
      sink.add(data);
    } else {
      Iterator<T> data = (Iterator<T>) message.getContent();
      while (data.hasNext()) {
        sink.add(data.next());
      }
    }
    return true;
  }

  @Override
  public void prepare(Config cfg, TaskContext ctx) {
    TSetContext tSetContext = new TSetContext(cfg, ctx.taskIndex(), ctx.taskId(), ctx.taskName(),
        ctx.getParallelism(), ctx.getWorkerId(), ctx.getConfigurations());

    sink.prepare(tSetContext);
  }
}
