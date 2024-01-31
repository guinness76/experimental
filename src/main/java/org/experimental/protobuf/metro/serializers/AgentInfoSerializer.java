package org.experimental.protobuf.metro.serializers;

import java.util.List;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import org.experimental.protobuf.AgentInfo;
import org.experimental.protobuf.ProtobufSerializable;
import org.experimental.protobuf.ProtobufSerializerFactory;
import org.experimental.protobuf.ProtobufSerializerFactory.ProtoBufferClassNotFoundException;
import org.experimental.protobuf.ProtobufSerializerFactory.SerializableNotFoundException;
import org.experimental.protobuf.Version;
import org.experimental.protobuf.generated.AgentInfoProto;
import org.experimental.protobuf.generated.AgentInfoProto.AgentInfoPB;
import org.experimental.protobuf.generated.VersionProto.VersionPB;

public class AgentInfoSerializer implements ProtobufSerializable<AgentInfo> {
    @Override
    public Class<?> getProtoMsgClass() {
        return AgentInfoProto.AgentInfoPB.class;
    }

    @Override
    public Message toProtobufMessage(AgentInfo theObject) {
        ProtobufSerializable<Version> versionSerializer = null;
        VersionPB versionPB = null;
        try {
            versionSerializer = ProtobufSerializerFactory.get().getByJavaClass(Version.class);
            versionPB = (VersionPB) versionSerializer.toProtobufMessage(theObject.getVersion());
        } catch (SerializableNotFoundException e) {
            throw new RuntimeException(e); // todo
        }

        AgentInfoProto.AgentInfoPB.Builder builder = AgentInfoProto.AgentInfoPB.newBuilder()
            .setVersion(versionPB)
            .setEdition(theObject.getEdition())
            .setIsDemoExpired(theObject.isDemoExpired());

        List<String> projects = List.of("project1", "project2");
        builder.addAllProjects(projects);

        return builder.build();
    }

    @Override
    public AgentInfo fromProtobufMessage(Message theMessage) throws ClassNotFoundException, InvalidProtocolBufferException {
        AgentInfoPB pb = (AgentInfoPB) theMessage;

        ProtobufSerializable<Version> versionSerializer = null;
        Version version = null;
        try {
            versionSerializer = ProtobufSerializerFactory.get().getByJavaClass(Version.class);
            version = versionSerializer.fromProtobufMessage(pb.getVersion());
        } catch (ProtobufSerializerFactory.SerializableNotFoundException e) {
            throw new RuntimeException(e); // todo
        }


        AgentInfo agentInfo = new AgentInfo();
        agentInfo.setVersion(version);
        agentInfo.setEdition(pb.getEdition());
        agentInfo.setDemoExpired(pb.getIsDemoExpired());
        agentInfo.setProjects(pb.getProjectsList());
        return agentInfo;
    }
}
