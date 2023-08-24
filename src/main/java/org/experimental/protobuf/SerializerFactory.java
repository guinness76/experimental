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
        ProtobufSerializable<T> ret = (ProtobufSerializable<T>) registered.get(clazz);
        return ret;
    }

    // todo throw a SerializerNotFound exception if the serializer doesn't exist
    public Object getSerializer(String className) throws ClassNotFoundException {
        Class<?> clazz = Class.forName(className);
        return registered.get(clazz);
    }

    public void registerSerializer(Class<?> targetClass, ProtobufSerializable<?> theSerializer) {
        registered.put(targetClass, theSerializer);
    }

    public void unregisterSerializer(Class<?> targetClass) {
        registered.remove(targetClass);
    }
}
