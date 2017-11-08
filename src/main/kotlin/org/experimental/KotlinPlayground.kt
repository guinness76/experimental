package org.experimental

/**
 * Created by mattgross on 6/26/2017.
 */
fun main(args: Array<String>) {

    println("Hello World")

    var x = 1   // variable, but only to another number
    val y = 2   // constant, cannot be changed

    var xPrime: Int = 3 // variable whose type is explicitly defined

    // Primitives
    val myBool: Boolean = false; // boolean value type
    val myChar: Char = 'a'       // character value type
    val myByte: Byte = 0x01      // 8 bits wide
    val myShort: Short = 16      // 16 bits wide
    val myInt: Int = 32          // 32 bits wide
    val myLong: Long = 64L       // 64 bits wide
    val myFloat: Float = 1.005F  // 32 bits wide
    val myDouble: Double = 2.02  // 64 bits wide

    // Primitive tricks with underscores
    val oneMillion = 1_000_000
    val creditCardNumber = 1234_5678_9012_3456L
    val socialSecurityNumber = 999_99_9999L
    val hexBytes = 0xFF_EC_DE_5E
    val bytes = 0b11010010_01101001_10010100_10010010

    // Upconverting an explicitly defined primitive
    /* var convertedInt: Int = myShort */ // This won't work. You must manually widen the value as shown below
    var convertedInt: Int = myShort.toInt()

    // Bitshifting, 'or', 'and' operations available, can shift left (shl) or shift right (shr)
    val shifted = 0x000FF000 or (1 shl 2);  // 0x000FF000 or'ed with 00000100 = 0x000FF100

    // Multiline String
    val multiString = """[
Hello
    world
]"""
    println(multiString)

    // Formatted string templating
    val lights = "four"
    val fmtString = "There are ${lights} lights!"
    println(fmtString)
    println("There are ${lights.length} letters in the word '${lights}'")

    // Null values. The ? character is needed to indicate that a value is nullable
    var theSetting: String? = null
    val flag = false;
    if(flag) {
        theSetting = "flag is true"
    }

    // Note that you cannot use println(theSetting.length) directly, as you will get a compiler error. To access, you
    // must add ? to indicate that you understand that this could be a null variable. In addition, the line below
    // will print out "null" for the length, instead of giving a NPE.
    println("theSetting length=" + theSetting?.length)

    // Using a default value when the variable is null
    println("theSetting length=" + (theSetting?.length ?: -1))

    println("append function output with default second value: " + append("system"))
    println("append function output with overridden second value: " + append("system", 2))

    println("printArgs function with two args..." )
    printArgs("-c", "-x")

    println("printArgs function with no args...")
    printArgs()

    // Nested function called from external function, where the external function is stored in a variable
    val notOdd = not(::isOdd)
    var result = notOdd(5)

    // Instead of a previously created nested function, you can define a nested function on the fly
    val notZero = not {n -> n == 0}
    result = notZero(5)

    // Similar to notZero, but we replaced 'n -> n' with the keyword 'it'. We can get away with this here since the
    // not() function only takes in 1 parameter.
    val notPositive = not {it > 0}
    result = notPositive(5)

    for(i in 0..4) {
        println("${i} notOdd=${notOdd(i)} notZero=${notZero(i)} notPositive=${notPositive(i)}")
    }

    // Class usage
    val myInstance = MyClass(3, "theInstance")
    println("myInstance.name=${myInstance.name}, type=${myInstance.name}, count*2=${myInstance.getMultipliedCount(2)}")
    myInstance.name = "theInstance_renamed"
    println("myInstance.name=${myInstance.name}, count*4=${myInstance getMultipliedCountInfix 4}")

    // Data class usage
    val theStruct = MyStruct(2, 4, 6)
    println("theStruct=${theStruct}")

    val theStructCopy = theStruct.copy(c = 12)
    println("Copied and modified theStruct, copy=${theStructCopy}")

    // Variables can be assigned directly from the struct
    val(a, b, c) = theStructCopy
    println("From copied struct, a=${a}, b=${b}, c=${c}")

    // Destructuring can also occur over a list of structs
    for((a, b, c) in listOf(theStruct, theStructCopy)) {
        println("In a loop, struct: a=${a}, b=${b}, c=${c}")
    }

    // A quick struct in a single line of code
    data class EasyStruct(var x: Int, var y: Int, var z: Int)
    val anEasyStruct = EasyStruct(5, 10, 15)
    println("anEasyStruct=${anEasyStruct}")

    // Using a singleton
    println("From singleton: ${MySingleton.echo("hello")}")
    // Note that instead of creating a new MySingleton object here, we just get back a passable reference
    val singletonHandle = MySingleton
    println(singletonHandle.echo("meh"))

    // Working with lists. Note that the <> list generic is actually optional
    // A standard list is immutable. Nothing can be added or removed
    val intList = listOf<Int>(1, 2, 3)
    val intListSize = intList.size
    val firstInt = intList.first()
    val lastInt = intList.last()
    println("intList contains ${intListSize} element(s), first=${firstInt}, last=${lastInt}")

    // You actually need to specify a mutable list using mutableListOf
    val stringList = mutableListOf<String>("one", "two", "three")
    stringList.add("three")
    stringList.remove("one")
    println("stringList contains ${stringList.size} element(s), first=${stringList.first()}, last=${stringList.last()}")

    // Working with sets
    val stringSet = mutableSetOf<String>("one", "two", "three")
    // Remember that trying to add the same value to a set just replaces the existing value
    stringSet.add("three")
    println("stringSet contains the value 'three'? ${stringSet.contains("three")}")

    // Working with maps. Like lists and sets, the map can be set up to be mutable or immutable
    // Example of a map with 2 entries. In 1 line of code, the map itself can be destructured into multiple entries
    // where you can access 'key' and 'value' variables for each entry.
    val theMap = mapOf("a" to 100, "b" to 200)
    for((key, value) in theMap) {
        println("key=${key}, value=${value}")
    }
    // Accessing a map value by key
    println("Map value under key 'a' = ${theMap["a"]}")

    // Generate a sequence of even numbers. The first parameter is the seed, the second parameter is the
    // increment function.
    val evenSeq = generateSequence(2, {it + 2})
    val evens = evenSeq.take(5).toList() // Run the function 5 times, to get a list up to 10
    println("Even number list=" + evens)

    // A map produced using various collections functions
    val anotherSeq = (1..10)
        .map { it * 2 }     // Multiply every value in the list by 2
        .filter {it % 2 == 0}   // Not actually necessary here, but shows how values can be thrown out if needed
        .groupBy {it < 10}  // Separates remaining values into two groups: one group with a value under 10 and an assigned key, and another group with no assigned key
        .mapKeys {if(it.key) "underTen" else "overTen" } // Specify the keys of the two groups
    println("Even number list using collection processing=" + anotherSeq)

    // Anything that is iterable can be a FOR loop
    for (num in evens) {
        println("evens value = $num")
    }

    for (c in "hello") {
        println(c)
    }

    // While loops are unchanged from Java
    var counter = 0
    while(counter < 5) {
        counter++
    }
    println("After while loop, counter = $counter")

    // In Java, we would use a ternary operator like this:
    // String result = (counter == 5) ? "Counter is set" : "Counter is not set";
    // But in Kotlin, the IF statement has been expanded from a branch decision to an expression that can
    // actually return a value (handy for one-liners that need to conditionally assign a value)
    val msg =  if (counter == 5) "Counter is ready" else "Counter is not ready"

    // Illustration of the "when" statement, Kotlin's replacement for if-else-if chains. The "in" keyword makes
    // this function quite handy.
    val statusAsInt = 5

    val statusAsStr = when {
        statusAsInt in 0..2 -> "low"
        statusAsInt in 3..5 -> "medium"
        statusAsInt > 5 -> "high"
        else -> "unknown"
    }

    println("Status is $statusAsStr")

    // Using "when" for specific values
    when (statusAsInt) {
        2,3 -> println("Currently in medium status, the value is lowish medium")
        4,5 -> println("Currently in medium status, the value is highish medium")
    }

    // Kotlin enum example
    var statusAsEnum = when {
        statusAsInt in 0..2 -> StatusCode.Low
        statusAsInt in 3..5 -> StatusCode.Medium
        statusAsInt > 5 -> StatusCode.High
        else -> StatusCode.Unknown
    }

    println("StatusCode enum for value $statusAsInt is $statusAsEnum")

    // String coersion example, where different types of values are coerced from their class type
    fun coerceToString(theStatus : Any) : String {
        when {
            theStatus is Int -> {
                return when {
                    statusAsInt in 0..2 -> "low"
                    statusAsInt in 3..5 -> "medium"
                    statusAsInt > 5 -> "high"
                    else -> "unknown"
                }
            }
            theStatus is StatusCode -> {
                return when (theStatus){
                    StatusCode.Low -> "low"
                    StatusCode.Medium -> "medium"
                    StatusCode.High -> "high"
                    StatusCode.Unknown -> "unknown"
                }
            }
            theStatus is String -> return theStatus
            else -> return "unknown"
        }
    }

    println("Numeric status code=${coerceToString(statusAsInt)}")
    println("Enum status code=${coerceToString(statusAsEnum)}")
    println("String status code=${coerceToString(statusAsStr)}")

    /*
    Extensions are a way to add new functionality to a class.
    This is similar to C# extension methods.
    */
    fun String.remove(c: Char): String {
        return this.filter {it != c}
    }
    println("Hello, world!".remove('l')) // => Heo, word!

}

