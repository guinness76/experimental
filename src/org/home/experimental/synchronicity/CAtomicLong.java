package org.home.experimental.synchronicity;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by mattg on 10/19/14.
 * <p/>
 * This counter uses CAS (compare-and-swap) processor instructions to update the value of the counter. The processor
 * instructions are direct machine code that has a better chance of updating a value without instruction overlap.
 * But if the counter gets into a race against another thread to update, then the counter keeps trying endlessly
 * until it succeeds (called a spin lock).
 */
public class CAtomicLong implements Counter {

    private final AtomicLong atomic = new AtomicLong();

    public long getCounter() {
        return atomic.get();
    }

    public void increment() {
        atomic.incrementAndGet();
    }
}
