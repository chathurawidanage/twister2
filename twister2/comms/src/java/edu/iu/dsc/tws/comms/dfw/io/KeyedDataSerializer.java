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
import java.util.Queue;

import edu.iu.dsc.tws.api.comms.messaging.MessageFlags;
import edu.iu.dsc.tws.api.comms.messaging.types.MessageTypes;
import edu.iu.dsc.tws.api.comms.packing.DataBuffer;
import edu.iu.dsc.tws.api.comms.packing.DataPacker;
import edu.iu.dsc.tws.api.comms.structs.Tuple;
import edu.iu.dsc.tws.api.config.Config;
import edu.iu.dsc.tws.comms.dfw.OutMessage;

/**
 * This serializer will be used to serialize messages with keys
 */
public class KeyedDataSerializer extends BaseSerializer {

  @Override
  public void init(Config cfg, Queue<DataBuffer> buffers) {
    this.sendBuffers = buffers;
  }

  /**
   * Builds the body of the message. Based on the message type different build methods are called
   *
   * @param payload the message that needs to be built
   * @param sendMessage the send message object that contains all the metadata
   * @param targetBuffer the data targetBuffer to which the built message needs to be copied
   * @return true if the body was built and copied to the targetBuffer successfully,false otherwise.
   */
  public boolean serializeSingleMessage(Object payload,
                                        OutMessage sendMessage, DataBuffer targetBuffer) {
    Object value = null;
    Object key = new byte[0];
    DataPacker keyPacker = sendMessage.getKeyType().getDataPacker();
    DataPacker valuePacker = sendMessage.getDataType().getDataPacker();
    if ((sendMessage.getFlags() & MessageFlags.SYNC_BARRIER) == MessageFlags.SYNC_BARRIER) {
      value = payload;
      keyPacker = MessageTypes.BYTE_ARRAY.getDataPacker();
      valuePacker = MessageTypes.BYTE_ARRAY.getDataPacker();
    } else {
      Tuple tuple = (Tuple) payload;
      value = tuple.getValue();
      key = tuple.getKey();
    }
    return serializeKeyedData(value, key,
        valuePacker,
        keyPacker,
        sendMessage.getSerializationState(), targetBuffer);
  }

  /**
   * Helper method that builds the body of the message for keyed messages.
   *
   * @param payload the message that needs to be built
   * @param key the key associated with the message
   * @param state the state object of the message
   * @param targetBuffer the data targetBuffer to which the built message needs to be copied
   * @return true if the body was built and copied to the targetBuffer successfully,false otherwise.
   */
  protected boolean serializeKeyedData(Object payload, Object key,
                                       DataPacker dataPacker, DataPacker keyPacker,
                                       SerializeState state,
                                       DataBuffer targetBuffer) {
    ByteBuffer byteBuffer = targetBuffer.getByteBuffer();
    // okay we need to serialize the header
    if (state.getPart() == SerializeState.Part.INIT) {
      int keyLength = keyPacker.determineLength(key, state);
      state.getActive().setTotalToCopy(keyLength);

      // now swap the data store.
      state.swap();

      // okay we need to serialize the data
      int dataLength = dataPacker.determineLength(payload, state);
      state.setCurrentHeaderLength(dataLength + keyLength);
      state.getActive().setTotalToCopy(dataLength);

      state.swap(); //next we will be processing key, so need to swap

      state.setPart(SerializeState.Part.HEADER);
    }

    if (state.getPart() == SerializeState.Part.HEADER) {
      // first we need to copy the data size to buffer
      if (buildSubMessageHeader(targetBuffer, state.getCurrentHeaderLength(),
          state.getActive().getTotalToCopy(), keyPacker)) {
        // now set the size of the buffer
        targetBuffer.setSize(byteBuffer.position());
        return false;
      }
      state.setPart(SerializeState.Part.KEY);
    }

    if (state.getPart() == SerializeState.Part.KEY) {
      // this call will copy the key length to buffer as well
      boolean complete = DataPackerProxy.writeDataToBuffer(
          keyPacker,
          key,
          byteBuffer,
          state
      );
      // now set the size of the buffer
      targetBuffer.setSize(byteBuffer.position());
      if (complete) {
        state.swap(); //if key is done, swap to activate saved data object
        state.setPart(SerializeState.Part.BODY);
      }
    }

    // now we can serialize the body
    if (state.getPart() != SerializeState.Part.BODY) {
      // now set the size of the buffer
      targetBuffer.setSize(byteBuffer.position());
      return false;
    }

    // now lets copy the actual data
    boolean completed = DataPackerProxy.writeDataToBuffer(
        dataPacker,
        payload,
        byteBuffer,
        state
    );
    // now set the size of the buffer
    targetBuffer.setSize(byteBuffer.position());

    // okay we are done with the message
    return state.reset(completed);
  }

  /**
   * Builds the sub message header which is used in multi messages to identify the lengths of each
   * sub message. The structure of the sub message header is |length + (key length)|. The key length
   * is added for keyed messages
   */
  private boolean buildSubMessageHeader(DataBuffer buffer, int length,
                                        int keyLength, DataPacker keyPacker) {
    ByteBuffer byteBuffer = buffer.getByteBuffer();
    int requiredSpace = keyPacker.isHeaderRequired()
        ? MAX_SUB_MESSAGE_HEADER_SPACE : NORMAL_SUB_MESSAGE_HEADER_SIZE;
    if (byteBuffer.remaining() < requiredSpace) {
      return true;
    }
    int keyLengthSize = keyPacker.isHeaderRequired() ? Integer.BYTES : 0;
    byteBuffer.putInt(length + keyLengthSize);
    if (keyPacker.isHeaderRequired()) {
      byteBuffer.putInt(keyLength);
    }
    return false;
  }
}
