package org.experimental;

/**
 * Created by matt.gross on 6/11/2014.
 */
public class Secretary implements IWorker {

    public static final String SECRETARY_DEFAULT_ROLE = "Answers phones";
    private String name = "";
    private String role = SECRETARY_DEFAULT_ROLE;

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

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    @Override
    public String toString() {
        return "Secretary{" +
                "role='" + role + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
