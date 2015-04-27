package org.home.experimental.synchronicity;

import java.util.concurrent.locks.StampedLock;

/**
 * Created by mattg on 10/19/14.
 * <p/>
 * Java 8 replacement of ReadWriteLock. A long value stamp is given when the write lock is accessed. This same stamp
 * must be used to unlock the write lock. The stamp MUST never be passed outside the local code, as StampedLocks are
 * not re-entrant. This means that if local code calls outside code, and the outside code tries to access the counter
 * again, the thread can deadlock against itself. This issue does not occur with ReadWriteLock, which is re-entrant.
 * <p/>
 * A StampedLock can offer much greater performance over a ReadWriteLock, at the expense of more complexity and being
 * harder to manage.
 */
public class CStamped implements Counter {

    private StampedLock rwlock = new StampedLock();

    private long counter;
    private long misses = 0;
//    public long s, t;

    public long getCounter() {

        // See if the value can be retrieved the easy way using an optimistic read.
        long stamp = rwlock.tryOptimisticRead();

        try {
            long result = counter;

            // Easy way worked, return from here.
            if (rwlock.validate(stamp)) {
                return result;
            }

            misses++;

            // Have to do this the hard way, using a full read lock.
            stamp = rwlock.readLock();
            result = counter;
            rwlock.unlockRead(stamp);

            return result;
        } finally {

        }
    }

    public void increment() {
        long stamp = rwlock.writeLock();

        try {
            ++counter;
        } finally {
            rwlock.unlockWrite(stamp);
        }
    }

    public long getMisses() {
        return misses;
    }
}
