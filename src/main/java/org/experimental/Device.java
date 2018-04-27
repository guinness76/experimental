package org.experimental;

import java.util.Properties;

/**
 * Created by mattg on 4/13/15.
 */
public interface Device {

    boolean init(Properties deviceProps);

    default boolean setup(Properties deviceProps) {
        // Note that you can only call internal private interface methods with *default* interface methods
        printDeviceProps(deviceProps);
        return false;
    }

    private void printDeviceProps(Properties deviceProps) {
        // empty implementation
    }
}
