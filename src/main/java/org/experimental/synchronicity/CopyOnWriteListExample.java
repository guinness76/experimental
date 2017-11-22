package org.experimental.synchronicity;

import java.util.Arrays;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by mattgross on 11/22/2017.
 */
public class CopyOnWriteListExample {

    public static void main(String[] args) throws Exception {

        /*
        CopyOnWriteArrayList allows all the values of a concurrent list to be accessed without needing to synchronize
        and lock the list. Grabbing an iterator provides a copy of the list as it looks at that exact moment. The
        iterator can then be browsed at leisure and other threads can add to the list without affecting the iterator.
        To get an updated copy of the list later, just grab another iterator. Note that the iterator cannot be used
        to add or remove from the list.
         */
        ExecutorService es = Executors.newFixedThreadPool(2);
        CopyOnWriteArrayList<Integer> theList = new CopyOnWriteArrayList<>(Arrays.asList(1, 3, 5, 7));
        ListIterator<Integer> iterator = theList.listIterator();
        es.execute(() -> {
            try {
                Thread.sleep(200);
                System.out.println("\nAdding 9 to list");
                theList.add(9);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });

        System.out.println("List before adding 9:");
        iterator.forEachRemaining(value -> System.out.print(value + ","));
        Thread.sleep(1000);

        ListIterator<Integer> anotherIterator = theList.listIterator();
        System.out.println("\nList after adding 9:");
        anotherIterator.forEachRemaining(value -> System.out.print(value + ","));
        System.exit(0);
    }
}
