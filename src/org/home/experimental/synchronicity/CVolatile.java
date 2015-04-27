package org.home.experimental.synchronicity;

/**
 * Created by mattg on 10/19/14.
 * <p/>
 * Similar to the CDirty counter, but at least in this version, the counter will display at the same value across all
 * threads due to use of the volatile keyword on the counter. Works well when you have one write thread and several
 * read threads, but using more than one write thread can cause a value to get accidentally overwritten (such as when
 * using a += b, which is not atomic).
 */
public class CVolatile implements Counter {

    private volatile long counter;

    public long getCounter() {
        return counter;
    }

    public void increment() {
        ++counter;
    }

}
