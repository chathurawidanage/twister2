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
package edu.iu.dsc.tws.comms.dfw.io;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Assert;
import org.junit.Test;

import edu.iu.dsc.tws.api.comms.messaging.ChannelMessage;
import edu.iu.dsc.tws.api.comms.messaging.MessageHeader;
import edu.iu.dsc.tws.api.comms.messaging.types.MessageType;
import edu.iu.dsc.tws.api.comms.messaging.types.MessageTypes;
import edu.iu.dsc.tws.api.comms.packing.DataBuffer;
import edu.iu.dsc.tws.api.config.Config;
import edu.iu.dsc.tws.comms.dfw.InMessage;
import edu.iu.dsc.tws.comms.dfw.OutMessage;

public class DataSerializerTest {
  @Test
  public void testBuildLargeIntegerMessage() {
    int numBuffers = 10;
    int size = 1000;
    MessageType type = MessageTypes.INTEGER_ARRAY;
    Object data = createData(800, type);
    InMessage inMessage = singleValueCase(numBuffers, size, type, data);
    Assert.assertArrayEquals((int[]) inMessage.getDeserializedData(), (int[]) data);
  }

  @Test
  public void testBuildLargeDoubleMessage() {
    int numBuffers = 10;
    int size = 1000;
    MessageType type = MessageTypes.DOUBLE_ARRAY;
    Object data = createData(800, type);
    InMessage inMessage = singleValueCase(numBuffers, size, type, data);
    Assert.assertArrayEquals((double[]) inMessage.getDeserializedData(), (double[]) data, .01);
  }

  @Test
  public void testBuildLargeLongMessage() {
    int numBuffers = 10;
    int size = 1000;
    MessageType type = MessageTypes.LONG_ARRAY;
    Object data = createData(800, type);
    InMessage inMessage = singleValueCase(numBuffers, size, type, data);
    Assert.assertArrayEquals((long[]) inMessage.getDeserializedData(), (long[]) data);
  }

  @Test
  public void testBuildLargeShortMessage() {
    int numBuffers = 10;
    int size = 1000;
    MessageType type = MessageTypes.SHORT_ARRAY;
    Object data = createData(800, type);
    InMessage inMessage = singleValueCase(numBuffers, size, type, data);
    Assert.assertArrayEquals((short[]) inMessage.getDeserializedData(), (short[]) data);
  }

  @Test
  public void testBuildLargeByteMessage() {
    int numBuffers = 10;
    int size = 1000;
    MessageType type = MessageTypes.BYTE_ARRAY;
    Object data = createData(800, type);
    InMessage inMessage = singleValueCase(numBuffers, size, type, data);
    Assert.assertArrayEquals((byte[]) inMessage.getDeserializedData(), (byte[]) data);
  }

  @Test
  public void testBuildLargeObjectMessage() {
    int numBuffers = 20;
    int size = 1000;
    MessageType type = MessageTypes.OBJECT;
    Object data = createData(800, type);
    InMessage inMessage = singleValueCase(numBuffers, size, type, data);
    Assert.assertArrayEquals((int[]) inMessage.getDeserializedData(), (int[]) data);
  }

  private InMessage singleValueCase(int numBuffers, int size, MessageType type, Object data) {
    BlockingQueue<DataBuffer> bufferQueue = createDataQueue(numBuffers, size);

    OutMessage outMessage = new OutMessage(0, 1, -1, 10, 0, null,
        null, type, null, null, data);

    DataSerializer serializer = new DataSerializer();
    serializer.init(Config.newBuilder().build(), bufferQueue);

    List<ChannelMessage> messages = new ArrayList<>();

    while (outMessage.getSendState() != OutMessage.SendState.SERIALIZED) {
      ChannelMessage ch = serializer.build(data, outMessage);
      messages.add(ch);
    }

    DataDeserializer deserializer = new DataDeserializer();
    deserializer.init(Config.newBuilder().build());

    MessageHeader header = deserializer.buildHeader(
        messages.get(0).getBuffers().get(0), 1);
    InMessage inMessage = new InMessage(0, type,
        null, header);
    for (ChannelMessage channelMessage : messages) {
      for (DataBuffer dataBuffer : channelMessage.getBuffers()) {
        inMessage.addBufferAndCalculate(dataBuffer);
      }
    }
    deserializer.build(inMessage, 1);
    return inMessage;
  }

  @SuppressWarnings("Unchecked")
  @Test
  public void testBuildLargeListIntMessage() {
    int numBuffers = 16;
    int size = 1000;
    List<Object> data = new AggregatedObjects<>();
    for (int i = 0; i < 4; i++) {
      Object o = createData(800, MessageTypes.INTEGER_ARRAY);
      data.add(o);
    }

    InMessage inMessage = listValueCase(numBuffers, size, data, MessageTypes.INTEGER_ARRAY);
    List<Object> result = (List<Object>) inMessage.getDeserializedData();
    for (int i = 0; i < result.size(); i++) {
      Object exp = result.get(i);
      Object d = data.get(i);

      Assert.assertArrayEquals((int[]) exp, (int[]) d);
    }
  }

  @SuppressWarnings("Unchecked")
  @Test
  public void testBuildLargeListLongMessage() {
    int numBuffers = 32;
    int size = 1000;
    List<Object> data = new AggregatedObjects<>();
    for (int i = 0; i < 4; i++) {
      Object o = createData(800, MessageTypes.LONG_ARRAY);
      data.add(o);
    }

    InMessage inMessage = listValueCase(numBuffers, size, data, MessageTypes.LONG_ARRAY);
    List<Object> result = (List<Object>) inMessage.getDeserializedData();
    for (int i = 0; i < result.size(); i++) {
      Object exp = result.get(i);
      Object d = data.get(i);

      Assert.assertArrayEquals((long[]) exp, (long[]) d);
    }
  }

