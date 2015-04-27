package org.home.experimental;

import org.junit.Test;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by mattg on 6/10/14. <br> http://www.javacodegeeks.com/2014/05/java-8-features-tutorial.html
 * http://confluex.com/blog/java-8-lambda-expressions-2/
 */
public class Java8Testing {

    @Test
    public void interfaceTesting() {

        String myWork = "Charlie work";

        /* Call a default method on the interface */
        IWorker secretary = new Secretary();
        secretary.issuePay();

        /* Custom interface that has been converted to a functional interface */
        /* Before Java 8 */
        Workforce.makeWorkerWork(myWork, new IWorker() {

            @Override
            public void doWork(String theWork) {
                System.out.println(theWork);
            }
        });

        /* Lambda with custom interior. At least one parameter is required in the IWorker doWork method. */
        Workforce.makeWorkerWork(myWork, theWorker -> System.out.println(myWork));

        /* Lambda that calls existing static method on IWorker */
        Workforce.makeWorkerWork(myWork, IWorker::defaultWork);
    }

    /**
     * Shows how a lambda can be used to simplify a Java interface that has had the @FunctionalInterface annotation
     * added for Java 8. This lambda takes no parameters.
     */
    @Test
    public void lambdaNoParamTesting() {

        /* Classic Runnable implementation before Java 8 */
        Runnable r = new Runnable() {
            @Override
            public void run() {
                System.out.println("Hello world!");
            }
        };

        /* Java 8 version of Runnable using a lambda. Note how empty parens are used when no variables are passed. */
        Runnable r2 = () -> {
            System.out.println("Hello Lambda Expression!");
        };
    }

    /**
     * Shows how lambdas can be used with standard Java interfaces to create new objects on the fly.
     */
    @Test
    public void lambdaImplementOneMethod() {

        /* Create a new implementation of an interface without a lambda */
        Device d = new Device() {

            @Override
            public boolean setup(Properties deviceProps) {
                return deviceProps.containsKey("x");
            }
        };

        /* Use a lambda to create the implementation. */
        Device ld = (deviceProps) -> {
            // Implementation of setup() is here
            return deviceProps.containsKey("x");
        };

        /* Use this style of lambda when we always want to return true, regardless of the passed properties */
        Device alwaysOn = (deviceProps) -> true;
        Device alwaysOn2 = (deviceProps -> true); // Equivalent! But this only works when there is one parameter.
        Device alwaysOn3 = deviceProps -> true; // Equivalent! But this only works when there is one parameter.

        Device alwaysOn4 = (deviceProps -> {
            return true;
        }); // Equivalent! But this only works when there is one parameter.
    }

    /**
     * Shows how a lambda can be used to simplify a Java interface that has had the @FunctionalInterface annotation
     * added for Java 8. This lambda takes two parameters.
     */
    @Test
    public void lambdaTwoParamTesting() {
        List<Person> roster = Person.createRoster();

        Person[] rosterAsArray = roster.toArray(new Person[roster.size()]);

        /* Sorting an array using a custom comparator */
        /* Before Java 8 */
        class PersonAgeComparator implements Comparator<Person> {
            public int compare(Person a, Person b) {
                return a.getBirthday()
                        .compareTo(b.getBirthday());
            }
        }

        Arrays.sort(rosterAsArray, new PersonAgeComparator());

        /*
         * Lambda, custom interior. Note how "public int compare(Person a, Person b)" has been simplified to
         * "(Person a, Person b) ->"
         */
        Arrays.sort(rosterAsArray, (Person a, Person b) -> {
            return a.getBirthday()
                    .compareTo(b.getBirthday());
        });

        /* Lambda reusing the existing static function compareByAge() in Person */
        Arrays.sort(rosterAsArray, (Person a, Person b) -> Person.compareByAge(a, b));

        /* And another abbreviation of the Person params */
        Arrays.sort(rosterAsArray, (a, b) -> Person.compareByAge(a, b));

        /* Finally, an abbreviation of the params and the compareByAge function */
        Arrays.sort(rosterAsArray, Person::compareByAge);

    }

