package experimental.protobuf;

import com.google.protobuf.Any;
import org.experimental.protobuf.generated.PropertyValProtocols.PropertyProto;
import org.experimental.protobuf.generated.PropertyValProtocols.PropertyValueProto;

public class PropertySerializerExample {
    public static void main(String[] args) throws Exception {
        BasicProperty<String> systemNameProp = new BasicProperty<>("systemName", String.class);
        PropertyValue pv = new PropertyValue(systemNameProp, "controller");

        // The idea is that a list or set of PropertyValues needs to be serialized using protocol buffers.
        PropertyProto propProto = PropertyProto.newBuilder()
            .setName(systemNameProp.getName())
            .setType(systemNameProp.getType().getName())
            .build();

        PropertyValueProto propValProto = PropertyValueProto.newBuilder()
            .setProperty(propProto)
            .setStringValue((String) pv.getValue()) // The property definition indicates that we are dealing with a String
            .build();

        byte[] serialized = propValProto.toByteArray();

        PropertyValueProto deserializedPropValProto = PropertyValueProto.parseFrom(serialized);
        PropertyProto deserializedPropProto = deserializedPropValProto.getProperty();
        String propName = deserializedPropProto.getName();
        String propType = deserializedPropProto.getType();

        String deserializedValue = null;
        if ("java.lang.String".equalsIgnoreCase(propType)) {
            deserializedValue = deserializedPropValProto.getStringValue();
        }

        System.out.printf("Property name='%s', property value='%s', property class='%s'\n",
            propName, deserializedValue, propType);
    }
}
