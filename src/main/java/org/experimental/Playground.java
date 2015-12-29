package org.experimental;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by mattg on 5/22/15.
 */
public class Playground {

    private static Playground p = new Playground();
    private volatile ConcurrentHashMap<String, List> map = new ConcurrentHashMap<>();
//    private volatile Map<String, List> map = Collections.synchronizedMap(new HashMap<String, List>());

    public static void main(String[] args) throws Exception {
        p.start();
    }

    public void start() {
        ScheduledExecutorService processExecutor = Executors.newSingleThreadScheduledExecutor();
        ScheduledExecutorService myThreadExecutor = Executors.newScheduledThreadPool(2);

        ScheduledFuture f = processExecutor.scheduleAtFixedRate(new ProcessThread(), 0, 1, TimeUnit.SECONDS);
        ScheduledFuture t1 = myThreadExecutor.scheduleAtFixedRate(new MyThread("MyThread-1", "This is message 1"), 200, 200, TimeUnit.MILLISECONDS);
        ScheduledFuture t2 = myThreadExecutor.scheduleAtFixedRate(new MyThread("MyThread-1", "This is message 2"), 200, 200, TimeUnit.MILLISECONDS);
        ScheduledFuture t3 = myThreadExecutor.scheduleAtFixedRate(new MyThread("MyThread-2", "This is message 3"), 200, 200, TimeUnit.MILLISECONDS);


    }

    public List<String> getList(String key) {

        List<String> list = map.get(key);

        if (list == null) {
            System.out.println("List was null for key " + key);
            list = new ArrayList<>();
            map.putIfAbsent(key, list);
            System.out.println("Added list to map for key " + key);
        }

//        System.out.println("Map size currently is " + map.size());

        return list;
    }

//    public void setMessage(String threadName, String msg){
//        map.putIfAbsent(threadName, msg);
//    }

    private class MyThread implements Runnable {

        private String threadName;
        private String msg;

        public MyThread(String threadName, String msg) {
            this.threadName = threadName;
            this.msg = msg;
        }

        @Override
        public void run() {
            List<String> theList = p.getList(threadName);

            if (theList.isEmpty()) {
                theList.add("ThreadName=" + threadName);
                theList.add("Msg=" + msg);
            }

        }
    }

    private class ProcessThread implements Runnable {

        @Override
        public void run() {

            System.out.println("Number of items in map:" + map.size());

            try {
                for (List<String> theList : map.values()) {

                    for (String item : theList) {
//                        System.out.println(item);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
