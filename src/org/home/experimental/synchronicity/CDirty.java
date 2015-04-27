package org.home.experimental.synchronicity;

/**
 * Created by mattg on 10/19/14.
 * <p/>
 * This is how a counter would perform without any attempt to lock anything. Compare this as a baseline against the
 * other counters that do lock while incrementing. Note that the counter may display different values to different
 * threads, due to the fact that it is not volatile. This is because writing to a variable in Java occurs in multiple
 * instructions. Writing and reading the same variable from different threads can cause the CPU instructions to overlap,
 * and the wrong value can be returned in the reading thread. Use the volatile keyword on a variable to partially
 * prevent this behavior.
 */
public class CDirty implements Counter {

    private long counter;

    public long getCounter() {
        return counter;
    }

    public void increment() {
        ++counter;
    }
}
