package org.experimental;

import java.util.Properties;

/**
 * Demonstrates how a private method can now be overridden in an interface in Java 9
 */
public interface LocalDevice extends Device {

    // To call printDeviceProps() in this interface, you must provide another default setup() method that calls it
    default boolean setup(Properties deviceProps) {
        printDeviceProps(deviceProps);
        return false;
    }

    private void printDeviceProps(Properties deviceProps) {
        // The base printDeviceProps method is empty in the parent interface.
        System.out.println("Device properties: " + deviceProps.toString());
    }
}
