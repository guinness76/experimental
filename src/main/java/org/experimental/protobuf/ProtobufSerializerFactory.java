package org.experimental.protobuf;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.util.JsonFormat.TypeRegistry;

public class ProtobufSerializerFactory {
    private final ConcurrentHashMap<Class<?>, ProtobufSerializable<?>> registered = new ConcurrentHashMap<>();
    private TypeRegistry typeRegistry = null;
    private static final ProtobufSerializerFactory instance = new ProtobufSerializerFactory();

    public static ProtobufSerializerFactory get() {
        return instance;
    }

    public synchronized void register(Class<?> targetClass, ProtobufSerializable<?> theSerializer) {
        registered.put(targetClass, theSerializer);
        registered.put(theSerializer.getProtoMsgClass(), theSerializer);
        updateTypeRegistry();
    }

    public synchronized void unregister(Class<?> targetClass) {
        ProtobufSerializable<?> serializer = registered.get(targetClass);
        if (serializer != null) {
            registered.remove(targetClass);
            registered.remove(serializer.getProtoMsgClass());
        }
        updateTypeRegistry();
    }

    private void updateTypeRegistry() {
        List<Descriptor> descriptors = new ArrayList<>();
        registered.values().forEach(val -> {
            Class clazz = val.getProtoMsgClass();
            try {
                Method method = clazz.getMethod("getDescriptor");
                Object result = method.invoke(clazz, null);
                if (result instanceof Descriptor) {
                    descriptors.add((Descriptor) result);
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);  // todo throw some other exception
            }
        });

        typeRegistry = TypeRegistry.newBuilder().add(descriptors).build();
    }

    public <T> ProtobufSerializable<T> getByJavaClass(Class<?> clazz) throws SerializableNotFoundException {
        if (!registered.containsKey(clazz)) {
            throw new SerializableNotFoundException(clazz);
        } else {
            return (ProtobufSerializable<T>) registered.get(clazz);
        }
    }

    public <T> ProtobufSerializable<T> getByProtocolBufferClass(Class<?> protocolBufferClass)
        throws ProtoBufferClassNotFoundException {
        if (!registered.containsKey(protocolBufferClass)) {
            throw new ProtoBufferClassNotFoundException(protocolBufferClass);
        } else {
            return (ProtobufSerializable<T>) registered.get(protocolBufferClass);
        }
    }

    public synchronized TypeRegistry getTypeRegistry() {
        return typeRegistry;
    }

    /**
     * Throw when no ProtobufSerializable can be found for the specified Java class. A ProtobufSerializable
     * implementation is required to serialize a Java class into protocol buffers to pass over the gateway network. This
     * exception can get thrown when attempting to serialize a message.
     */
    public static class SerializableNotFoundException extends Exception {
        public SerializableNotFoundException(Class clazz) {
            super(String.format("ProtobufSerializable implementation not found for Java class '%s'", clazz.getName()));
        }
    }

    /**
     * Throw when the ProtocolBuffer class specifed in the ServerMessage bodyProtobufClass field can't be found.
     * This class is auto-generated from a .proto file. This exception can get thrown when attempting to deserialize
     * a message.
     */
    public static class ProtoBufferClassNotFoundException extends Exception {
        public ProtoBufferClassNotFoundException(Class protocolBufferClass) {
            super(String.format("ProtobufSerializable implementation not found for protocol buffer class '%s'",
                protocolBufferClass.getName()));
        }
    }
}
