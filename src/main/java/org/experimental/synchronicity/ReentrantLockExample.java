package org.experimental.synchronicity;

/**
 * Created by mattgross on 3/6/2017.
 */

import java.text.DateFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Shows how a ReentrantLock can be used to bring fairness to waiting threads. With the garden variety lock and
 * synchronization, there is no guarantee which of the two short threads will get the lock first. With the reentrant
 * lock and fairness mode, the longer waiting thread will always get the lock first.
 * @author matt.gross
 *
 * From a developerWorks article:
 * Use [ReentrantLock] when you actually need something it provides that synchronized doesn't, like
 * timed lock waits, interruptible lock waits, non-block-structured locks, multiple condition variables, or lock
 * polling. ReentrantLock also has scalability benefits, and you should use it if you actually have a situation that
 * exhibits high contention, but remember that the vast majority of synchronized blocks hardly ever exhibit any
 * contention, let alone high contention. I would advise developing with synchronization until synchronization has
 * proven to be inadequate, rather than simply assuming "the performance will be better" if you use ReentrantLock.
 * Remember, these are advanced tools for advanced users. (And truly advanced users tend to prefer the simplest tools
 * they can find until they're convinced the simple tools are inadequate.) As always, make it right first, and then
 * worry about whether or not you have to make it faster.
 *
 */
public class ReentrantLockExample {

    // Garden variety synchronization lock
    private Object OBJECT_LOCK = new Object();
    private static final int OBJECT_LOCK_MODE = 0;

    // Set reentrant lock to fair mode, to guarantee that the longest waiting thread gets the lock first
    private ReentrantLock REENTRANT_LOCK = new ReentrantLock(true);
    private static final int REENTRANT_LOCK_MODE = 1;

    private String theString = "";

    /**
     * @param args
     */
    public static void main(String[] args) {
        ReentrantLockExample main = new ReentrantLockExample();
        main.execute();
    }

    public void execute(){
        ExecutorService es = Executors.newFixedThreadPool(3);

        // Set the mode here: OBJECT_LOCK_MODE for classic synchronization, REENTRANT_LOCK_MODE for reentrant style locks.
//        int mode = OBJECT_LOCK_MODE;
        int mode = REENTRANT_LOCK_MODE;

        Runnable longThread = null;
        Runnable firstShortThread = null;
        Runnable secondShortThread = null;
        if(mode == OBJECT_LOCK_MODE){
            longThread = new MonitorThread();
            firstShortThread = new MonitorThreadTwo("aaa");
            secondShortThread = new MonitorThreadTwo("bbb");
        }
        else{
            longThread = new MonitorThreadThree();
            firstShortThread = new MonitorThreadFour("aaa");
            secondShortThread = new MonitorThreadFour("bbb");
        }

        es.submit(longThread);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Submit the first short thread. It will be blocked by the long thread above. In theory, one would think that
        // this thread would always resume first. But with standard synchronization, this is not so! In fact, there
        // is no way to know which thread will be handed the lock unless something like a reentrant lock is used.
        es.submit(firstShortThread);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        es.submit(secondShortThread);

        // Wait for all threads to finish
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        es.shutdownNow();
        System.out.println("Final result, theString=" + theString);
    }

    private class MonitorThread implements Runnable{

        @Override
        public void run() {
            synchronized (OBJECT_LOCK){
                DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG);
                Calendar c = Calendar.getInstance();
                System.out.println("Starting doThing() in lock....");
                theString = "doThing=" + df.format(c.getTime());
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("finished doThing, theString=" + theString);
        }
    }

    private class MonitorThreadTwo implements Runnable {

        private String name;
        public MonitorThreadTwo(String name){
            this.name = name;
        }

        @Override
        public void run() {
            synchronized (OBJECT_LOCK){
                DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG);
                Calendar c = Calendar.getInstance();
                System.out.println("Running MonitorThreadTwo[" + name + "] in lock");
                theString = "doOtherThing=" + df.format(c.getTime());
            }
            System.out.println("finished doOtherThing, theString=" + theString);
        }
    }

    private class MonitorThreadThree implements Runnable{

        @Override
        public void run() {

            try{
                REENTRANT_LOCK.lock();
                DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG);
                Calendar c = Calendar.getInstance();
                System.out.println("Starting doThing() in lock....");
                theString = "doThing=" + df.format(c.getTime());
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            finally{
                REENTRANT_LOCK.unlock();
            }
            System.out.println("finished doThing, theString=" + theString);


        }
    }

    private class MonitorThreadFour implements Runnable {

        private String name;
        public MonitorThreadFour(String name){
            this.name = name;
        }


        @Override
        public void run() {
            try{
                REENTRANT_LOCK.lock();
            synchronized (REENTRANT_LOCK){
                DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG);
                Calendar c = Calendar.getInstance();
                System.out.println("Running MonitorThreadTwo[" + name + "] in lock");
                theString = "doOtherThing=" + df.format(c.getTime());
            }

            }
            finally{
                REENTRANT_LOCK.unlock();
            }
            System.out.println("finished doOtherThing, theString=" + theString);
        }
    }


}
