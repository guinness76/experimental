package org.experimental

import groovy.transform.Memoized

/**
 * Created by user1 on 3/24/2017.
 */
class GroovyPlayground {


    // Field is both public readable and writable, getters and setters are automatically generated
    def writableField = "Not set"
    def emptyWritableField

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

        // Groovy style getter and setter usage results
        println("writableField=" + writableField)

        // String comparison; note that '==' can be used instead of '.equals()'
        if("ABC" == writableField) {
            println("Messages match")
        }
    }

    void branchAndLoop() {
        // Ternary operator, switches on whether writableField is set to a value
        def displayName = writableField ? writableField : "Not set"
        println("displayName=" + displayName)

        displayName = emptyWritableField ? writableField : "Not set"
        println("displayName=" + displayName)

        // Elvis operator, a ternary shortcut
        displayName = writableField ?: "Not set"
        println("displayName=" + displayName)

        // FOR loop over a range. First loop: i=0. Last loop: i=9
        def x = 0
        for (i in 0 .. 9) {
            printf("%d ", i)
            x += i
        }
        print("\n")

        // Iterate over a list
        def theList = ["A", "B", "C", "D", "E"]

        for (element in theList) {
            printf("%s, ", element)
        }
        print("\n\n.each\n")

        // Convert all items in the list to lowercase
        theList = theList*.toLowerCase()

        // Iterate over a list using .each
        theList.each {element -> print(element + ", ")}
        print("\n\n.eachWithIndex\n")

        // "Safe" navigation operator: emptyWritableField is null, so calling .value would normally throw a NPE.
        // But with the '?.' keyword, .value is just set to null, and no exception is thrown (unless you try to do
        // something with the null .value)
        def unknownValue = emptyWritableField?.value
        println("emptyWritableField=" + emptyWritableField + ", emptyWritableField.value=" + unknownValue)

        // Iterate while using an index. Note how the variable types can be optionally specified
        theList.eachWithIndex{ String element, int i -> printf("%s[%d]\n", element, i)}

        // Iterate over an array
        def theArray = (0..20).toArray()
        def idx = 0
        for (i in theArray) {
            printf("%d ", i)
        }
        print("\n\nMap\n")

        // 2 different ways to iterate over a map. The second way uses the built in iterator object
        def ratesMap = ["MB": "Megabytes/sec", "KB": "Kilobytes/sec"]
        ratesMap.each { key, value -> printf("%s:%s\n", key, value)}
        ratesMap.each {println it.key + ":" + it.value}     // Can also use {println "$it.key: $it.value"}
    }

    void closures() {
        def clos = { println "Hello from a closure!" }
        println "Executing the Closure:"
        clos()

        // Passing parameters to a closure
        def sum = { a, b -> println a+b }
        sum(2,4)

        // Closures may refer to variables outside the closure
        def x = 5
        def multiplyBy = { num -> num * x }
        println multiplyBy(10)

        // If only 1 parameter needs to be passed, you can use the special variable "it" to access without needing '->'
        // to specify the parameter name
        clos = { println "Message=" + it}
        clos("meh")

        // Memoization: similar to a cache, but the closure calculation result remains forever. Useful for calculations
        // where a calculation result can take advantage of the result of a previous calculation. For example,
        // factorial(5) is a single calculation if you already calculated factorial(4) and below.
        def resultFour = GroovyPlayground.factorial(4)
        println "Factorial 4 results:" + resultFour

        // Note that when this runs, the other 4 factorials have already been calculated and are saved in memory. The
        // code just needs to calculate one factorial and merge it with the previous results.
        def resultFive = GroovyPlayground.factorial(5)
        println "Factorial 5 results:" + resultFive
    }

    @Memoized
    static Long factorial(Long f) {
        println "Calculating factorial for $f"
        if (f == 0 || f == 1) {
            return 1
        }
        factorial(f - 1) * f
    }


}