enum class StatusCode {
    Unknown, Low, Medium, High
}

/**
 * Create a class with one constant and one modifiable variable
 */
class MyClass (val count: Int, var name:String){

    /**
     * Note that the you must specify Int as the return value. Call like so: myInstance.getMultipliedCount(5)
     */
    fun getMultipliedCount(multiplier: Int): Int {
        return count * multiplier
    }

    /**
     * Allows infix notation. Call like so: myInstance getMultipliedCountInfix 5
     */
    infix fun getMultipliedCountInfix(multiplier: Int): Int {
        return count * multiplier
    }
}

/**
 * Struct style class with 3 constants. Features include internally autogenerated toString(), equals() and hashcode()
 * functions.
 */
data class MyStruct (val a:Int, val b:Int, val c:Int)

/**
 * firstValue is explicitly defined
 * second value is explicitly defined with an overridable default value
 */
fun append(firstValue: String, secondValue: Int = 1): String {
    return firstValue.plus("_").plus(secondValue);
}

/**
 * Any number of String args is allowed, including no args
 */
fun printArgs(vararg args: String) {
    if(args.size == 0) {
        println("No args")
        return
    }
    args.forEach { arg -> println("Processing arg: " + arg) }
}

fun isOddExplicit(theValue: Int): Boolean {
    return theValue%2 == 1
}

/**
 * Same as previous function, but replace function brackets and the 'return' keyword with '=' for single line functions
 */
fun isOdd(theValue: Int): Boolean = theValue%2 == 1

/**
 * Same as previous function, but the return value is inferred (although still shown as a Boolean in Intellij hover doc)
 */
fun isOddInferred(theValue: Int) = theValue%2 == 1

/**
 * passedFunc: (Int) -> Boolean) is the definition of the nested function being passed to not()
 * (Int)  -> Boolean is the definition of the function being returned by not()
 */
fun not(passedFunc: (Int) -> Boolean): (Int)  -> Boolean {
    // theValue is tbe Int that is passed to not().
    // invoke() will run the passed function against the top-level value and invert the result.
    // Remember there are 2 steps to use this:
    // First step is to create a function with a nested function inside and assign to a variable: var invert = not(::isOdd)
    // Second step is to call the variable that holds the external function: var result = invert(5)
    return {theValue -> !passedFunc.invoke(theValue)}
}


/**
 * A Singleton. Use the "object" keyword to create.
 */
object MySingleton {
    fun echo(msg: String): String {
        return "echo " + msg
    }
}