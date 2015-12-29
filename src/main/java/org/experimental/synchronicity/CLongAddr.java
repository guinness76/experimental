package org.experimental.synchronicity;

import java.util.concurrent.atomic.LongAdder;

/**
 * Created by mattg on 10/19/14.
 * <p/>
 * Java 8 replacement for AtomicLong. Still uses CAS, but instead of spinning in a thread race, this class stores the
 * changed number diff somewhere else, and does not apply the change until longValue() is called. This leads to
 * faster performance over the older AtomicLong class.
 */
public class CLongAddr implements Counter {

    private final LongAdder adder = new LongAdder();

    public long getCounter() {
        return adder.longValue();
    }

    public void increment() {
        adder.increment();
    }
}
