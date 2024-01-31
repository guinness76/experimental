package org.experimental.protobuf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import org.experimental.protobuf.metro.ServerMessage;
import org.experimental.protobuf.metro.ServerMessage.ServerMessageHeader;
import org.experimental.protobuf.metro.serializers.AgentInfoSerializer;
import org.experimental.protobuf.metro.serializers.ServerMessageSerializer;
import org.experimental.protobuf.metro.serializers.ServiceInvocationSerializer;
import org.experimental.protobuf.metro.serializers.StringSerializer;
import org.experimental.protobuf.generated.ServerMsgProtocols.ServerMessagePB;
import org.experimental.protobuf.metro.serializers.VersionSerializer;

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
    Run compile-proto.sh to turn .proto files into generated Java classes.
    Also see https://developers.google.com/protocol-buffers/docs/javatutorial
     */
    public static void main(String[] args) throws Exception {
        ProtobufSerializable<ServerMessage> svrMsgSerializer = new ServerMessageSerializer();
        ProtobufSerializable<String> stringSerializer = new StringSerializer();
        ProtobufSerializable<AgentInfo> agentInfoSerializer = new AgentInfoSerializer();
        ProtobufSerializable<ServiceInvocation> siSerializer = new ServiceInvocationSerializer();
        ProtobufSerializable<Version> versionSerializer = new VersionSerializer();

        ProtobufSerializerFactory.get().register(ServerMessage.class, svrMsgSerializer);
        ProtobufSerializerFactory.get().register(String.class, stringSerializer);
        ProtobufSerializerFactory.get().register(AgentInfo.class, agentInfoSerializer);
        ProtobufSerializerFactory.get().register(ServiceInvocation.class, siSerializer);
        ProtobufSerializerFactory.get().register(Version.class, versionSerializer);

        sendStringMsg();
        sendAgentInfoMsg();
        sendNullMsg();
        sendServiceInvocationMsg(false);
        sendServiceInvocationMsg(true); // Include a non-Protobuf agent report object
    }

    static void sendStringMsg() throws Exception {
        System.out.println("\n** String message test **");
        boolean fileWrite = false;
        boolean fileRead = false;

        ProtobufSerializable<ServerMessage> svrMsgSerializer = ProtobufSerializerFactory.get().getByJavaClass(ServerMessage.class);
        ServerMessage sm = buildStringMsg("This is a protocol buffer message sent inside a ServerMessage");
        Message svrProtobufMsg = svrMsgSerializer.toProtobufMessage(sm);

        // A TypeRegistry is necessary because ServerMessage specifies an Any type for the body. JsonFormat can't print
        // without the TypeRegistry to define what the Any type actually is.
        System.out.println("Server message as JSON:"
            + JsonFormat.printer().usingTypeRegistry(ProtobufSerializerFactory.get().getTypeRegistry()).print(svrProtobufMsg));
        // System.out.println("Server message:" + svrProtobufMsg);

        // The last mile. At this point, we need bytes.
        byte[] serialized = svrProtobufMsg.toByteArray();
        System.out.println("Protobuf: message size in bytes=" + serialized.length);

        File outFile = new File("C:\\development\\experimental\\src\\main\\java\\org\\experimental\\protobuf\\temp\\protbuf.dat");
        if (fileWrite) {
            writeToFile(serialized, outFile);
        }

        Message deserializedServerMsgProto = null;
        if (fileRead) {
            byte[] readBytes = readFromFile(outFile);
            deserializedServerMsgProto = ServerMessagePB.parseFrom(readBytes);
        } else {
            deserializedServerMsgProto = ServerMessagePB.parseFrom(serialized);
        }

        ServerMessage reconstitutedSvrMsg = svrMsgSerializer.fromProtobufMessage(deserializedServerMsgProto);
        String bodyObj = (String) reconstitutedSvrMsg.getSource();

        System.out.println("Reconstructed ServerMessage: " + reconstitutedSvrMsg);
        System.out.println("Reconstructed message body: " + bodyObj);

        // String newFieldVal = deserialized.getHeader().getNewFieldX();
        // System.out.println("Protobuf new field value reported as: '" + newFieldVal + "'");

    }

    static void sendAgentInfoMsg() throws Exception {
        System.out.println("\n** AgentInfo test **");
        ProtobufSerializable<ServerMessage> svrMsgSerializer = ProtobufSerializerFactory.get().getByJavaClass(ServerMessage.class);

        AgentInfo agentInfo = new AgentInfo();
        agentInfo.setVersion(Version.parse("8.3.0.0"));
        agentInfo.setEdition("edge");
        agentInfo.setProjects(List.of("project1", "project2"));
        agentInfo.setDemoExpired(true);
        ServerMessage eamMsg = buildAgentInfoMsg(agentInfo);
        Message eamProtobufMsg = svrMsgSerializer.toProtobufMessage(eamMsg);

        // System.out.println("EAM Server message:" + eamProtobufMsg);
        System.out.println("EAM Server message as JSON:"
            + JsonFormat.printer().usingTypeRegistry(ProtobufSerializerFactory.get().getTypeRegistry()).print(eamProtobufMsg));
        byte[] eamSerialized = eamProtobufMsg.toByteArray();

        Message deserializedServerMsgProto = ServerMessagePB.parseFrom(eamSerialized);
        ServerMessage reconstitutedSvrMsg = svrMsgSerializer.fromProtobufMessage(deserializedServerMsgProto);
        AgentInfo reconstructed = (AgentInfo) reconstitutedSvrMsg.getSource();
        System.out.println("Reconstructed EAM ServerMessage: " + reconstitutedSvrMsg);
        System.out.println("Reconstructed AgentInfo: " + reconstructed);
    }

    static void sendNullMsg() throws Exception {
        System.out.println("\n** Empty ServerMessage body test **");
        ProtobufSerializable<ServerMessage> svrMsgSerializer = ProtobufSerializerFactory.get().getByJavaClass(ServerMessage.class);
        ServerMessage nullValMsg = buildNullBodyMsg();
        Message nullValProtobufMsg = svrMsgSerializer.toProtobufMessage(nullValMsg);

        System.out.println("Null body message as JSON:"
            + JsonFormat.printer().usingTypeRegistry(ProtobufSerializerFactory.get().getTypeRegistry()).print(nullValProtobufMsg));
        byte[] serializedBytes = nullValProtobufMsg.toByteArray();

        Message deserializedServerMsgProto = ServerMessagePB.parseFrom(serializedBytes);
        ServerMessage reconstitutedSvrMsg = svrMsgSerializer.fromProtobufMessage(deserializedServerMsgProto);
        System.out.println("Reconstructed null body ServerMessage: " + reconstitutedSvrMsg);
        if (reconstitutedSvrMsg.getSource() != null) {
            throw new Exception("Message body should have been null!");
        }
    }

    static void sendServiceInvocationMsg(boolean addReport) throws Exception {
        System.out.printf("\n** Service Invocation test (java serialization=%b) **\n", addReport);
        ProtobufSerializable<ServerMessage> svrMsgSerializer = ProtobufSerializerFactory.get().getByJavaClass(ServerMessage.class);
        ServerMessage sm = buildAgentInfoServiceInvocationMsg(addReport);
        Message smProtobufMsg = svrMsgSerializer.toProtobufMessage(sm);

        System.out.println("ServiceInvocation message as JSON:"
            + JsonFormat.printer().usingTypeRegistry(ProtobufSerializerFactory.get().getTypeRegistry()).print(smProtobufMsg));
        byte[] serializedBytes = smProtobufMsg.toByteArray();

        Message deserializedServerMsgProto = ServerMessagePB.parseFrom(serializedBytes);
        ServerMessage reconstitutedSvrMsg = svrMsgSerializer.fromProtobufMessage(deserializedServerMsgProto);
        System.out.printf("Reconstructed ServiceInvocation ServerMessage (java serialization=%b): %s\n", addReport, reconstitutedSvrMsg);
        ServiceInvocation si = (ServiceInvocation) reconstitutedSvrMsg.getSource();
        System.out.printf("Reconstructed ServiceInvocation: (java serialization=%b): %s\n", addReport, si);
    }

    static ServerMessage buildStringMsg(String bodyText) throws InvalidProtocolBufferException {
        ServerMessage sm = ServerMessage.createFor(new ServerMessageHeader("testIntent|2", "_protobuf_"), bodyText);
        sm.addHeaderValue("field2", "value2");
        return sm;
    }

    static ServerMessage buildAgentInfoMsg(AgentInfo agentInfo) {
        return ServerMessage.createFor(new ServerMessageHeader("eamIntent|2", "_protobuf_"), agentInfo);
    }

    static ServerMessage buildNullBodyMsg() {
        return ServerMessage.createFor(new ServerMessageHeader("fireActionIntent|2", "_protobuf_"), null);
    }

    static ServerMessage buildAgentInfoServiceInvocationMsg(boolean includeReport) {
        AgentInfo agentInfo = new AgentInfo();
        agentInfo.setVersion(Version.parse("8.3.0.0"));
        agentInfo.setEdition("maker");
        agentInfo.setProjects(List.of("project3", "project4"));
        agentInfo.setDemoExpired(false);

        ServiceInvocation si = null;
        if (includeReport) {
            // Protobuf serializer hasn't been created for this class yet
            AgentMetricsReport report = new AgentMetricsReport();
            report.addEvent("testEvent1");
            report.addEvent("testEvent2");
            report.addMetric(5.2);
            report.addMetric(3.1);

            Class<?>[] classes = {String.class, AgentInfo.class, AgentMetricsReport.class};
            Object[] args = {"theAgent", agentInfo, report};
            si = new ServiceInvocation("EAMService", 1, "postAgentInfo", classes, args);
        } else {
            Class<?>[] classes = {String.class, AgentInfo.class};
            Object[] args = {"theAgent", agentInfo};
            si = new ServiceInvocation("EAMService", 1, "postAgentInfo", classes, args);
        }

        return ServerMessage.createFor(new ServerMessageHeader("serviceCall|2", "_protobuf_"), si);
    }

    static byte[] writeToBytes(ServerMessagePB theMsg) {
        return theMsg.toByteArray();
    }

    static ServerMessagePB readFromBytes(byte[] bytes) throws IOException  {
        return ServerMessagePB.parseFrom(bytes);
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
