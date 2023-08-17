package org.experimental.synchronicity;

import java.util.concurrent.ConcurrentHashMap;

import com.vmlens.api.AllInterleavings;
import org.junit.Assert;
import org.junit.Test;

public class VMLensTest {

    // public void update(ConcurrentHashMap<Integer, Integer> map) {
    //     Integer result = map.get(1);
    //     if (result == null) {
    //         map.put(1, 1);
    //     } else {
    //         map.put(1, result + 1);
    //     }
    // }
    // @Test
    // public void testUpdate() throws InterruptedException {
    //     try (AllInterleavings allInterleavings =
    //              new AllInterleavings("VMLensTest");) {
    //         while (allInterleavings.hasNext()) {
    //             final ConcurrentHashMap<Integer, Integer> map =
    //                 new ConcurrentHashMap<Integer, Integer>();
    //             Thread first = new Thread(() -> {
    //                 update(map);
    //             });
    //             Thread second = new Thread(() -> {
    //                 update(map);
    //             });
    //             first.start();
    //             second.start();
    //             first.join();
    //             second.join();
    //             Assert.assertEquals(2,
    //                 map.get(1).intValue());
    //         }
    //     }
    // }

    private static final Object LOCK_1 = new Object();
    private static final Object LOCK_2 = new Object();
    int i = 0;
    @Test
    public void test() throws InterruptedException {
        try (AllInterleavings allInterleavings =
                 new AllInterleavings
                     ("VMLensTest");) {
            while (allInterleavings.hasNext()) {
                Thread first = new Thread(() -> {
                    synchronized (LOCK_1) {
                        i++;
                    }
                });
                Thread second = new Thread(() -> {
                    synchronized (LOCK_2) {
                        i++;
                    }
                });
                first.start();
                second.start();

                first.join();
                second.join();
            }
        }
    }

}
