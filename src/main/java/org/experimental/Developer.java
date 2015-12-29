package org.experimental;

/**
 * Created by mattg on 10/12/14.
 */
public class Developer implements IWorker {

    String name = "";

    @Override
    public void doWork(String work) {
        System.out.println("Coding some " + work);
    }

    public String role() {
        return "developer";
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    @Override
    public void issuePay() {
        System.out.println(name + " has been paid.");
    }

    @Override
    public String toString() {
        return name + ", role=[" + role() + "]";
    }

}
