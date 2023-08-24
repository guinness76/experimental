package experimental.protobuf.metro.serializers;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import experimental.protobuf.ProtobufSerializable;
import experimental.protobuf.metro.ServerMessage;
import org.experimental.protobuf.ProtobufMsg.ServerMessageHeaderProto;
import org.experimental.protobuf.ProtobufMsg.ServerMessageProto;

public class ServerMessageSerializer implements ProtobufSerializable<ServerMessage> {
    @Override
    public byte[] serialize(ServerMessage sm) {
        ServerMessageHeaderProto header = ServerMessageHeaderProto.newBuilder()
            .setIntentName(sm.getIntentName())
            .setIntentVersion(sm.getIntentVersion())
            .setCodecName(sm.getCodec())
            .putAllHeadersValues(sm.getHeaderValues())
            .build();

        ServerMessageProto smProto = ServerMessageProto.newBuilder()
            .setHeader(header)
            .setBody(ByteString.copyFrom(sm.getSourceStream()))
            .build();

        return smProto.toByteArray();
    }

    @Override
    public ServerMessage deserialize(byte[] bytes) throws InvalidProtocolBufferException {
        ServerMessageProto smProto = ServerMessageProto.parseFrom(bytes);
        ServerMessageHeaderProto headerProto = smProto.getHeader();
        byte[] bodyBytes = smProto.getBody().toByteArray();
        String intent = String.format("%s|%d", headerProto.getIntentName(), headerProto.getIntentVersion());

        // todo separate out the header intent version from the name
        return ServerMessage.createFor(intent, headerProto.getCodecName(), bodyBytes);
    }
}
