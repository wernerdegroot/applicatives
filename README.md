# Applicatives

Java code generation for applicative functors, selective functors and more.

- [Applicatives](#applicatives)
    * [Getting started](#getting-started)
    * [Motivating example](#motivating-example)
        + [Combining two `CompletableFuture`s](#combining-two-completablefutures)
        + [Combining more `CompletableFuture`s](#combining-more-completablefutures)
    * [Another example](#another-example)
    * [The rules](#the-rules)
    * [Variance](#variance)
    * [Lift](#Lift)
    * [Stacking](#stacking)
    * [Contravariant](#contravariant)
    * [Decomposition](#decomposition)
    * [Contributing](#contributing)
    * [More contravariant data type](#more-contravariant-data-types)
    * [License](#license)

## Getting started

Java 8 or higher is required.

Add the required dependencies:

* [nl.wernerdegroot.applicatives.processor:1.1.0](https://mvnrepository.com/artifact/nl.wernerdegroot.applicatives/processor/1.1.0)  

    Annotation processor. Only needed during compilation. Hook it into `maven-compiler-plugin` or include it as dependency with scope `provided`.
 
* [nl.wernerdegroot.applicatives.runtime:1.1.0](https://mvnrepository.com/artifact/nl.wernerdegroot.applicatives/runtime/1.1.0)

    Required runtime dependencies. Only a handful of classes, and no transitive dependencies.

Example:

```xml
<dependency>
    <groupId>nl.wernerdegroot.applicatives</groupId>
    <artifactId>runtime</artifactId>
    <version>1.1.0</version>
</dependency>

...

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.8.1</version>
            <configuration>
                <source>${maven.compiler.source}</source>
                <target>${maven.compiler.target}</target>
                <annotationProcessorPaths>
                    <path>
                        <groupId>nl.wernerdegroot.applicatives</groupId>
                        <artifactId>processor</artifactId>
                        <version>1.1.0</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>
```

You may also want to include `prelude`, for applicative instances for some common classes that are included in Java's standard library: 

```xml
<dependency>
    <groupId>nl.wernerdegroot.applicatives</groupId>
    <artifactId>prelude</artifactId>
    <version>1.1.0</version>
</dependency>
```

## Motivating example

### Combining two `CompletableFuture`s

Suppose you have a class like the following: 

```java
public class Person {
    
    private final String firstName;
    private final String lastName;

    public Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Getters, `hashCode`, `equals` and `toString`
}
```

Let's pretend it will take some time for the application to come up with a `firstName` and a `lastName`. Perhaps you need to load those from a slow database, or make a network request:

```java
// Get a first name:
String firstName = "Jack";

// Wait a while and get a last name:
TimeUnit.HOURS.sleep(24);
String lastName = "Bauer";

// Combine the first name and last name into a `Person`:
Person person = new Person(firstName, lastName);
```

Instead of blocking the main thread of your application, you decide to switch to non-blocking `CompletableFuture`s. Although it will take some time to obtain `firstName` and `lastName` and combine those into a `Person`, the application is free to perform other, useful tasks while it waits:

```java
// Get a first name:
CompletableFuture<String> futureFirstName = 
        CompletableFuture.completedFuture("Jack");

// Wait a while and get a last name:
CompletableFuture<String> futureLastName = 
        CompletableFuture.supplyAsync(() -> {
            TimeUnit.HOURS.sleep(24);
            return "Bauer";
        });

// Combine the two `CompletableFuture`s with a first name 
// and a last name into a `CompletableFuture` with a `Person`:
CompletableFuture<Person> futurePerson =
        futureFirstName.thenCombine(futureLastName, Person::new);

// Do something useful while we wait for the Person. 
```

The method [`thenCombine`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html#thenCombine-java.util.concurrent.CompletionStage-java.util.function.BiFunction-) is a nifty function that combines the result of two `CompletableFuture`s (by using a `BiFunction` that you provide). In this case, the provided `BiFunction` combines a first name (`String`) and a last name (`String`) into a `Person` object.

The method `thenCombine` is very useful, but if you want to combine more than two `CompletableFuture`s it won't get you very far. Although it is not impossible to combine three or four `CompletableFuture`s using nothing but Java's standard library provides, you will have to accept a lot of boilerplate code. The next section describes the problem of combining four `CompletableFuture`s, and shows how to solve this problem using the Java standard library. The solution should leave you somewhat dissatisfied. The section will continue by showing you how you can use this library to reduce the amount of boilerplate to a minimum.

### Combining more `CompletableFuture`s

Suppose you work on an application that allows people to trade [Pokemon cards](https://en.wikipedia.org/wiki/Pok%C3%A9mon_Trading_Card_Game) online. Such an application might have a `PokemonCard` class like the following:

```java
public class PokemonCard {
   
    private final String name;
    private final int hp;
    private final EnergyType energyType;
    private final List<Move> moves;
    
    public PokemonCard(String name, int hp, EnergyType energyType, List<Move> moves) {
        this.name = name;
        this.hp = hp;
        this.energyType = energyType;
        this.moves = moves;
    }

    // Getters, `hashCode`, `equals` and `toString`
}
```

Imagine that each of the attributes of such a `PokemonCard` need to be loaded from some different external system (perhaps your company is using microservices). Instead of having a `String`, `int`, `EnergyType` and `List<Move>` (which can be combined directly into a `PokemonCard`), you are stuck with a `CompletableFuture<String>`, `CompletableFuture<Integer>`, `CompletableFuture<EnergyType>` and `CompletableFuture<List<Move>>`:

```java
// Fetch the name of the Pokemon:
CompletableFuture<String> futureName = ...;

// Fetch the number of health points: 
CompletableFuture<Integer> futureHp = ...;

// Fetch the energy type of the card: 
CompletableFuture<EnergyType> futureEnergyType = ...;

// Fetch the moves the Pokemon can perform: 
CompletableFuture<List<Move>> futureMoves = ...;
```

How do you combine those?

Like I claimed in the previous section, `thenCombine` won't be of much help. It's capable of combining two `CompletableFuture`s, but not any more than that. Unfortunately, the authors of the Java standard library did not provide an overload for `thenCombine` that combines four `CompletableFuture`s. 

If you are willing to go through the hassle, you can still use `thenCombine` to combine these four `CompletableFuture`s. You'll have to call that method no less than three times: once to combine `futureName` and `futureHp` into a `CompletableFuture` of some intermediate data structure (`NameAndHp`), then again to combine that with `futureEnergyType` into a `CompletableFuture` of yet another intermediate data structure (`NameAndHpAndEnergyType`), and one last time to combine that with `futureMoves` into a `CompletableFuture` of a `PokemonCard`:

```java
CompletableFuture<PokemonCard> futurePokemonCard = 
        futureName.thenCombine(futureHp, NameAndHp::new)
            .thenCombine(futureEnergyType, NameAndHpAndEnergyType::new)
            .thenCombine(futureMoves, (nameAndHpAndEnergyType, moves) -> {
                
                // Unpack intermediate data structure:
                String name = nameAndHpAndEnergyType.getName();
                int hp = nameAndHpAndEnergyType.getHp();
                EnergyType energyType = nameAndHpAndEnergyType.getEnergyType();
                
                // Create `PokemonCard`:
                return new PokemonCard(name, hp, energyType, moves);
        });

// Implementation of `NameAndHp` and `NameAndHpAndEnergyType` 
// not provided for the sake of brevity.
```

This is obviously not a solution for a programmer that demands excellence from their programming language!

The best alternative I found is to abandon `thenCombine` completely, wait until all `CompletableFuture`s are resolved using `CompletableFuture.allOf`. We can then use `thenApply` and `join` to extract the results (which requires some care, as you may accidentally block the thread if the computation did not complete yet):

```java
CompletableFuture<PokemonCard> futurePokemonCard = 
        CompletableFuture.allOf(
                futureName,
                futureHp,
                futureEnergyType,
                futureMoves
        ).thenApply(ignored -> {
            String name = futureName.join();
            int hp = futureHp.join();
            EnergyType energyType = futureEnergyType.join();
            List<Move> moves = futureMoves.join();

            return new PokemonCard(name, hp, energyType, moves);
        });
```

There are several other ways of achieving the same result. See [StackOverflow](https://stackoverflow.com/questions/34004802/how-to-combine-3-or-more-completionstages) for a discussion about the trade-offs on each of these alternatives.

Instead of having to write all this boilerplate code, wouldn't it be nice if the authors of Java's standard library would just provide a couple of overloads for `thenCombine` for three or more `CompletableFuture`s? Even though the Java standard library doesn't have such a thing, this library has your back!

All that it requires of you is to provide a way to combine two `CompletableFuture`s. We write:

```java
public class CompletableFutures implements CompletableFuturesOverloads {

    @Override
    @Covariant
    public <A, B, C> CompletableFuture<C> combine(
            CompletableFuture<A> left,
            CompletableFuture<B> right,
            BiFunction<? extends A, ? extends B, ? super C> fn) {

        // Implementation already conveniently provided 
        // by the authors of the Java standard library:
        return left.thenCombine(right, fn);
    }
}
```

You may wonder about the interface `CompletableFuturesOverloads` that this class implements. Where does that come from? And what does it contain? When you compile, the interface `CompletableFuturesOverloads` is generated for you. It contains many overloads for the `combine`-method that accept three or more `CompletableFuture`s to combine. If you want, you can specify how many overloads you'll need with `@Covariant`'s attribute `maxArity`.

With these overloads in our toolbox, combining four `CompletableFuture`s is as easy as combining two:

```java
CompletableFuture<PokemonCard> futurePokemonCard =
        CompletableFutures.instance().combine(
                futureName,
                futureHp,
                futureEnergyType,
                futureMoves,
                PokemonCard::new
        );
```

In the example above, I took the liberty to add a method `instance` to `CompletableFutures`. This method is not essential to make the overloads work, but it makes for a pleasant API:

```java
public class CompletableFutures implements CompletableFuturesOverloads {
    
    private static final CompletableFutures INSTANCE = new CompletableFutures(); 

    // Because `CompletableFutures.instance()` reads just a tad nicer
    // than `new CompletableFutures()` and provides opportunities to
    // reuse the same instance over and over again:
    public static CompletableFutures instance() {
        return INSTANCE;
    }

    // Like before

}
```

Note that the `CompletableFutures` class as described above is already conveniently included for you in the `prelude` module. Check out the [implementation](https://github.com/wernerdegroot/applicatives/blob/main/prelude/src/main/java/nl/wernerdegroot/applicatives/prelude/CompletableFutures.java) and the [tests](https://github.com/wernerdegroot/applicatives/blob/main/prelude/src/test/java/nl/wernerdegroot/applicatives/prelude/CompletableFuturesTest.java).

## Another example

In order to test the new Pokemon card application that you and your co-workers are building, it would be helpful to be able to generate a bunch of random `PokemonCard` objects to seed the test environment with. One of your co-workers already did much of the hard work, and she wrote the following functions:

```java
// Generate a random name:
Function<Random, String> randomName = ...;

// Generate random health points: 
Function<Random, Integer> randomHp = ...;

// Generate a random energy type: 
Function<Random, EnergyType> randomEnergyType = ...;

// Generate a random list of moves: 
Function<Random, List<Move>> randomMoves = ...;
```

Note that your co-worker is pretty smart. Each of these generators require an instance of `Random`, which guarantees that generating a random `String`, `Integer`, `EnergyType`, or `List<Move>` is predictable and repeatable (if you provide it with a predictable and repeatable instance of `Random` that is).

All that is left for you is to combine those four separate generators into a generator for `PokemonCard` objects (`Function<Random, PokemonCard>`). Although the following code works, it is not going to win any beauty contests:

```java
Function<Random, PokemonCard> randomPokemonCard = random -> {
    String name = randomName.apply(random);
    int hp = randomHp.apply(random);
    EnergyType energyType = randomEnergyType.apply(random);
    List<Move> moves = randomMoves.apply(random);

    return new PokemonCard(name, hp, energyType, moves);
};
```

There is a more convenient and elegant way to combine these four generators into a generator for `PokemonCard` objects. If we write a `@Covariant`-annotated method to combine two random generator functions, this library will reward us with a bunch of overloads that combine three or more of those:

```java
public class RandomGeneratorFunctions implements RandomGeneratorFunctionsOverloads {
    
    private static final RandomGeneratorFunctions INSTANCE = new RandomGeneratorFunctions();
    
    public static RandomGeneratorFunctions instance() {
        return INSTANCE;
    }

    @Override
    @Covariant
    public <A, B, C> Function<Random, C> combine(
            Function<Random, A> left,
            Function<Random, B> right,
            BiFunction<? super A, ? super B, ? extends C> fn) {
        return random -> {
            A generatedFromLeft = left.apply(random);
            B generatedFromRight = right.apply(random);
            fn.apply(generatedFromLeft, generatedFromRight);
        };
    }
} 
```

See how easy it becomes to combine random generator functions?

```java
Function<Random, PokemonCard> randomPokemonCard = 
        RandomGeneratorFunctions.instance().combine(
            randomName,
            randomHp,
            randomEnergyType,
            randomMoves,
            PokemonCard::new
        );
```

Note that a class much like the class `RandomGeneratorFunctions` described above is already conveniently included for you in the `prelude` module. Check out the [implementation](https://github.com/wernerdegroot/applicatives/blob/main/prelude/src/main/java/nl/wernerdegroot/applicatives/prelude/Functions.java) and the [tests](https://github.com/wernerdegroot/applicatives/blob/main/prelude/src/test/java/nl/wernerdegroot/applicatives/prelude/FunctionsTest.java).

## The rules

You will need to write a class that looks this (for a given, imaginary class `Foo`):

```
 ┌─────────────────────────────────────┐                                                              
 │ Name of the class is not important. │                                                              
 └──────────────────┬──────────────────┘                                                              
                    │            ┌─────────────────────────────────────────────┐                      
                    │            │ Class can have type parameters. These need  │                      
                    │            │ to provided to the generated class as well. │                      
                    │            └───────┬────────────────────────────┬────────┘                      
                    ▼                    ▼                            ▼                               
public class CanBeAnything<C1, C2, ..., CN> implements GeneratedClass<C1, C2, ..., CN> {              
                                                                                                      
                 ┌──────────────────────────────────┐      ┌────────────────────────────┐            
                 │ Specify name of generated class. │   ┌──┤ Explained in next section. │            
                 └─────────────────┬────────────────┘   │  └────────────────────────────┘            
    @Override                      ▼                    ▼                                             
    @Covariant(className = "GeneratedClass", liftMethodName = "lift", maxArity = 26)                  
                                                                         ▲                          
 ┌────────────────────────────┐        ┌────────────────────────────┐    │                         
 │ Method needs exactly three │        │ Name of the method is not  │    │ ┌──────────────────────┐
 │ type parameters (although  │    ┌───┤  important, but overloads  │    └─│ Number of overloads. │
 │  name is not important).   │    │   │  will have the same name.  │      └──────────────────────┘
 └─────────────┬──────────────┘    │   └────────────────────────────┘                              
               ▼                   ▼                                 ┌───────────────────────────┐    
    public <A, B, C> Foo<C> whateverYouLike(                         │  Typically, the types of  │    
                        ▲                                            │  these are identical. In  │    
        Foo<A> left,  ◀─┼────────────────────────────────────────────┤ some cases, the types are │    
                        │                                            │  allowed to diverge. See  │    
        Foo<B> right, ◀─┘                                            │    section "Variance".    │    
                                                                     └───────────────────────────┘    
        BiFunction<? super A, ? super B, ? extends C> combinator) {                                   
                                                          ▲                                           
                        ┌────────────────────┐            │                                           
        return ...;  ◀──┤ This is up to you! │            │                                           
                        └────────────────────┘            │                                           
    }                                 ┌───────────────────┴───────────────────┐                       
}                                     │    Combinator function is always a    │                       
                                      │     BiFunction with contravariant     │                       
                                      │ parameters and covariant return type. │                       
                                      └───────────────────────────────────────┘                       
```

`Foo` can be any data structure for which you can write a class like above. Such data structures are called ["applicatives"](https://en.wikipedia.org/wiki/Applicative_functor). Common examples from the Java standard library are:

* `java.util.Optional`
* `java.util.concurrent.CompletableFuture`
* `java.util.function.Function`
* `java.util.function.BiFunction`
* `java.util.List`
* `java.util.Map`
* `java.util.Set`
* `java.util.Stream`
* `java.util.stream.Collector`

There are many other data structures like this, such as `Mono`/`Flux` from [Reactor](https://projectreactor.io/), [parser combinators](https://en.wikipedia.org/wiki/Parser_combinator), etc.

Moreover, any "stack" of these data structures (a `List` of `Optional`s, or a `Function` that returns a `Stream` of `CompletableFuture`s) can automatically be combined this way too! The sections [Lift](#lift) and [Stacking](#stacking) describe how stacking of applicatives works.

## Variance

Note that, in the diagram above, the types of the parameters `Foo<A> left` and `Foo<B> right` are too strict. Applicatives are typically covariant, and you may want to adjust the types of the parameters to reflect this (use `Foo<? extends A>` and `Foo<? extends B>` instead of `Foo<A>` and `Foo<B>`). This is similar to something you'll find in [the definition of `CompletableFuture.thenCombine`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html#thenCombine-java.util.concurrent.CompletionStage-java.util.function.BiFunction-) and is [generally recommended](https://en.wikipedia.org/wiki/Robustness_principle):

> Among programmers, to produce [compatible functions](https://en.wikipedia.org/wiki/Liskov_substitution_principle), the principle is also known in the form [be contravariant in the input type and covariant in the output type](https://en.wikipedia.org/wiki/Covariance_and_contravariance_(computer_science)).

In certain circumstances, the types of `left` and `right` are allowed to diverge as well. This is considered to be an advanced feature, but it may be necessary if you want to prevent the execution time overhead of excessive copying. See [the implementation of the applicative for `List`](https://github.com/wernerdegroot/applicatives/blob/main/prelude/src/main/java/nl/wernerdegroot/applicatives/prelude/Lists.java) included in the `prelude` module for inspiration. 

## Lift
Lifting is a way to "upgrade" a function that works with regular values like `String`s and `Integer`s (usually very easy to write) to a similar function that works with, for example, `CompletableFuture`s like `CompletableFuture<String>`s and `CompletableFuture<Integer>`s instead (which is usually much more tiresome to write).

```
┌─────────────────────────────────────────────────────────────────────────────────────────────┐
│                              BiFunction<String, String, Person>                             │
└─────────────────────────────────────────────────────────────────────────────────────────────┘
                                                │                                              
                                                │  CompletableFutures.lift                      
                                                ▼                                              
┌─────────────────────────────────────────────────────────────────────────────────────────────┐
│ BiFunction<CompletableFuture<String>, CompletableFuture<String>, CompletableFuture<Person>> │
└─────────────────────────────────────────────────────────────────────────────────────────────┘
```

Using `lift`, you can transform any `BiFunction<A, B, C>` into a `BiFunction<CompletableFuture<A>, CompletableFuture<B>, CompletableFuture<C>>` or `BiFunction<List<A>, List<B>, List<C>>` or whatever applicative you may choose to lift this function into. You are not limited to a `BiFunction` either. Any function with up to 26 arguments can be lifted in this fashion.

Let's check out an example:

```java
// Apologies for the nasty type of this thing.
// Using `var` would definitely be advisable!
BiFunction<CompletableFuture<String>, CompletableFuture<String>, CompletableFuture<Person>> lifted = 
        CompletableFutures.instance().lift(Person::new);
        
CompletableFuture<Person> futurePerson =
        lifted.apply(
            futureFirstName, 
            futureLastName
        );
```

Of course, the code above could be written more succinctly as:

```java
CompletableFuture<Person> futurePerson = 
        CompletableFutures.instance().combine(
            futureFirstName, 
            futureLastName,
            Person::new
        );
```

So, when would you ever prefer using `lift` over calling the `combine`-overload that accepts two `CompletableFuture`s? This is explained in the next section.

## Stacking

The nice thing about applicatives is that a "stack" of two applicatives is an applicative as well.

Because both `CompletableFuture` and `List` are applicatives[^1], their combination is an applicative too. We can combine a `CompletableFuture<List<String>>` (first names) and another `CompletableFuture<List<String>>` (last names) into a `CompletableFuture<List<Person>>` by lifting the combinator `Person::new` twice:

[^1]: The applicative for lists provides the [cartesian product](https://www.geeksforgeeks.org/cartesian-product-of-sets/) of two lists.

```java
// Get a list of first names:
CompletableFuture<List<String>> futureFirstNames =
        CompletableFuture.completedFuture(asList("Jack", "Kim"));

// Wait a while and get a list of last names:
CompletableFuture<List<String>> futureLastNames =
        CompletableFuture.supplyAsync(() -> {
            TimeUnit.HOURS.sleep(24);
            return asList("Bauer");
        });

// Lift *twice* and then apply. Will yield `new Person("Jack", "Bauer")` 
// and `new Person("Kim", "Bauer")` as soon as `futureLastNames` resolves.
CompletableFuture<List<Person>> futurePersons = 
        CompletableFutures.instance().lift(Lists.instance().lift(Person::new)).apply(
            futureFirstNames, 
            futureLastNames
        );
```

In this example, we are lifting `Person::new` twice before we apply:

```
┌───────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                       BiFunction<String, String, Person>                                      │
└───────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
                                                         │                                                        
                                                         │  Lists.lift                                            
                                                         ▼                                                        
┌───────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│                              BiFunction<List<String>, List<String>, List<Person>>                             │
└───────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
                                                         │                                                        
                                                         │  CompletableFutures.lift                               
                                                         ▼                                                        
┌───────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│ BiFunction<CompletableFuture<List<String>>, CompletableFuture<List<String>>, CompletableFuture<List<Person>>> │
└───────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
```

If we wanted to, we could even lift the result once more. You can keep stacking applicatives!

## Contravariant

After using `@Covariant` a couple of times, you are likely wondering if there is a `@Contravariant` you could use as well. It turns out there is.

A note of caution is in order. Although it is very useful, composing contravariant data types is a bit weird. The rules for such data types are a bit counter-intuitive -- or should I say "contra-intuitive"? -- as the following example shows.

Let's start by composing two `Predicate`s by hand. If we have a `Predicate` for a `Person`'s first name and another `Predicate` for a `Person`'s last name, we can combine those into a `Predicate` for a `Person`:

```java
Predicate<String> isJack = Predicate.isEqual("Jack");
Predicate<String> isBauer = Predicate.isEqual("Bauer");

Predicate<Person> isJackBauer = person -> {
  return isJack.test(person.getFirstName()) && isBauer.test(person.getLastName());
};
```

When combining two covariant data types, like combining a `CompletableFuture<String>` and another `CompletableFuture<String>` into a `CompletableFuture<Person>`, we had to provide a way to combine a first name (type `String`) and last name (also type `String`) into a `Person`. We used a `Person`'s constructor for that.

When combining two contravariant data types, like combining a `Predicate<String>` and another `Predicate<String>` into a `Predicate<Person>`, we had to provide a way to split a `Person` into a first name (type `String`) and a last name (also type `String`) into a `Person`. This is exactly the opposite of what we provide a covariant data types! 

```
                   == Covariance ==                                            == Contravariance ==                  
                                                                                                                     
                                                                                                                     
┌──────────────────────┐       ┌──────────────────────┐                       ┌──────────────────────┐               
│ First name (String)  │       │  Last name (String)  │                       │        Person        │               
└──────────────────────┘       └──────────────────────┘                       └──────────────────────┘               
            │                              │                                              │                          
            └───────────────┬──────────────┘                              ┌───────────────┴──────────────┐           
                            ▼                                             ▼                              ▼           
                ┌──────────────────────┐                      ┌──────────────────────┐       ┌──────────────────────┐
                │        Person        │                      │ First name (String)  │       │  Last name (String)  │
                └──────────────────────┘                      └──────────────────────┘       └──────────────────────┘
```

This library can generate a whole bunch of overloads to combine two or more `Predicate`s if we add the following class to our project:

```java
public class Predicates implements PredicatesOverloads {
    
  private static final Predicates INSTANCE = new Predicates();
  
  public static Predicates instance() {
      return predicates;
  }

  @Override
  @Contravariant
  public <A, B, Intermediate, C> Predicate<C> combine(
          Predicate<A> left,
          Predicate<B> right,
          Function<? super C, ? super Intermediate> toIntermediate,
          Function<? super Intermediate, ? extends A> extractLeft,
          Function<? super Intermediate, ? extends B> extractRight) {

      return (C toCheck) -> {
          Intermediate intermediate = toIntermediate.apply(toCheck);
          return left.test(extractLeft.apply(intermediate)) && right.test(extractRight.apply(intermediate));
      };
  }
}
```

The signature of `combine` is a bit more complicated than in the covariant case, but if you take a minute to study this example I'm sure you can figure it out. The method `combine` will accept a `Predicate<A>` and a `Predicate<B>`. It is also provided with a way to transform a value of type `C` to some intermediate data structure of type `Intermediate`, and a way to extract both an `A` and a `B` from such an `Intermediate`. The following diagram illustrates the role of each of these functions. With these five ingredients, it's a piece of cake to return a `Predicate<C>`.

```
                    == Contravariance ==                    
                                                            
                   ┌─────────────────────┐                  
                   │          C          │                  
                   └─────────────────────┘                  
                              │                             
                              │  toIntermediate             
                              ▼                             
                   ┌─────────────────────┐                  
                   │    Intermediate     │                  
                   └─────────────────────┘                  
                              │                             
 extractRight  ┌──────────────┴──────────────┐  extractLeft 
               │                             │              
               ▼                             ▼              
    ┌─────────────────────┐       ┌─────────────────────┐   
    │          A          │       │          B          │   
    └─────────────────────┘       └─────────────────────┘   
```

Although implementing such a method is a bit hairy, using the generated overloads is pretty straightforward:

```java
// Verify name:
Predicate<String> isValidName = ...
        
// Check if sufficiently powerful:
Predicate<Integer> isValidHp = ...
        
// Ensure that Pokemon is of the right type:
Predicate<EnergyType> isValidEnergyType = ...
        
// Validate moves:
Predicate<List<Move>> areValidMoves = ...

// Combine:
Predicate<PokemonCard> isValidPokemon = 
        Predicates.instance().combine(
                isValidName,
                isValidHp,
                isValidEnergyType,
                areValidMoves
        );
```

Wait a minute! How does `combine` know how to break apart a `PokemonCard`?! That will be discussed in the next section.

## Decomposition

As noted before, we need to decompose an object into its constituent parts. For a `Person`, that would be two `String`s. A `PokemonCard` decomposes into a `String`, `Integer`, `EnergyType` and a `List<Move>`. In order to do so, `PokemonCard` implements the `Decomposable4<String, Integer, EnergyType, List<Move>>` interface:

```java
public class PokemonCard implements Decomposable4<String, Integer, EnergyType, List<Move>> {

    private final String name;
    private final int hp;
    private final EnergyType energyType;
    private final List<Move> moves;

    public PokemonCard(String name, int hp, EnergyType energyType, List<Move> moves) {
        this.name = name;
        this.hp = hp;
        this.energyType = energyType;
        this.moves = moves;
    }

    @Override
    public <T> T decomposeTo(Function4<? super String, ? super Integer, ? super EnergyType, ? super List<Move>, ? extends T> fn) {
        return fn.apply(name, hp, energyType, moves);
    }

    // Getters, `hashCode`, `equals` and `toString`
}
```

It's a neat trick that I learned over at [Benji Weber's blog](https://benjiweber.co.uk/blog/2020/09/19/fun-with-java-records/). It's a neat way to decompose objects into basically any other object with similar attributes.

If your class implements such an interface, the `combine` method is able to take advantage of it by splitting it up into its constituent parts.

If you are unable (or unwilling) to modify your classes to implement an additional interface like this, you can always opt for a stand-alone decomposition:

```java
// Break a PokemonCard apart into a Tuple of String, Integer, EnergyType and List<Move>:
Decomposition4<PokemonCard, String, Integer, EnergyType, List<Move>> decomposition = 
        pokemonCard -> Tuple.of(
                pokemonCard.getName(), 
                pokemonCard.getHp(), 
                pokemonCard.getEnergyType,
                pokemonCard.getMoves()
        );

Predicate<PokemonCard> isValidPokemon = 
        Predicates.instance().combine(
                isValidName,
                isValidHp,
                isValidEnergyType,
                areValidMoves,
                decomposition
        );
```

## More contravariant data types

You can use `@Contravariant` for any data structure for which you can write a class like above. Such data structures are called ["divisible"](https://www.youtube.com/watch?v=IJ_bVVsQhvc). Common examples from the Java standard library are:

* `java.util.Predicate`
* `java.util.concurrent.Comparator`
* `java.util.function.Function`
* `java.util.function.BiFunction`

Many of these are included in the `prelude` module.

Other examples of contravariant data types that can be combined include [Hamcrest's matchers](http://hamcrest.org/JavaHamcrest/tutorial), validators, etc.

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License

[MIT](https://github.com/wernerdegroot/applicatives/blob/v1.1.0/LICENSE)