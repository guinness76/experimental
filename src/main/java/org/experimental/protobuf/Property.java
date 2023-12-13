package experimental.protobuf;

import java.io.Serializable;
import javax.annotation.Nullable;

public interface Property<T> extends Serializable {

    /**
     * The "name" of this property. Should be descriptive, but also unique. To this end, it is encouraged to make your
     * keys fully qualified by prefixing them with your module id. For example: "mymod.MyProperty". Properties defined
     * by the system start with "sys."
     */
    String getName();

    /**
     * The data type for this property.
     */
    Class<? extends T> getType();

    /**
     * The default value for new instances of this property, or null if not applicable.
     */
    @Nullable
    T getDefaultValue();
}
