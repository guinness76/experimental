package org.experimental.protobuf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import com.google.protobuf.ByteString;
import com.google.protobuf.util.JsonFormat;
import experimental.protobuf.ProtobufSerializable;
import experimental.protobuf.SerializerFactory;
import experimental.protobuf.metro.ServerMessage;
import experimental.protobuf.metro.serializers.ServerMessageSerializer;
import experimental.protobuf.metro.serializers.StringSerializer;
import org.experimental.protobuf.generated.ServerMsgProtocols.ServerMessageProto;

/**
 * Created by mattgross on 3/31/2017. Protocol buffer classes offer the following advantages:
 * 1) More flexible than Java serialization, which tends to break the moment anything changes in the serialized class.
 * A machine receiving a protocol buffer class with new fields can just ignore the new fields.
 * 2) Vastly more efficient than XML, which can take up to 100 times longer to parse, and also results in larger
 * packet sizes.
 * 3) Both Java and Python stubs (along with other common languages) can be generated from the .proto file, using protoc.exe
 */
public class ProtobufExample {

    /*
    Run compile-proto.sh to turn protocolheader.proto into a ProtobufMsg Java class.
    Also see https://developers.google.com/protocol-buffers/docs/javatutorial
     */
    public static void main(String[] args) throws Exception {
        boolean fileWrite = false;
        boolean fileRead = false;

        SerializerFactory.get().registerSerializer(ServerMessage.class, new ServerMessageSerializer());
        SerializerFactory.get().registerSerializer(String.class, new StringSerializer());

        ServerMessage sm = buildServerMsg("This is a protocol buffer message sent inside a ServerMessage");
        ProtobufSerializable<ServerMessage> serializer = SerializerFactory.get().getSerializer(ServerMessage.class);
        byte[] serialized = serializer.serialize(sm);

        System.out.println("Protobuf: message size in bytes=" + serialized.length);

        File outFile = new File("C:\\development\\experimental\\src\\main\\java\\org\\experimental\\protobuf\\temp\\protbuf.dat");
        if (fileWrite) {
            writeToFile(serialized, outFile);
        }

        ServerMessage deserializedMsg = null;
        if (fileRead) {
            byte[] readBytes = readFromFile(outFile);
            deserializedMsg = serializer.deserialize(readBytes);
        } else {
            deserializedMsg = serializer.deserialize(serialized);
        }

        ServerMessageSerializer sms = (ServerMessageSerializer) serializer;
        System.out.println("ServerMessageProto output:" + JsonFormat.printer().print(sms.getAsProtoMsg(sm)));

        // Now the body needs to be reconstructed. In the real world, the codec specified in the ServerMessageHeader
        // is used to deserialize the body contents.
        ProtobufSerializable<String> stringDeserializer = SerializerFactory.get().getSerializer(String.class);
        String newBody = stringDeserializer.deserialize(deserializedMsg.getSourceStream());

        System.out.println("Reconstructed ServerMessage: " + deserializedMsg);
        System.out.println("Reconstructed message body: " + newBody);

        // String newFieldVal = deserialized.getHeader().getNewFieldX();
        // System.out.println("Protobuf new field value reported as: '" + newFieldVal + "'");
    }

    static ServerMessage buildServerMsg(String bodyText) {
        // The message contents need to be serialized first. Then the ServerMessage can be built with the header
        ProtobufSerializable<String> serializer = SerializerFactory.get().getSerializer(String.class);
        byte[] bodyBytes = serializer.serialize(bodyText);

        // todo encode the intent version
        ServerMessage sm = ServerMessage.createFor("testIntent|2", "_protobuf_", bodyBytes);
        sm.addHeaderValue("field1", "value1");
        sm.addHeaderValue("field2", "value2");
        return sm;
    }

    static byte[] writeToBytes(ServerMessageProto theMsg) {
        return theMsg.toByteArray();
    }

    static ServerMessageProto readFromBytes(byte[] bytes) throws IOException  {
        return ServerMessageProto.parseFrom(bytes);
    }

    static void writeToFile(byte[] theBytes, File theFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(theFile)) {
            fos.write(theBytes);
            fos.flush();
        }
    }

    static byte[] readFromFile(File theFile) throws IOException {
        try (FileInputStream fis = new FileInputStream(theFile)) {
            return fis.readAllBytes();
        }
    }
}
