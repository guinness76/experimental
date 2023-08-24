package experimental.protobuf;

import com.google.protobuf.InvalidProtocolBufferException;

public interface ProtobufSerializable<T> {
    byte[] serialize(T theObject);

    T deserialize(byte[] objectBytes) throws InvalidProtocolBufferException;
}
