package org.experimental.protobuf;

public class BasicProperty<T> implements Property<T> {
    private String name;
    private Class<? extends T> clazz;
    private T defaultValue;
    private int hcode = 0;

    /**
     * Bean compatibility...
     */
    public BasicProperty() {
    }

    public BasicProperty(String name, Class<? extends T> cls) {
        this(name, cls, null);
    }

    public BasicProperty(String name, Class<? extends T> cls, T defaultValue) {
        setName(name);
        this.clazz = cls;
        this.defaultValue = defaultValue;
    }

    public BasicProperty(Property<T> copy) {
        this(copy.getName(), copy.getType(), copy.getDefaultValue());
    }

    /**
     * Returns a new basic property with the given name, object type, and null value. Mainly used
     * for looking up other defined properties.
     */
    public static BasicProperty<Object> of(String name) {
        return new BasicProperty<>(name, Object.class, null);
    }

    @Override
    public T getDefaultValue() {
        return defaultValue;
    }

    /**
     * Final because if this was overridden then the hashcode calculated in the ctor would be wrong
     */
    @Override
    public final String getName() {
        return name;
    }

    @Override
    public Class<? extends T> getType() {
        return clazz;
    }

    /**
     * We override hashCode and equals in a special way to only look at a name. So, different implementations of a
     * particular property will be considered equivilent.
     **/
    @Override
    public int hashCode() {
        if (hcode == 0) {
            this.hcode = name == null ? 0 : name.toLowerCase().hashCode();
        }
        return hcode;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Property && ((Property) obj).getName().equalsIgnoreCase(name);
    }

    @Override
    public String toString() {
        return getName();
    }

    public Class<? extends T> getClazz() {
        return clazz;
    }

    @SuppressWarnings("unchecked")
    public void setClazz(Class<?> clazz) {
        this.clazz = (Class<T>) clazz;
    }

    public void setName(String name) {
        this.name = name;
    }

    @SuppressWarnings("unchecked")
    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = (T) defaultValue;
    }

    /**
     * Chaining version
     */
    @SuppressWarnings("unchecked")
    public BasicProperty<T> setClazz_(Class<?> clazz) {
        this.clazz = (Class<T>) clazz;
        return this;
    }

    /**
     * Chaining version
     */
    public BasicProperty<T> setName_(String name) {
        this.name = name;
        return this;
    }

    /**
     * Chaining version
     */
    @SuppressWarnings("unchecked")
    public BasicProperty<T> setDefaultValue_(Object defaultValue) {
        this.defaultValue = (T) defaultValue;
        return this;
    }

}
