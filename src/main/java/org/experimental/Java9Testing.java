package org.experimental;

import java.util.List;
import java.util.Set;

import org.junit.Test;

/**
 * Created by mattgross on 12/4/2017.
 */
public class Java9Testing {

    @Test
    public void streamTesting() {
        // Produces a ImmutableCollections.ListN object from simple Strings
        List<String> easyString = List.of("A", "B", "C");
        easyString.forEach(theString -> System.out.println(theString));

        // Produces an immutable Set. Not that elements are NOT sorted in insert order
        Set<Integer> easyInts = Set.of(1, 2, 3);
        easyInts.forEach(theInt -> System.out.print(theInt + " "));
    }
}
