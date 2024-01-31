package org.experimental.protobuf;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import org.experimental.protobuf.ProtobufSerializerFactory.ProtoBufferClassNotFoundException;
import org.experimental.protobuf.ProtobufSerializerFactory.SerializableNotFoundException;

public interface ProtobufSerializable<T> {
    /**
     * @return the Java class corresponding to the 'message' field in the .proto file. This class is automatically
     * generated when the .proto file is compiled into a Java class.
     */
    Class<?> getProtoMsgClass();

    /**
     * Builds a Protobuf Message object from the passed Java object. Field values need to be copied from the passed
     * object to the newly created Protobuf message object.
     * @return a Protobuf Message representation of the passed Java object.
     */
    Message toProtobufMessage(T theObject);

    /**
     * Converts a Protobuf Message object back into the original Java object. Field values need to be copied from the
     * passed Protobuf object to the newly recreated Java object.
     */
    T fromProtobufMessage(Message theMessage) throws ClassNotFoundException, InvalidProtocolBufferException;
}
