package org.home.experimental.synchronicity;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by mattg on 10/19/14.
 * <p/>
 * This counter uses a ReadWriteLock. This only works well if you tend to do a lot more reading than writing. Equal
 * amounts of read/write will hurt performance more than other locking methods.
 * <p/>
 * Read lock- a thread that obtains this lock will ensure that no writer can obtain the write lock until all read
 * locks have been unlocked. A read lock cannot be obtained if a different thread is using the write lock.
 * <p/>
 * Write lock- a thread that obtains this lock will ensure that no other writes and no reads can occur until this
 * lock is released.
 */
public class CReadWriteLock implements Counter {

    private ReadWriteLock rwlock = new ReentrantReadWriteLock();

    private Lock rlock = rwlock.readLock();
    private Lock wlock = rwlock.writeLock();

    private long counter;

    public long getCounter() {
        try {
            rlock.lock();
            return counter;
        } finally {
            rlock.unlock();
        }
    }

    public void increment() {
        try {
            wlock.lock();
            ++counter;
        } finally {
            wlock.unlock();
        }
    }
}
