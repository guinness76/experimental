package org.experimental.protobuf;

import java.io.ByteArrayInputStream;

import com.google.protobuf.ByteString;
import org.experimental.protobuf.ProtobufMsg.GatewayNetworkMsg;
import org.experimental.protobuf.ProtobufMsg.ProtocolHeader;
import org.experimental.protobuf.ProtobufMsg.MessageBody;

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
    Run compile-proto.bat to turn protocolheader.proto into a ProtobufMsg Java class.
    Also see https://developers.google.com/protocol-buffers/docs/javatutorial
     */
    public static void main(String[] args) throws Exception{

        ProtocolHeader header = ProtocolHeader.newBuilder()
                .setMagic(0x4941)
                .setVersion(1)
                .setMessageId(100)
                .setOpCode(1)
                .setSenderId("server1")
                .setSenderURL("http://localhost:8088/main")
                .setTargetAddress("server2")
                .build();

        String bodyStr = "This is a protocol buffer message";
        MessageBody body = MessageBody.newBuilder()
                .setMsgBody(ByteString.copyFrom(bodyStr.getBytes()))
                .build();

        GatewayNetworkMsg msg = GatewayNetworkMsg.newBuilder()
                .setHeader(header)
                .setBody(body).build();

        byte[] serialized = msg.toByteArray();

        GatewayNetworkMsg deserialized = GatewayNetworkMsg.parseFrom(serialized);
        System.out.println("Reconstructed message: " + deserialized.toString());
    }
}
