package org.experimental.protobuf.metro.serializers;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import org.experimental.protobuf.ProtobufSerializable;
import org.experimental.protobuf.ProtobufSerializerFactory;
import org.experimental.protobuf.ProtobufSerializerFactory.ProtoBufferClassNotFoundException;
import org.experimental.protobuf.ProtobufSerializerFactory.SerializableNotFoundException;
import org.experimental.protobuf.ServiceInvocation;
import org.experimental.protobuf.generated.ServiceInvocationProto;
import org.experimental.protobuf.generated.ServiceInvocationProto.ServiceInvocationPB;
import org.experimental.protobuf.metro.SerializationUtils;
import org.experimental.protobuf.metro.ServerMessage;

public class ServiceInvocationSerializer implements ProtobufSerializable<ServiceInvocation> {
    @Override
    public Class<?> getProtoMsgClass() {
        return ServiceInvocationProto.ServiceInvocationPB.class;
    }

    @Override
    public Message toProtobufMessage(ServiceInvocation si) {
        ServiceInvocationProto.ServiceInvocationPB.Builder builder =
            ServiceInvocationProto.ServiceInvocationPB.newBuilder()
            .setServiceId(si.getServiceId())
            .setMethodName(si.getMethodName())
            .setVersion(si.getVersion());

        List<String> argClasses = new ArrayList<>(si.getArgTypes().length);
        for (Class<?> clazz: si.getArgTypes()) {
            argClasses.add(clazz.getName());
        }
        builder.addAllArgClasses(argClasses);

        List<Any> anyArgs = new ArrayList<>(si.getArgs().length);
        boolean javaSerializationNeeded = false;
        for(Object arg: si.getArgs()) {
            ProtobufSerializable pb = null;
            try {
                pb = ProtobufSerializerFactory.get().getByJavaClass(arg.getClass());
            } catch (SerializableNotFoundException e) {
                // Abort Protobuf serialization and fall back to the old SerializationUtil for the time being.
                // TODO Also log a message indicating that a protobuf serializer is needed.
                javaSerializationNeeded = true;
                break;
            }
            Message msg = pb.toProtobufMessage(arg);
            anyArgs.add(Any.pack(msg));
        }

        if (javaSerializationNeeded) {
            builder.setUsingJavaSerialization(true);

            List<ByteString> serializedArgs = new ArrayList<>();
            for(Object arg: si.getArgs()) {
                try {
                    byte[] bytes = SerializationUtils.serialize((Serializable) arg);
                    serializedArgs.add(ByteString.copyFrom(bytes));
                } catch (IOException e) {
                    throw new RuntimeException(e); // todo, maybe throw the IOException
                }
            }
            builder.addAllJavaSerializedArgs(serializedArgs);
        } else {
            builder.addAllArgs(anyArgs);
        }


        return builder.build();
    }

    @Override
    public ServiceInvocation fromProtobufMessage(Message theMessage) throws ClassNotFoundException,
        InvalidProtocolBufferException {
        ServiceInvocationPB siPB = (ServiceInvocationPB) theMessage;

        // Translate the list of classname Strings into an array of Classes
        List<String> argClasses = siPB.getArgClassesList();
        List<Class> argClassList = new ArrayList<>(argClasses.size());
        for (String argClassName: argClasses) {
            Class clazz = Class.forName(argClassName);
            argClassList.add(clazz);
        }
        Class[] classesArr = argClassList.toArray(new Class[0]);

        List<Object> originalArgs = null;
        if (siPB.getUsingJavaSerialization()) {
            // Boo. Java serialization was used to serialize the message. We have to deserialize the same way.
            // todo Log a message indicating that Java serialization was used.
            List<ByteString> serializedArgs = siPB.getJavaSerializedArgsList();
            originalArgs = new ArrayList<>(serializedArgs.size());
            for(ByteString theArg: serializedArgs) {
                try {
                    Object obj = SerializationUtils.decode(theArg.toByteArray());
                    originalArgs.add(obj);
                } catch (IOException e) {
                    throw new RuntimeException(e); // todo
                }
            }
        } else {
            // Translate the list of Any objects into the appropriate classes
            // todo throw an exception if the class name list and Any list aren't equal in size
            List<Any> anyArgs = siPB.getArgsList();
            originalArgs = new ArrayList<>(anyArgs.size());
            try {
                for (int i=0; i<anyArgs.size(); i++) {
                    Any item = anyArgs.get(i);
                    ProtobufSerializable<?> serializer = ProtobufSerializerFactory.get().getByJavaClass(classesArr[i]);
                    Class protoClass = serializer.getProtoMsgClass();
                    Message unpacked = item.unpack(protoClass);
                    Object obj = serializer.fromProtobufMessage(unpacked);
                    originalArgs.add(obj);
                }
            } catch (SerializableNotFoundException e) {
                throw new RuntimeException(e);  // todo handle
            }
        }

        Object[] originalObj = originalArgs.toArray(new Object[0]);
        return new ServiceInvocation(siPB.getServiceId(), siPB.getVersion(), siPB.getMethodName(), classesArr, originalObj);
    }
}
