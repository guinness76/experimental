package org.home.experimental.synchronicity;

/**
 * Created by mattg on 10/19/14.
 * <p/>
 * Classic locking thru synchronization against a generic object. Both reads and writes are restricted to a single
 * thread at a time, and you cannot read and write at the same time either.
 */
public class CSynchronized implements Counter {

    private Object lock = new Object();

    private int counter;

    public long getCounter() {
        synchronized (lock) {
            return counter;
        }
    }

    public void increment() {
        synchronized (lock) {
            ++counter;
        }
    }
}
