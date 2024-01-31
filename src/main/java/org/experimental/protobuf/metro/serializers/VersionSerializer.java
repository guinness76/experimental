package org.experimental.protobuf.metro.serializers;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import org.experimental.protobuf.ProtobufSerializable;
import org.experimental.protobuf.ProtobufSerializerFactory.ProtoBufferClassNotFoundException;
import org.experimental.protobuf.ProtobufSerializerFactory.SerializableNotFoundException;
import org.experimental.protobuf.Version;
import org.experimental.protobuf.generated.VersionProto;

public class VersionSerializer implements ProtobufSerializable<Version> {
    @Override
    public Class<?> getProtoMsgClass() {
        return VersionProto.VersionPB.class;
    }

    @Override
    public Message toProtobufMessage(Version theVersion) {
        return VersionProto.VersionPB.newBuilder()
            .setMajor(theVersion.getMajor())
            .setMinor(theVersion.getMinor())
            .setBuild(theVersion.getBuild())
            .setBeta(theVersion.getBeta())
            .setRc(theVersion.getRc())
            .setSnapshot(theVersion.isSnapshot())
            .setDev(theVersion.isDev())
            .build();
    }

    @Override
    public Version fromProtobufMessage(Message theMessage)
        throws ClassNotFoundException, InvalidProtocolBufferException {
        VersionProto.VersionPB pb = (VersionProto.VersionPB) theMessage;
        int major = pb.getMajor();
        int minor = pb.getMinor();
        int rev = pb.getRev();
        int build = pb.getBuild();
        int beta = pb.getBeta();
        int rc = pb.getRc();
        boolean isSnapshot = pb.getSnapshot();
        boolean isDev = pb.getDev();

        return new Version(major, minor, rev, build, beta, rc, isSnapshot, isDev);
    }
}
