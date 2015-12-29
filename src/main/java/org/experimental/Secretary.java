package org.experimental;

/**
 * Created by matt.gross on 6/11/2014.
 */
public class Secretary implements IWorker {

    private String role = "Answers phones";

    public Secretary() {

    }

    public Secretary(String role) {
        this.role = role;
    }

    /**
     * Called if the Optional that was supposed to contain a secretary was
     * actually null.
     *
     * @return
     */
    public static Secretary nullSecretary() {
        return new Secretary("The secretary was fired by Don Draper");
    }

    @Override
    public void doWork(String work) {
        System.out.println(work);
    }

    public String role() {
        return role;
    }

    @Override
    public String toString() {
        return "Secretary, role=[" + role + "]";
    }
}