    /**
     * Demonstrates the Function interface. A Function accepts an object of one class (type T) and returns an object of
     * a different class (result of type R). Use a Function to convert objects, or maybe to look up an object based on
     * another object.
     */
    @Test
    public void functionTesting() {

        List<Person> roster = Person.createRoster();

        /*
         * Function without using lambda. This function takes in a Person, and returns the person's gender as a
         * Person.Sex emum
         */
        Function<Person, Person.Sex> funcOld = new Function<Person, Person.Sex>() {

            @Override
            public Person.Sex apply(Person person) {
                return person.getGender();
            }
        };

        /*
         * The same function using a lambda. We still use a Function<T,U>, but T is known to be a Person because the
         * original roster List is a List<Person>. U is known to be Person.Sex, because we call getGender(), which
         * returns a Person.Sex enum.
         */
        Function<Person, Person.Sex> funcNew = Person::getGender;

        /*
         * The same function defined directly into the map() method, which takes a Function<T,U>. We could have passed
         * funcNew to map() if we wanted to. The method below is retrieving all the Persons from the List, extracting
         * the Person.Sex for each, and couting the final result.
         */
        long numOfMales = roster.stream()
                                .map(Person::getGender)
                                .count();

        // Set up a HashMap for BiFunction example
        HashMap<String, String> peopleInfo = new HashMap<>();
        Person theFirst = roster.get(0);
        peopleInfo.put("first", theFirst.getName());

        // Example of a BiFunction (non-lambda form). BiFunction<T,U,R> takes in 2 args (T and U) and returns the result as R.
        BiFunction<String, String, String> oldBiFunction = new BiFunction<String, String, String>() {
            @Override
            public String apply(String key, String value) {
                return value += " is the first";
            }
        };

        // Lambda example of BiFunction, where the apply() method is hidden behind the lambda.
        // The HashMap.computeIfPresent() method will directly change a value in a map if the key matches.
        peopleInfo.computeIfPresent("first", (key, value) -> value += " is the first");
    }

    /**
     * Demonstrates the Consumer functional interface. A Consumer processes the object(s) provided to it (the processing
     * is also known as side effects). A Consumer uses a template T to identify the object being passed to the accept()
     * method. The accept() method does the actual work.
     */
    @Test
    public void consumerTesting() {

        List<Person> roster = Person.createRoster();
        HashMap<String, Person> people = new HashMap<>();

        /* Iterating thru a list and running an existing function on each element */
        /* Before Java 8 */
        for (Person p : roster) {
            p.printPerson();

            // Putting this here for convenience to build up the Hashmap, used in the BiConsumer example
            people.put(p.getName(), p);
        }

        /*
         * Using the Consumer interface without a lambda. Note how the template of the Consumer matches the template of
         * the List.
         */
        roster.forEach(new Consumer<Person>() {

            @Override
            public void accept(Person person) {
                person.printPerson();
            }
        });

        /*
         * Lambda. The List.forEach method signature is forEach(Consumer<T> action). In this case, the new Consumer call
         * and its accept() method are mostly hidden. Person::printPerson means create a new Consumer<Person> and run
         * printPerson() on the passed Person from the List.
         */
        roster.forEach(Person::printPerson);

        /*
         * Using Consumer.andThen() setup. In this case, we create two Consumers. Note how in the first Consumer, we use
         * '::'. This is because accept's only parameter is a Person, and it returns void. In the second Consumer, we
         * use '->', because we are calling a random method. We define the name of the object being passed in within
         * parentheses, and the method we want to run on the right side of the'->'.
         */
        Consumer<Person> print = Person::printPerson;
        Consumer<Person> addToMap = (personToAdd) -> people.put(personToAdd.getName(), personToAdd);

        /* Now we call one consumer first, and then chain the consumer we want to run second */
        roster.forEach(print.andThen(addToMap));

        /*
         * BiConsumer- how it would look if it existed before Java 8. Note how the only difference between Consumer and
         * BiConsumer is the fact that the BiConsumer accept() takes 2 args instead of 1. The arg object types are still
         * determined by the template values (T,U)
         */
        BiConsumer<String, Person> printAgesOld = new BiConsumer<String, Person>() {
            @Override
            public void accept(String name, Person person) {
                System.out.println(name + ":" + person.getAge());
            }
        };

        /*
         * BiConsumer using a lambda. The two passed-in objects that are passed to the hidden accept() are in the
         * parentheses.
         */
        BiConsumer<String, Person> printAgesNew = (name, thePerson) -> System.out.println(name + ":"
                + thePerson.getAge());

        /* Now run the BiConsumer against the map of people */
        people.forEach(printAgesNew);

        /*
         * This is an even fancier version of the BiConsumer. It only works because Person.printGender() is static AND
         * it takes a String and a Person as the only method arguments.
         */
        BiConsumer<String, Person> printGender = Person::printGender;

        /*
         * Same idea as the BiConsumer above, but this version runs against a method on a standard Person instance,
         * instead of a static method. Note that it requires a Person object with the same String and Person parameters.
         */
        Person someone = roster.get(0);
        BiConsumer<String, Person> printGenderInstance = someone::printGenderInstance;

    }

