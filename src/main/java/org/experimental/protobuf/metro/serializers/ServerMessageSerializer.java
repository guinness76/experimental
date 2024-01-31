package org.experimental.protobuf.metro.serializers;

import java.io.IOException;
import java.io.Serializable;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import org.experimental.protobuf.ProtobufSerializable;
import org.experimental.protobuf.ProtobufSerializerFactory;
import org.experimental.protobuf.ProtobufSerializerFactory.ProtoBufferClassNotFoundException;
import org.experimental.protobuf.ProtobufSerializerFactory.SerializableNotFoundException;
import org.experimental.protobuf.metro.SerializationUtils;
import org.experimental.protobuf.metro.ServerMessage;
import org.experimental.protobuf.generated.ServerMsgProtocols.ServerMessagePB;
import org.experimental.protobuf.generated.ServerMsgProtocols.ServerMessageHeaderProto;
import org.experimental.protobuf.metro.ServerMessage.ServerMessageHeader;

public class ServerMessageSerializer implements ProtobufSerializable<ServerMessage> {

    @Override
    public Class<?> getProtoMsgClass() {
        return ServerMessagePB.class;
    }

    @Override
    public Message toProtobufMessage(ServerMessage sm) {
        ServerMessageHeaderProto header = ServerMessageHeaderProto.newBuilder()
            .setIntentName(sm.getIntentName())
            .setIntentVersion(sm.getIntentVersion())
            .setCodecName(sm.getCodec())
            .putAllHeadersValues(sm.getHeaderValues())
            .build();

        ServerMessagePB smProto = null;
        Object source = sm.getSource();
        if (source != null) {
            ProtobufSerializable pb = null;
            try {
                pb = ProtobufSerializerFactory.get().getByJavaClass(source.getClass());

                Message msg = pb.toProtobufMessage(source);
                Any body = Any.pack(msg);

                smProto = ServerMessagePB.newBuilder()
                    .setHeader(header)
                    .setBody(body)
                    .setBodyProtobufClass(pb.getProtoMsgClass().getName())
                    .build();
            } catch (SerializableNotFoundException e) {
                // Fall back to the old SerializationUtil for the time being.
                // TODO Also log a message indicating that a protobuf serializer is needed.
                try {
                    byte[] bytes = SerializationUtils.serialize((Serializable) source);
                    smProto = ServerMessagePB.newBuilder()
                        .setHeader(header)
                        .setJavaSerializedBody(ByteString.copyFrom(bytes))
                        .setUsingJavaSerialization(true)
                        .build();
                } catch (IOException ex) {
                    throw new RuntimeException(ex); // todo, maybe throw the IOException
                }
            }

        } else {
            smProto = ServerMessagePB.newBuilder()
                .setHeader(header)
                .build();
        }

        return smProto;
    }

    @Override
    public ServerMessage fromProtobufMessage(Message msg)
        throws ClassNotFoundException, InvalidProtocolBufferException {
        ServerMessagePB smProto = (ServerMessagePB) msg;
        ServerMessageHeaderProto headerProto = smProto.getHeader();

        // Rebuild the header
        ServerMessageHeader header = new ServerMessageHeader();
        header.setCodecName(headerProto.getCodecName());
        header.setIntentName(headerProto.getIntentName());

        headerProto.getHeadersValuesMap().entrySet().forEach((entry) -> {
            header.addHeaderValue(entry.getKey(), entry.getValue());
        });

        boolean usedJavaSerialization = smProto.getUsingJavaSerialization();
        if (usedJavaSerialization) {
            // Boo. Java serialization was used to serialize the message. We have to deserialize the same way.
            // todo Log a message indicating that Java serialization was used.
            try {
                Object obj = SerializationUtils.decode(smProto.getBodyProtobufClassBytes().toByteArray());
                return ServerMessage.createFor(header, obj);
            } catch (IOException e) {
                throw new RuntimeException(e); // todo
            }
        } else {
            // The body field is marked as "Any". That means we have to look up the exact Protobuf class that was
            // used to serialize the body. After we have that, we can call unpack() to get back the exact class.
            String protobufClass = smProto.getBodyProtobufClass();

            // TODO Use StringUtils.isEmpty() here
            if ("".equals(protobufClass)) {
                return ServerMessage.createFor(header, null);
            } else {
                Class<Message> clazz = (Class<Message>) Class.forName(protobufClass);
                Any anyBody = smProto.getBody();
                Message unpacked = anyBody.unpack(clazz);

                ProtobufSerializable<?> serializer = null;
                try {
                    serializer = ProtobufSerializerFactory.get().getByProtocolBufferClass(clazz);
                } catch (ProtoBufferClassNotFoundException e) {
                    throw new RuntimeException(e); // todo handle
                }
                Object obj = serializer.fromProtobufMessage(unpacked);

                return ServerMessage.createFor(header, obj);
            }
        }
    }

}
