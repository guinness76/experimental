package org.home.experimental.synchronicity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by mattg on 10/19/14.
 * https://github.com/takipi/counters-benchmark
 * http://www.takipiblog.com/java-8-stampedlocks-vs-readwritelocks-and-synchronized/
 */
public class SyncMain {

    public static long TARGET_NUMBER = 100000000l;
    public static int READ_THREADS = 5;
    public static int WRITE_THREADS = 5;
    public static int ROUNDS = 1;

    private Boolean[] rounds;
    private ExecutorService es;
    private int round;
    private long start;


    public static void main(String[] args) {
        SyncMain main = new SyncMain();

    }

    public SyncMain() {

        rounds = new Boolean[ROUNDS];
        start();
    }

    public void start() {

        for (round = 0; round < ROUNDS; round++) {


//            Counter counter = (Counter) new CDirty();
//            Counter counter = (Counter) new CVolatile();
//            Counter counter = (Counter) new CSynchronized();
//            Counter counter = (Counter) new CAtomicLong();
//            Counter counter = (Counter) new CLongAddr();
//            Counter counter = (Counter) new CReadWriteLock();
            Counter counter = (Counter) new CStamped();


            rounds[round] = Boolean.FALSE;

            es = Executors.newFixedThreadPool(READ_THREADS + WRITE_THREADS);
            start = System.currentTimeMillis();

            for (int j = 0; j < READ_THREADS; j++) {
//                System.out.printf("Starting read thread [%d] for round %d%n", j, round);
                es.execute(new SyncReader(counter));

            }

            for (int j = 0; j < WRITE_THREADS; j++) {
//                System.out.printf("Starting write thread [%d] for round %d%n", j, round);
                es.execute(new SyncWriter(counter));
            }


            try {

                es.awaitTermination(10, TimeUnit.MINUTES);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Rounds are finished");
    }

    public void publish(long end) {
        synchronized (rounds[round]) {
            if (rounds[round] == Boolean.FALSE) {
                System.out.println("Total time (ms): " + (end - start));

                rounds[round] = Boolean.TRUE;
//                System.out.println("Shutting down executor by thread " + Thread.currentThread().getName());
                es.shutdownNow();
            }
        }
    }

    private class SyncReader implements Runnable {
        private final Counter counter;

        public SyncReader(Counter counter) {
            this.counter = counter;
        }

        public void run() {
            while (true) {
                if (Thread.interrupted()) {
                    break;
                }

                long count = counter.getCounter();

                if (count > SyncMain.TARGET_NUMBER) {

                    if (counter instanceof CStamped) {
                        CStamped cs = (CStamped) counter;
                        System.out.println("Stamped total optimistic misses:" + cs.getMisses());
                    }

                    publish(System.currentTimeMillis());
                    break;
                }
            }
        }
    }

    public class SyncWriter implements Runnable {
        private final Counter counter;

        public SyncWriter(Counter counter) {
            this.counter = counter;
        }

        public void run() {
            while (true) {
                if (Thread.interrupted()) {
                    break;
                }

                counter.increment();

//                try {
//                    Thread.currentThread().sleep(1);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
        }
    }
}