    /**
     * Demonstrates the Predicate functional interface. A Predicate tests values and returns true or false. The tested
     * value's class is determined by the template T of the Predicate.
     */
    @Test
    public void predicateTesting() {

    }

    /**
     * Demonstrates the Supplier functional interface. A Supplier provides a value every time it is called (such as an
     * incrementing value). The value's class is determined by the template T used by the Supplier.
     */
    @Test
    public void supplierTesting() {

    }

    /**
     * Demonstrates various properties of CompletableFuture
     */
    @Test
    public void completableFutureTesting() {

    }

    @FilterBy("name")
    @FilterBy("pay")
    @Test
    public void annotationTesting() {
        /* The annotations around this method are Java 8-style in that more than one is allowed. */
    }

    @Test
    public void parameterTesting() {

        /*
         * In the examples below, the return type Value.defaultValue() is figured out on the fly. Before Java 8, you
         * would have needed to use Value.< String >defaultValue().
         */
        Value<String> value = new Value<>();
        String theValueAsStr = value.getOrDefault("22", Value.defaultValue());

        Value<Integer> value2 = new Value<Integer>();
        Integer value2AsInt = value2.getOrDefault(42, Value.defaultValue());
    }

    @Test
    public void optionalTesting() {

        // This secretary definitely exists within the Optional wrapper.
        Optional<IWorker> oSecretary = Optional.ofNullable(new Secretary());

        System.out.println("This secretary should be present:");

        // Since the secretary exists, the orElseGet portion will not be called.
        String role = (oSecretary.orElseGet(Secretary::nullSecretary)).role();
        System.out.println("Role from present secretary: " + role);

        // Since the secretary exists, the issuePay method can be called.
        oSecretary.ifPresent(IWorker::issuePay);

        // This secretary is "null".
        Optional<Secretary> maybeSecretary = Optional.ofNullable(null);
        System.out.println("This secretary may not be present:");

        // In this case, the orElseGet method will call Secretary.nullSecretary(),
        // which will return a temporary Secretary that is customized when an 
        // unexpected null condition has been hit.
        String nullRole = maybeSecretary.orElseGet(Secretary::nullSecretary)
                                        .role();

        // Another way: the orElse method will return a custom String when the secretary in the Optional is null.
        String nullRoleTwo = maybeSecretary.map(Secretary::role)
                                           .orElse("No secretary present");

        System.out.println("Role from null secretary: " + nullRole);

        // The secretary is null, so issuePay() will not be called.
        maybeSecretary.ifPresent(IWorker::issuePay);

        // Another way to create a new secretary that requires parameters to create
        Optional<Secretary> optSecretary = Optional.empty();
        Secretary theTemp = optSecretary.orElse(new Secretary("temp"));

        System.out.println("Role from temp secretary: " + theTemp.role());

    }

