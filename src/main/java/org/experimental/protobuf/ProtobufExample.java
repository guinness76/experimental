package org.experimental.protobuf;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

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
    Run compile-proto.sh to turn protocolheader.proto into a ProtobufMsg Java class.
    Also see https://developers.google.com/protocol-buffers/docs/javatutorial
     */
    public static void main(String[] args) throws Exception{
        boolean fileWrite = true;
        boolean fileRead = true;

        GatewayNetworkMsg msg = buildMsg();
        byte[] serialized = writeToBytes(msg);
        System.out.println("Protobug: message size in bytes=" + serialized.length);

        File outFile = new File("C:\\development\\experimental\\src\\main\\java\\org\\experimental\\protobuf\\temp\\protbuf.dat");
        if (fileWrite) {
            writeToFile(msg, outFile);
        }

        GatewayNetworkMsg deserialized = null;
        if (fileRead) {
            deserialized = readFromFile(outFile);
        } else {
            deserialized = readFromBytes(serialized);
        }
        System.out.println("Reconstructed message: " + deserialized.toString());

        // String newFieldVal = deserialized.getHeader().getNewFieldX();
        // System.out.println("Protobuf new field value reported as: '" + newFieldVal + "'");

        // todo Next step is to see how this system deals with change, aka field added/removed/change definition
    }



    static GatewayNetworkMsg buildMsg() {
        ProtocolHeader header = ProtocolHeader.newBuilder()
            .setMagic(0x4941)
            .setVersion(1)
            .setMessageId(100)
            .setOpCode(1)
            .setSenderId("server1")
            .setSenderURL("http://localhost:8088/main")
            .setTargetAddress("server2")
            // .setNewFieldX("BBB")
            .build();

        String bodyStr = "This is a protocol buffer message";
        MessageBody body = MessageBody.newBuilder()
            .setMsgBody(ByteString.copyFrom(bodyStr.getBytes()))
            .build();

        return GatewayNetworkMsg.newBuilder()
            .setHeader(header)
            .setBody(body).build();
    }

    static byte[] writeToBytes(GatewayNetworkMsg theMsg) {
        return theMsg.toByteArray();
    }

    static GatewayNetworkMsg readFromBytes(byte[] bytes) throws IOException  {
        return GatewayNetworkMsg.parseFrom(bytes);
    }

    static void writeToFile(GatewayNetworkMsg theMsg, File theFile) throws Exception {
        FileOutputStream fos = new FileOutputStream(theFile);
        fos.write(theMsg.toByteArray());
        fos.flush();
        fos.close();
    }

    static GatewayNetworkMsg readFromFile(File theFile) throws Exception {
        FileInputStream fis = new FileInputStream(theFile);
        byte[] fileBytes = fis.readAllBytes();
        return readFromBytes(fileBytes);
    }
}
