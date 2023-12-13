package experimental.protobuf;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class SerializerFactory {
    private final ConcurrentHashMap<Class<?>, ProtobufSerializable<?>> registered = new ConcurrentHashMap<>();
    private static final SerializerFactory instance = new SerializerFactory();

    public static SerializerFactory get() {
        return instance;
    }

    // todo throw a SerializerNotFound exception if the serializer doesn't exist
    public <T> ProtobufSerializable<T> getSerializer(Class clazz) {
        return (ProtobufSerializable<T>) registered.get(clazz);
    }

    // todo throw a SerializerNotFound exception if the serializer doesn't exist
    public <T> ProtobufSerializable<T> getSerializer(String className) throws ClassNotFoundException {
        Class<?> clazz = Class.forName(className);
        return (ProtobufSerializable<T>) registered.get(clazz);
    }

    public void registerSerializer(Class<?> targetClass, ProtobufSerializable<?> theSerializer) {
        registered.put(targetClass, theSerializer);
    }

    public void unregisterSerializer(Class<?> targetClass) {
        registered.remove(targetClass);
    }
}
