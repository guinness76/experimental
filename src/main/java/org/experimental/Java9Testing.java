package org.experimental;

import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

import org.junit.Test;

public class Java9Testing {

    @Test
    public void testCollectionCreate() {
        // Produces an *immutable* list. An alternative to Arrays.asList()
        List stringList = List.of("a", "b", "c");

        // Produces an *immutable* set. Unlike a list, the elements will not be in the order they were inserted.
        Set stringSet = Set.of("d", "e", "f");
    }

    @Test
    public void testPrivateInterfaceMethods() {
        /*
        LocalDevice is an interface extended from the Device interface. Both have private methods. The Device private
        method is empty, while the LocalDevice method prints device properties to the console.
         */
        LocalDevice localDevice = new LocalDevice() {
            @Override
            public boolean init(Properties deviceProps) {
                return false;
            }
        };

        Properties localProps = new Properties();
        localProps.setProperty("address", "localhost");
        localProps.setProperty("port", "8096");
        // This will print the properties to the console
        localDevice.setup(localProps);

    }

    @Test
    public void testStreamOptional() {
        Optional<List<String>> nonEmptyOpt = Optional.of(List.of("A", "B", "C"));
        long listItems = nonEmptyOpt.stream().count();
        System.out.printf("Non-null list contains %d items\n", listItems);

        // In Java 9, an empty Optional's stream can be accessed. This results in a stream with 0 items, so we
        // don't have to do awkward Optional checking before starting a stream pipeline.
        Optional<List<String>> emptyOpt = Optional.ofNullable(null);
        listItems = emptyOpt.stream().count();
        System.out.printf("Null list contains %d items\n", listItems);
    }


}
