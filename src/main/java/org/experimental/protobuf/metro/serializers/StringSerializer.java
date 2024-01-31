package org.experimental.protobuf.metro.serializers;

import com.google.protobuf.Message;
import org.experimental.protobuf.ProtobufSerializable;
import org.experimental.protobuf.generated.StringProtocol;
import org.experimental.protobuf.generated.StringProtocol.StringMessagePB;

public class StringSerializer implements ProtobufSerializable<String> {

    public Class<?> getProtoMsgClass() {
        return StringProtocol.StringMessagePB.class;
    }

    @Override
    public Message toProtobufMessage(String theObject) {
        return StringProtocol.StringMessagePB.newBuilder()
            .setValue(theObject)
            .build();
    }

    @Override
    public String fromProtobufMessage(Message theMessage) {
        return ((StringMessagePB) theMessage).getValue();
    }
}