  @SuppressWarnings("Unchecked")
  @Test
  public void testBuildLargeListDoubleMessage() {
    int numBuffers = 32;
    int size = 1000;
    List<Object> data = new AggregatedObjects<>();
    for (int i = 0; i < 4; i++) {
      Object o = createData(800, MessageTypes.DOUBLE_ARRAY);
      data.add(o);
    }

    InMessage inMessage = listValueCase(numBuffers, size, data, MessageTypes.DOUBLE_ARRAY);
    List<Object> result = (List<Object>) inMessage.getDeserializedData();
    for (int i = 0; i < result.size(); i++) {
      Object exp = result.get(i);
      Object d = data.get(i);

      Assert.assertArrayEquals((double[]) exp, (double[]) d, 0.01);
    }
  }

  @SuppressWarnings("Unchecked")
  @Test
  public void testBuildLargeListShortMessage() {
    int numBuffers = 32;
    int size = 1000;
    List<Object> data = new AggregatedObjects<>();
    for (int i = 0; i < 4; i++) {
      Object o = createData(800, MessageTypes.SHORT_ARRAY);
      data.add(o);
    }

    InMessage inMessage = listValueCase(numBuffers, size, data, MessageTypes.SHORT_ARRAY);
    List<Object> result = (List<Object>) inMessage.getDeserializedData();
    for (int i = 0; i < result.size(); i++) {
      Object exp = result.get(i);
      Object d = data.get(i);

      Assert.assertArrayEquals((short[]) exp, (short[]) d);
    }
  }

  @SuppressWarnings("Unchecked")
  @Test
  public void testBuildLargeListByteMessage() {
    int numBuffers = 32;
    int size = 1000;
    List<Object> data = new AggregatedObjects<>();
    for (int i = 0; i < 4; i++) {
      Object o = createData(800, MessageTypes.BYTE_ARRAY);
      data.add(o);
    }

    InMessage inMessage = listValueCase(numBuffers, size, data, MessageTypes.BYTE_ARRAY);
    List<Object> result = (List<Object>) inMessage.getDeserializedData();
    for (int i = 0; i < result.size(); i++) {
      Object exp = result.get(i);
      Object d = data.get(i);

      Assert.assertArrayEquals((byte[]) exp, (byte[]) d);
    }
  }

  private InMessage listValueCase(int numBuffers, int size, List<Object> data, MessageType type) {
    BlockingQueue<DataBuffer> bufferQueue = createDataQueue(numBuffers, size);
    OutMessage outMessage = new OutMessage(0, 1, -1, 10, 0, null,
        null, type, null, null, data);

    DataSerializer serializer = new DataSerializer();
    serializer.init(Config.newBuilder().build(), bufferQueue);

    List<ChannelMessage> messages = new ArrayList<>();


    while (outMessage.getSendState() != OutMessage.SendState.SERIALIZED) {
      ChannelMessage ch = serializer.build(data, outMessage);
      messages.add(ch);
    }

    DataDeserializer deserializer = new DataDeserializer();
    deserializer.init(Config.newBuilder().build());

    MessageHeader header = deserializer.buildHeader(
        messages.get(0).getBuffers().get(0), 1);
    InMessage inMessage = new InMessage(0, type,
        null, header);
    for (ChannelMessage channelMessage : messages) {
      for (DataBuffer dataBuffer : channelMessage.getBuffers()) {
        inMessage.addBufferAndCalculate(dataBuffer);
      }
    }
    deserializer.build(inMessage, 1);
    return inMessage;
  }

  public static Object createData(int size, MessageType type) {
    if (type == MessageTypes.INTEGER_ARRAY) {
      int[] vals = new int[size];
      for (int i = 0; i < vals.length; i++) {
        vals[i] = i;
      }
      return vals;
    } else if (type == MessageTypes.LONG_ARRAY) {
      long[] vals = new long[size];
      for (int i = 0; i < vals.length; i++) {
        vals[i] = i;
      }
      return vals;
    } else if (type == MessageTypes.DOUBLE_ARRAY) {
      double[] vals = new double[size];
      for (int i = 0; i < vals.length; i++) {
        vals[i] = i;
      }
      return vals;
    } else if (type == MessageTypes.SHORT_ARRAY) {
      short[] vals = new short[size];
      for (int i = 0; i < vals.length; i++) {
        vals[i] = (short) i;
      }
      return vals;
    } else if (type == MessageTypes.BYTE_ARRAY) {
      byte[] vals = new byte[size];
      for (int i = 0; i < vals.length; i++) {
        vals[i] = (byte) i;
      }
      return vals;
    } else if (type == MessageTypes.OBJECT) {
      int[] vals = new int[size];
      for (int i = 0; i < vals.length; i++) {
        vals[i] = i;
      }
      return vals;
    } else {
      return null;
    }
  }

  private BlockingQueue<DataBuffer> createDataQueue(int numBuffers, int size) {
    BlockingQueue<DataBuffer> bufferQueue = new LinkedBlockingQueue<DataBuffer>();
    for (int i = 0; i < numBuffers; i++) {
      bufferQueue.offer(new DataBuffer(ByteBuffer.allocate(size)));
    }
    return bufferQueue;
  }
}