    @Test
    public void dateTimeTesting() {

        /*
         * Java 8 Date/Time method conventions:
         * 
         * now: static factory, Create an instance at the current date/time of: static factory, Creates an instance
         * where the factory is primarily validating the input parameters, not converting them. from: static factory,
         * Converts the input parameters to an instance of the target class, which may involve losing information from
         * the input. parse: static factory, Parses the input string to produce an instance of the target class. format:
         * instance, Uses the specified formatter to format the values in the temporal object to produce a string. get:
         * instance, Returns a part of the state of the target object. is: instance, Queries the state of the target
         * object (isBefore, isAfter, isLeapYear, etc) with: instance, Returns a copy of the target object with one
         * element changed; this is the immutable equivalent to a set method on a JavaBean. plus: instance, Returns a
         * copy of the target object with an amount of time added. minus: instance, Returns a copy of the target object
         * with an amount of time subtracted. to: instance, Converts this object to another type. at: instance, Combines
         * this object with another
         */

        // Available date enums
        DayOfWeek monday = DayOfWeek.MONDAY;
        Locale locale = Locale.getDefault();
        System.out.println("Monday full text: " + monday.getDisplayName(TextStyle.FULL, locale));
        System.out.println("Monday narrow text: " + monday.getDisplayName(TextStyle.NARROW, locale));
        System.out.println("Monday short text:" + monday.getDisplayName(TextStyle.SHORT, locale));

        Month october = Month.OCTOBER;
        System.out.println("October full text:" + october.getDisplayName(TextStyle.FULL, locale));
        System.out.println("October narrow text:" + october.getDisplayName(TextStyle.NARROW, locale));
        System.out.println("October short text:" + october.getDisplayName(TextStyle.SHORT, locale));

        // now:
        LocalDate today = LocalDate.now();

        // from- allows a LocalDate to be created from different date and time types that implement the TemporalAccessor interface
        LocalDate aDay = LocalDate.from(LocalDate.of(1970, 5, 15));

        // parse:
        // format:
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate parsedDate = LocalDate.parse("09/28/2014", formatter);
        System.out.println("Parsed date:" + parsedDate.format(formatter));

        // get:
        // This is a LocalDate, so some ChronoFields are unavailable.
        int currentWeekday = parsedDate.get(ChronoField.DAY_OF_WEEK);
        System.out.println("The current day of the week is:" + currentWeekday);

        // is:
        boolean after = parsedDate.isAfter(aDay);

        // with:
        // plus:
        // To get to the 21st of next month from any day this month. Note that you must actually add 20 days.
        LocalDate dueDate = today.with(TemporalAdjusters.firstDayOfNextMonth())
                                 .plusDays(20);

        // of:
        // with:
        // A birthday. Add 1 year to get next year's date.
        LocalDate birthday = LocalDate.of(1976, Month.JULY, 18);
        LocalDate firstYear = birthday.withYear(1977);

        System.out.println("FirstYear=" + firstYear);

        // to:
        long since1970 = birthday.toEpochDay();
        System.out.println("Days since 1970=" + since1970);

        // at:
        LocalDateTime dateTime = LocalDate.now()
                                          .atTime(11, 45);
        System.out.println("Today at 11:45 AM: " + dateTime.toString());

        /* Local time */
        // LocalDate is a date component only, LocalTime is a time component only, LocalDateTime is both
        LocalDate localDate = LocalDate.now();
        LocalTime localTime = LocalTime.now();
        LocalDateTime localDateTime = LocalDateTime.now();

        System.out.println("LocalDate now:" + localDate.toString());
        System.out.println("LocalTime now:" + localTime.toString());
        System.out.println("LocalDateTime now:" + localDateTime.toString());

        // Shows how wrapping of seconds occurs at midnight. We added 90 seconds, yet the after time is just after
        // midnight, instead of 60 seconds after midnight like we would think.
        LocalTime beforeMidnight = LocalTime.MIDNIGHT.minusSeconds(30);
        System.out.println("Before midnight: " + beforeMidnight.toString());
        LocalTime afterMidnight = beforeMidnight.plusSeconds(90);
        System.out.println("After midnight: " + afterMidnight.toString());

        // Use a LocalDateTime when we want to make sure midnight is handled properly. This version shows how adding
        // 90 seconds puts the final time to 30 seconds after midnight, as we would expect.
        LocalDateTime beforeMidnight2 = LocalDate.now()
                                                 .atTime(23, 59);
        System.out.println("Before midnight 2: " + beforeMidnight2.toString());
        LocalDateTime afterMidnight2 = beforeMidnight2.plusSeconds(90);
        System.out.println("After midnight 2: " + afterMidnight2.toString());

        /* Time zone and offset */
        Set<String> allZones = ZoneId.getAvailableZoneIds();
        LocalDateTime dt = LocalDateTime.now();

        // Create a List using the set of zones and sort it.
        List<String> zoneList = new ArrayList<String>(allZones);
        Collections.sort(zoneList);

        for (String s : zoneList) {
            ZoneId zone = ZoneId.of(s);
            ZonedDateTime zdt = dt.atZone(zone);

            // Uncomment this to see all available time zones.
//            System.out.println(String.format("%s", zone));
        }

        // The current time in London- note that you must set up a ZonedDateTime with the local zone first, then
        // use it to find London time.
        ZoneId localZone = ZoneId.systemDefault();
        ZoneId london = ZoneId.of("Europe/London");

        ZonedDateTime localZoneDateTime = ZonedDateTime.of(localDateTime, localZone);
        ZonedDateTime londonDateTime = localZoneDateTime.withZoneSameInstant(london);

        System.out.println("The current time in London:" + londonDateTime);

        /* Instant time */
        Instant timestamp = Instant.now();
        timestamp = timestamp.plusSeconds(3);

        Instant anotherTimestamp = Instant.now();
        long seconds = anotherTimestamp.until(timestamp, ChronoUnit.SECONDS);

        System.out.println("Seconds between instants=" + seconds);

        /* Old way of getting a long timestamp vs new way with Instant. The values are identical. */
        long millisSinceEpoch = anotherTimestamp.toEpochMilli();
        long oldMillisFromEpoch = System.currentTimeMillis();
        System.out.println(String.format("Old way of timestamp=%d, new way of timestamp=%d", oldMillisFromEpoch, millisSinceEpoch));

        /* Get a timestamp from an hour ago from the current epoch */
        long oneHourAgoLong = Instant.now()
                                     .minus(1, ChronoUnit.HOURS)
                                     .toEpochMilli();

        Instant oneHourAgo = Instant.ofEpochMilli(oneHourAgoLong);
        System.out.println("One hour ago using epoch translation:" + oneHourAgo);

        /*
         * Temporal package- similar to the Collections package in that time-based operations are contained within
         * interfaces. The interfaces perform actions such as add or subtract time units, or return a copy of a time
         * object with a modification to one of the time fields.
         */

        /* Period and duration */
        // Duration- use to measure differences in machine-based time, such as when using Instants
        Duration dur = Duration.between(timestamp, Instant.now());
        System.out.println("Milliseconds between timestamps=" + dur.toMillis());

        // ChronoUnit- use to measure days, hours, minutes, seconds, etc. between two different times
        long hours = ChronoUnit.HOURS.between(localDateTime, londonDateTime);
        System.out.println("Hours between local time and London=" + hours);

        // Period- use to measure days, month, years, etc. between two different dates
        Period p = Period.between(birthday, today);
        int age = p.getYears();

        System.out.println("You are " + age + " years old.");

        /* Alternative clock (Clock class) */
        // Used in testing to create your own clocks that are separate from the system Clock. Your clock can be
        // used as an alternate with the Java 8 datetime classes.
        Clock clock = Clock.system(london);
        Instant londonNow = Instant.now(clock);
        System.out.println("Now in London using custom clock: " + londonNow);

    }

