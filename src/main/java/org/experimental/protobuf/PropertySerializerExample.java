package org.experimental.protobuf;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.experimental.protobuf.generated.PropertyValProtocols.PropertyProto;
import org.experimental.protobuf.generated.PropertyValProtocols.PropertyValueProto;

/**
 * Run experimental/src/main/java/org/experimental/protobuf/compile-proto.sh to turn propertyvalue.proto
 * into a generated/PropertyValProtocols Java class.
 * Also see https://developers.google.com/protocol-buffers/docs/javatutorial
 */
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

        // Simulate alarm event data
        // BasicProperty<Boolean> isEnabled = new BasicProperty<>("enabled", Boolean.class);
        // BasicProperty<String> alarmName = new BasicProperty<>("name", String.class);
        BasicProperty<String> ackPipeline = new BasicProperty<>("ackPipeline", String.class);
        BasicProperty<Boolean> shelvingAllowed = new BasicProperty<>("shelvingAllowed", Boolean.class);

        BasicProperty<AckMode> ackMode = new BasicProperty<>("ackMode", AckMode.class);
        BasicProperty<String> userProperty = new BasicProperty<>("user", String.class);
        BasicProperty<String> sourceProperty = new BasicProperty<>("source", String.class);
        BasicProperty<Double> deadbandProperty = new BasicProperty<>("deadband", Double.class);
        BasicProperty<Boolean> isSystemEvent = new BasicProperty<>("systemEvent", Boolean.class);

        BasicPropertySet ackEventAssociatedData = new BasicPropertySet();
        ackEventAssociatedData.setValue(userProperty, "operator");
        EventData ackEventData = new EventData(new Date().getTime(), ackEventAssociatedData);

        BasicPropertySet shelfEventAssociatedData = new BasicPropertySet();
        shelfEventAssociatedData.setValue(userProperty, "admin");
        shelfEventAssociatedData.setValue(sourceProperty, "local gateway");
        EventData shelfEventData = new EventData(new Date().getTime(), shelfEventAssociatedData);

        BasicAlarmEvent alarmEvent = new BasicAlarmEvent();
        alarmEvent.setAlarmName("testAlarm");
        alarmEvent.setEnabled(true);
        alarmEvent.setValue(ackMode, AckMode.Auto);
        alarmEvent.setValue(deadbandProperty, 0.5);
        alarmEvent.setValue(isSystemEvent, false);
        alarmEvent.setAckEventData(ackEventData);
        alarmEvent.setShelfEventData(shelfEventData);

        GsonBuilder builder = new GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting();

        Gson gson = builder.create();
        String asJson = gson.toJson(alarmEvent, BasicAlarmEvent.class);
        System.out.println("AlarmEvent to JSON:\n" + asJson);

        /*
        Serializing in the alarm event:
        Protobuf as much as possible the fields inside the alarm event. For custom properties, we would have
        to look up the serializer from a registry. The serializer could return one of three possibilities:
        1) a Protobuf serializer (preferred so that we could call the serializer and get bytes back straightaway).
        2) JSON text (human readable)
        3) straight bytes (not human readable)
         */
    }

    private static class BasicPropertySet {
        private final Map<Property<?>, Object> values = new HashMap<>();

        public void setValue(Property<?> prop, Object value) {
            values.put(prop, value);
        }

        public Object getValue(Property<?> prop) {
            return values.get(prop);
        }
    }

    private static class EventData extends BasicPropertySet {
        public static final BasicProperty<Date> eventStartProp = new BasicProperty<>("eventStart", Date.class);

        public EventData(long timestamp) {
            setValue(eventStartProp, new Date(timestamp));
        }

        public EventData(long timestamp, BasicPropertySet associatedData) {
            setValue(eventStartProp, new Date(timestamp));

            // In the real system, merge() is called here
            for (Entry<Property<?>, Object> entry: associatedData.values.entrySet()) {
                setValue(entry.getKey(), entry.getValue());
            }
        }
    }

    private static class BasicAlarmEvent extends BasicPropertySet {
        boolean isEnabled = true;
        String alarmName;

        BasicPropertySet ackEventData;
        BasicPropertySet shelfEventData;


        public boolean isEnabled() {
            return isEnabled;
        }

        public void setEnabled(boolean enabled) {
            isEnabled = enabled;
        }

        public String getAlarmName() {
            return alarmName;
        }

        public void setAlarmName(String alarmName) {
            this.alarmName = alarmName;
        }

        public BasicPropertySet getAckEventData() {
            return ackEventData;
        }

        public void setAckEventData(BasicPropertySet ackEventData) {
            this.ackEventData = ackEventData;
        }

        public BasicPropertySet getShelfEventData() {
            return shelfEventData;
        }

        public void setShelfEventData(BasicPropertySet shelfEventData) {
            this.shelfEventData = shelfEventData;
        }
    }

    private enum AckMode {
        Unused(0), Auto(1), Manual(2);

        int intVal;

        private AckMode(int i) {
            this.intVal = i;
        }

        /**
         * Returns the type code for this AlertAckMode
         */
        public int getIntValue() {
            return intVal;
        }

        /**
         * Returns the AlertAckMode for the given type code. Returns null if there is node defined mode for the given code
         */
        public static AckMode getTypeForValue(int val) {
            AckMode[] allTypes = values();
            if (val >= 0 && val < allTypes.length) {
                return allTypes[val];
            }
            return null;
        }
    }
}
