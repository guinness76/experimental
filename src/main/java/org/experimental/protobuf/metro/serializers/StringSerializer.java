package experimental.protobuf.metro.serializers;

import com.google.protobuf.InvalidProtocolBufferException;
import experimental.protobuf.ProtobufSerializable;
import org.experimental.protobuf.ProtobufMsg.ServerMessageBodyProto;

public class StringSerializer implements ProtobufSerializable<String> {
    @Override
    public byte[] serialize(String theObject) {
        ServerMessageBodyProto bodyProto = ServerMessageBodyProto.newBuilder()
            .setMsgBody(theObject)
            .build();
        return bodyProto.toByteArray();
    }

    @Override
    public String deserialize(byte[] objectBytes) throws InvalidProtocolBufferException {
        ServerMessageBodyProto bodyProto = ServerMessageBodyProto.parseFrom(objectBytes);
        return bodyProto.getMsgBody();
    }
}
