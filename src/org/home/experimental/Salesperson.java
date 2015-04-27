package org.home.experimental;

/**
 * Created by mattg on 10/12/14.
 */
public class Salesperson implements IWorker {

    @Override
    public void doWork(String work) {
        System.out.println("I am a salesperson, I don't do " + work);
    }

    @Override
    public String toString() {
        return "Salesperson, role=[" + role() + "]";
    }
}
