package org.experimental.protobuf.metro;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

// Stand-in for the real SerializationUtil in metro
public class SerializationUtils {
    private SerializationUtils() {

    }

    public static byte[] serialize(Serializable object) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(bout)){
            oos.writeObject(object);
        }
        return bout.toByteArray();
    }

    public static Object decode(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return in.readObject();
        }
    }
}
