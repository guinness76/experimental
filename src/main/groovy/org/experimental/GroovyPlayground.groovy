package org.experimental

/**
 * Created by user1 on 3/24/2017.
 */
class GroovyPlayground {


    // Field is both public readable and writable, getters and setters are automatically generated
    def writableField = "Not set"

    /**
     * Do Groovy stuff here, man
     */
    void makeVariables(){
        // Always the best way to start
        println("Hello World")

        // Variable assignment- first an integer, then a string, then a Java date
        def x = 0
        x++

        x = "Now I am a string"
        x = new Date()

        // Create a list
        def family = ["Matt", "Jen"]
        println(family)
        family.add("Ellie")

        // Shortcut to add an element to the end of the list
        family << "Lucy"

        // Add multiple elements
        family.addAll(["A", "B"])

        // Remove an element Java style
        family.remove(4)

        // Remove an element using subtraction
        family = family - "B"

        // Iterate over a list
        family.each {person -> println(person)}

        // Iterate while using an index. Note how the variable types can be optionally specified
        family.eachWithIndex{ String person, int i -> printf("[%d]:%s\n", i, person)}

        // Non-Java way of checking for an element in a list, contains() can also be used
        def isInList = "Lucy" in family

        // Empty map
        def ratesMap = [:]

        // Two different ways to add variables to the map
        ratesMap = ["MB": "Megabytes/sec", "KB": "Kilobytes/sec"]
        ratesMap.put("B", "Bytes/sec")

        // Get a value out of the map by key
        def mb = ratesMap.MB
        println("MB maps to " + mb)

        // 2 different ways to iterate over a map. The second way uses the built in iterator object
        ratesMap.each { key, value -> printf("%s:%s\n", key, value)}
        ratesMap.each {println it.key + ":" + it.value}     // Can also use {println "$it.key: $it.value"}

        // Groovy style getter and setter usage results
        println("writableField=" + writableField)

    }
}
