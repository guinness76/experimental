package org.experimental.protobuf;

import javax.annotation.Nullable;

public class PropertyValue {
    private Property property;
    private Object value;

    public PropertyValue() {
    }

    public PropertyValue(Property prop, @Nullable Object value) {
        this.property = prop;
        this.value = value;
    }

    public Property getProperty() {
        return property;
    }

    @Nullable
    public Object getValue() {
        return value;
    }

    public static PropertyValue of(Property prop, @Nullable Object value) {
        return new PropertyValue(prop, value);
    }
}
