package org.home.experimental;

/**
 * Stolen from http://www.javacodegeeks.com/2014/05/java-8-features-tutorial.html
 */
public class Value< T > {
    
    public static< T > T defaultValue() {
        return null;
    }

     
    public T getOrDefault( T value, T defaultValue ) {
        return ( value != null ) ? value : defaultValue;
    }
}
