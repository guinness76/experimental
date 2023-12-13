package experimental.protobuf.metro.serializers;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import experimental.protobuf.ProtobufSerializable;
import experimental.protobuf.metro.ServerMessage;
import org.experimental.protobuf.generated.ServerMsgProtocols.ServerMessageProto;
import org.experimental.protobuf.generated.ServerMsgProtocols.ServerMessageHeaderProto;

public class ServerMessageSerializer implements ProtobufSerializable<ServerMessage> {
    @Override
    public byte[] serialize(ServerMessage sm) {
        return buildMessage(sm).toByteArray();
    }

    public ServerMessageProto getAsProtoMsg(ServerMessage sm) {
        return buildMessage(sm);
    }

    private ServerMessageProto buildMessage(ServerMessage sm) {
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

        return smProto;
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
