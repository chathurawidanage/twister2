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

import edu.iu.dsc.tws.api.comms.messaging.ChannelMessage;
import edu.iu.dsc.tws.api.comms.messaging.MessageHeader;
import edu.iu.dsc.tws.api.comms.messaging.types.MessageType;
import edu.iu.dsc.tws.api.comms.messaging.types.MessageTypes;
import edu.iu.dsc.tws.api.comms.packing.DataBuffer;
import edu.iu.dsc.tws.api.comms.structs.Tuple;
import edu.iu.dsc.tws.api.config.Config;
import edu.iu.dsc.tws.comms.dfw.InMessage;
import edu.iu.dsc.tws.comms.dfw.OutMessage;

public class BaseSerializeTest {
  public InMessage keyedListValueCase(int numBuffers, int size, List<Object> data,
                                      MessageType type, MessageType keyType) {
    BlockingQueue<DataBuffer> bufferQueue = createDataQueue(numBuffers, size);
    OutMessage outMessage = new OutMessage(0, 1, -1, 10, 0, null,
        null, type, keyType, null, data);

    KeyedDataSerializer serializer = new KeyedDataSerializer();
    serializer.init(Config.newBuilder().build(), bufferQueue);

    List<ChannelMessage> messages = new ArrayList<>();


    while (outMessage.getSendState() != OutMessage.SendState.SERIALIZED) {
      ChannelMessage ch = serializer.build(data, outMessage);
      messages.add(ch);
    }

    KeyedDataDeSerializer deserializer = new KeyedDataDeSerializer();
    deserializer.init(Config.newBuilder().build());

    MessageHeader header = deserializer.buildHeader(
        messages.get(0).getBuffers().get(0), 1);
    InMessage inMessage = new InMessage(0, type,
        null, header);
    inMessage.setKeyType(keyType);
    for (ChannelMessage channelMessage : messages) {
      for (DataBuffer dataBuffer : channelMessage.getBuffers()) {
        inMessage.addBufferAndCalculate(dataBuffer);
      }
    }
    deserializer.build(inMessage, 1);
    return inMessage;
  }

  public InMessage keyedSingleValueCase(int numBuffers, int size, MessageType type,
                                        MessageType keyType, Object data) {
    BlockingQueue<DataBuffer> bufferQueue = createDataQueue(numBuffers, size);

    OutMessage outMessage = new OutMessage(0, 1, -1, 10, 0, null,
        null, type, keyType, null, data);

    KeyedDataSerializer serializer = new KeyedDataSerializer();
    serializer.init(Config.newBuilder().build(), bufferQueue);

    List<ChannelMessage> messages = new ArrayList<>();

    while (outMessage.getSendState() != OutMessage.SendState.SERIALIZED) {
      ChannelMessage ch = serializer.build(data, outMessage);
      messages.add(ch);
    }

    KeyedDataDeSerializer deserializer = new KeyedDataDeSerializer();
    deserializer.init(Config.newBuilder().build());

    MessageHeader header = deserializer.buildHeader(
        messages.get(0).getBuffers().get(0), 1);
    InMessage inMessage = new InMessage(0, type,
        null, header);
    inMessage.setKeyType(keyType);
    for (ChannelMessage channelMessage : messages) {
      for (DataBuffer dataBuffer : channelMessage.getBuffers()) {
        inMessage.addBufferAndCalculate(dataBuffer);
      }
    }
    deserializer.build(inMessage, 1);
    return inMessage;
  }

  public BlockingQueue<DataBuffer> createDataQueue(int numBuffers, int size) {
    BlockingQueue<DataBuffer> bufferQueue = new LinkedBlockingQueue<DataBuffer>();
    for (int i = 0; i < numBuffers; i++) {
      bufferQueue.offer(new DataBuffer(ByteBuffer.allocate(size)));
    }
    return bufferQueue;
  }

  public Object createDataObject(int size, MessageType dataType) {
    if (dataType == MessageTypes.INTEGER_ARRAY) {
      int[] vals = new int[size];
      for (int i = 0; i < vals.length; i++) {
        vals[i] = i;
      }
      return vals;
    } else if (dataType == MessageTypes.LONG_ARRAY) {
      long[] vals = new long[size];
      for (int i = 0; i < vals.length; i++) {
        vals[i] = i;
      }
      return vals;
    } else if (dataType == MessageTypes.DOUBLE_ARRAY) {
      double[] vals = new double[size];
      for (int i = 0; i < vals.length; i++) {
        vals[i] = i;
      }
      return vals;
    } else if (dataType == MessageTypes.SHORT_ARRAY) {
      short[] vals = new short[size];
      for (int i = 0; i < vals.length; i++) {
        vals[i] = (short) i;
      }
      return vals;
    } else if (dataType == MessageTypes.BYTE_ARRAY) {
      byte[] vals = new byte[size];
      for (int i = 0; i < vals.length; i++) {
        vals[i] = (byte) i;
      }
      return vals;
    } else if (dataType == MessageTypes.OBJECT) {
      byte[] vals = new byte[size];
      for (int i = 0; i < vals.length; i++) {
        vals[i] = (byte) i;
      }
      return vals;
    } else {
      return null;
    }
  }

  public Object createKeyObject(MessageType dataType, int size) {
    if (dataType == MessageTypes.BYTE_ARRAY) {
      byte[] vals = new byte[size];
      for (int i = 0; i < vals.length; i++) {
        vals[i] = (byte) i;
      }
      return vals;
    } else if (dataType == MessageTypes.INTEGER) {
      return 1;
    } else {
      return null;
    }
  }

  public Object createKeyObject(MessageType dataType) {
    if (dataType == MessageTypes.INTEGER) {
      return 1;
    } else if (dataType == MessageTypes.LONG) {
      return 1L;
    } else if (dataType == MessageTypes.DOUBLE) {
      return 1.0;
    } else if (dataType == MessageTypes.SHORT) {
      return (short) 1;
    } else if (dataType == MessageTypes.BYTE_ARRAY) {
      byte[] vals = new byte[8];
      for (int i = 0; i < vals.length; i++) {
        vals[i] = (byte) i;
      }
      return vals;
    } else {
      return null;
    }
  }

  public Object createKeyedData(int size, MessageType dataType, MessageType keyType) {
    Object data = createDataObject(size, dataType);
    Object key = createKeyObject(keyType);
    return new Tuple(key, data);
  }

  public Object createKeyedData(int size, MessageType dataType, int keySize, MessageType keyType) {
    Object data = createDataObject(size, dataType);
    Object key = createKeyObject(keyType, keySize);
    return new Tuple(key, data);
  }
}