    @Test
    public void streamTesting() {
        Developer dev1 = new Developer();
        dev1.setName("Dev 1");

//        int hash1 = System.identityHashCode(dev1);
//        dev1.setName("Dev 1 x");
//        int hash2 = System.identityHashCode(dev1);
//
//        dev1 = new Developer();
//        int hash3 = System.identityHashCode(dev1);

        Developer dev2 = new Developer();
        dev2.setName("Dev 2");

        Developer dev3 = new Developer();
        dev3.setName("Dev 3");

        Secretary secretary = new Secretary();

        Salesperson sales1 = new Salesperson();
        Salesperson sales2 = new Salesperson();

        List<IWorker> workers = new ArrayList<IWorker>();
        workers.add(dev3);
        workers.add(dev1);
        workers.add(dev2);
        workers.add(secretary);
        workers.add(sales1);
        workers.add(sales2);

        /*
         * http://www.drdobbs.com/jvm/lambdas-and-streams-in-java-8-libraries/240166818
         * 
         * Java's classic for-each loop is inherently sequential, and must process the elements in the order specified
         * by the collection. Sometimes the strong guarantees of the for-each loop (sequential, in-order) are desirable,
         * but often are just an impediment to performance. This is where streams can be useful. Instead of controlling
         * the iteration, the client delegates that to the library and passes in snippets of code to execute at various
         * points in the computation. This allows the library to potentially use laziness, parallelism, and out-of-order
         * execution to improve performance.
         */

        // Before Java 8- a FOR loop to pay the developers
        for (IWorker worker : workers) {
            if (worker.role()
                      .equals("developer")) {
                worker.issuePay();
            }

        }

        // Pay the developers using a Java 8 stream. Note how a 'worker' variable is automatically created
        // without the need of a FOR loop.
        workers.stream()
               .filter(worker -> worker.role()
                                       .equals("developer") == true)
               .forEach(worker -> worker.issuePay());

        // Get a List of the sales people
        List<IWorker> salesPeople = workers
                .stream()
                .filter(worker -> worker.role()
                                        .equals("peon") == true)
                .collect(Collectors.toList());

        System.out.println(String.format("There are %d sales people", salesPeople.size()));

        // Sort the workers by role. Use a lambda for the Comparator that accesses the role() method for comparison.
        List<IWorker> sortedWorkers = workers
                .stream()
                .sorted(Comparator.comparing(worker -> worker.role()))
                .collect(Collectors.toList());

        System.out.println("Sorted workers: " + sortedWorkers);

        // Get all the different groups of workers (similar to a GROUP SQL statement)
        Set<String> departments = workers.stream()
                                         .map(worker -> worker.role())
                                         .collect(Collectors.toSet());

        System.out.println(departments);

        // Perform an operation that will be run as parallel in separate threads. Be very very careful to not
        // change the data inside any object in the original list while these are running.
        workers.parallelStream()
               .filter(worker -> worker.role()
                                       .equals("peon") == true)
               .forEach(worker -> worker.issuePay());

    }

}
