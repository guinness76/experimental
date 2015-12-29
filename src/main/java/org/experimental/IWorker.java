package org.experimental;

/**
 * Created by matt.gross on 6/11/2014.
 */
@FunctionalInterface
public interface IWorker {

    public void doWork(String work);

    public default void issuePay() {
        System.out.println("Yay! Getting paid!");
    }

    public default String role() {
        return "peon";
    }

    public static void defaultWork(String work) {
        System.out.println("Default work:" + work);
    }
}
