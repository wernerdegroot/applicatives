# Applicatives

Java code generation for applicative functors, selective functors and more.

- [Applicatives](#applicatives)
    * [Getting started](#getting-started)
    * [Motivating example](#motivating-example)
        + [Combining two `CompletableFuture`s](#combining-two-completablefutures)
        + [Combining more `CompletableFuture`s](#combining-more-completablefutures)
    * [Another example](#another-example)
    * [The rules](#the-rules)
    * [`lift`](#-lift-)
    * [Stacking](#stacking)

## Getting started

Java 8 or higher is required.

Add the following dependencies:

```xml
<dependency>
    <groupId>nl.wernerdegroot.applicatives</groupId>
    <artifactId>runtime</artifactId>
    <version>1.0.3</version>
</dependency>

<dependency>
    <groupId>nl.wernerdegroot.applicatives</groupId>
    <artifactId>processor</artifactId>
    <version>1.0.3</version>
    <scope>provided</scope>
</dependency>
```

You may also want to include `prelude`, for applicative instances for some common classes that are included in Java's standard library: 

```xml
<dependency>
    <groupId>nl.wernerdegroot.applicatives</groupId>
    <artifactId>prelude</artifactId>
    <version>1.0.3</version>
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

    // Getters, hashCode, equals and toString
}
```

Let's pretend it will take some time for the application to come up with a `firstName` and a `lastName`. Perhaps you need to load those from a slow database, or make a network request:

```java
String firstName = "Jack";
TimeUnit.HOURS.sleep(24);
String lastName = "Bauer";
Person person = new Person(firstName, lastName);
```

Instead of blocking the main thread of your application, you decide to switch to non-blocking `CompletableFuture`s. Although it will take some time to obtain the `firstName` and `lastName` and combine those into a `Person`, the application is free to perform other, useful tasks while it waits:

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

The method [`thenCombine`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html#thenCombine-java.util.concurrent.CompletionStage-java.util.function.BiFunction-) combines the result of two `CompletableFuture`s (by using a `BiFunction` that you provide). However, if you want to combine more than two `CompletableFuture`s you are out of luck.

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

    // Getters, hashCode, equals and toString
}
```

Now imagine that each of the attributes of such a `PokemonCard` need to be loaded from some different external system (perhaps your company is using microservices). Instead of having a `String`, `int`, `EnergyType` and `List<Move>` (which can be combined directly into a `PokemonCard`), you are stuck with a `CompletableFuture<String>`, `CompletableFuture<Integer>`, `CompletableFuture<EnergyType>` and `CompletableFuture<List<Move>>`:

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

Unfortunately, `thenCombine` won't be of much help. It's capable of combining two `CompletableFuture`s, but not any more than that. 

The best alternative you have is to await all the `CompletableFuture`s and use the `join`-method to extract the results (which requires some care, as it may block the thread if the computation did not complete yet):

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

(There are several other ways of achieving the same result. See [StackOverflow](https://stackoverflow.com/questions/34004802/how-to-combine-3-or-more-completionstages) for a discussion about the trade-offs on each of these alternatives.)

Instead of having to write all this boilerplate code, wouldn't it be nice if the authors of Java's standard library would have provided a couple of overloads for `thenCombine` for three or more `CompletableFuture`s? Even though the Java standard library doesn't have such a thing, this library has your back!

All that it requires of you is to provide a way to combine two `CompletableFuture`s. We write:

```java
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

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

When you compile, an interface named `CompletableFuturesOverloads` will be generated. This interface will have overloads for the `combine`-method which accept three or more `CompletableFuture`s to combine. If you want, you can specify how many overloads you'll need with the attribute `maxArity`.

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

In the example above, I took the liberty to add a method `instance` to `CompletableFutures`. This method is not essential to make the overloads work.

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

All that is left for you is to combine those four separate generators into a generator for `PokemonCard` objects (`Function<Random, PokemonCard>`). Although the following code works, it won't win any beauty contests:

```java
Function<Random, PokemonCard> randomPokemonCard = random -> {
    String name = randomName.apply(random);
    int hp = randomHp.apply(random);
    EnergyType energyType = randomEnergyType.apply(random);
    List<Move> moves = randomMoves.apply(random);

    return new PokemonCard(name, hp, energyType, moves);
};
```

There is a more convenient and elegant way to combine these four generators into a generator for `PokemonCard` objects. 

If we write a method to combine two random generator functions, the Applicatives library will reward us with a bunch of overloads that combine three or more of those:

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

All that's left for us is to reap the benefits:

```java
Function<Random, Person> randomPerson = 
        RandomGeneratorFunctions.instance().combine(
            randomPersonId,
            randomFirstName,
            randomLastName,
            randomBirthDate,
            Person::new
        );
```

## The rules

You will need to write a class that looks like the following (for a given, imaginary class `Foo`):

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
 ┌────────────────────────────┐        ┌────────────────────────────┐     │                           
 │ Method needs exactly three │        │ Name of the method is not  │     │   ┌──────────────────────┐
 │ type parameters (although  │    ┌───┤  important, but overloads  │     └───│ Number of overloads. │
 │  name is not important).   │    │   │  will have the same name.  │         └──────────────────────┘
 └─────────────┬──────────────┘    │   └────────────────────────────┘                                 
               ▼                   ▼                                 ┌───────────────────────────┐    
    public <A, B, C> Foo<C> whateverYouLike(                         │  Typically, the types of  │    
                        ▲                                            │  these are identical. In  │    
        Foo<A> left,  ◀─┼────────────────────────────────────────────┤ some cases, the types are │    
                        │                                            │  allowed to diverge. See  │    
        Foo<B> right, ◀─┘                                            │   "Type constructors".    │    
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

There are many other data structures like this, such as `Mono`/`Flux` from [Reactor](https://projectreactor.io/), [parser combinators](https://en.wikipedia.org/wiki/Parser_combinator), validators, predicates, etc.

Moreover, any "stack" of these data structures (a `List` of `Optional`s, or a `Function` that returns a `Stream` of `CompletableFuture`s) can automatically be combined this way too! The sections `Lift` and `Stacking` describe how stacking of applicatives works.

## Type constructors

Note that, in the example above, the types of the parameters `left` and `right` are too strict. Applicatives are typically covariant, and you may want to adjust the types of the parameters to reflect this (use `Foo<? extends A>` and `Foo<? extends B>` instead of `Foo<A>` and `Foo<B>`). This is similar to something you'll find in [the definition of `CompletableFuture.thenCombine`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html#thenCombine-java.util.concurrent.CompletionStage-java.util.function.BiFunction-) and is [generally recommended](https://en.wikipedia.org/wiki/Robustness_principle):

> Among programmers, to produce [compatible functions](https://en.wikipedia.org/wiki/Liskov_substitution_principle), the principle is also known in the form [be contravariant in the input type and covariant in the output type](https://en.wikipedia.org/wiki/Covariance_and_contravariance_(computer_science)).

The types of `left` and `right` are allowed to diverge as well. This is considered to be an advanced feature, but could be necessary to prevent the execution time overhead of excessive copying. See [the implementation of the applicative for `List`](https://github.com/wernerdegroot/applicatives/blob/main/prelude/src/main/java/nl/wernerdegroot/applicatives/prelude/Lists.java) for inspiration. 

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
// Lift `BiFunction<String, String, Person>` and then apply:
CompletableFuture<Person> futurePerson =
        CompletableFutures.instance().lift(Person::new).apply(
            futureFirstName, 
            futureLastName
        );
```

You may want to argue that the code above could be written more succinctly as:

```java
CompletableFuture<Person> futurePerson = 
        CompletableFutures.instance().combine(
            futureFirstName, 
            futureLastName,
            Person::new
        );
```

And you would be right! So, when would you ever prefer using `lift` over calling the `combine`-overload that accepts two `CompletableFuture`s?

## Stacking

The nice thing about applicatives is that a "stack" of two applicatives is an applicative as well.

Because both `CompletableFuture` and `List` are applicatives[^1], their combination is an applicative too. We can combine a `CompletableFuture<List<String>>` (first names) and another `CompletableFuture<List<String>>` (last names) into a `CompletableFuture<List<Person>>` by lifting the combinator `Person::new` twice:

[^1]: The applicative for lists provides the [cartesian product](https://www.geeksforgeeks.org/cartesian-product-of-sets/) of two lists.

```java
CompletableFuture<List<String>> futureFirstNames =
        CompletableFuture.completedFuture(asList("Jack", "Kim"));

CompletableFuture<List<String>> futureLastNames =
        CompletableFuture.supplyAsync(() -> {
            TimeUnit.HOURS.sleep(24);
            return asList("Bauer");
        });

// Lift *twice* and then apply.
// Will yield `new Person("Jack", "Bauer")` and `new Person("Kim", "Bauer")` 
// as soon as `futureLastNames` resolves.
CompletableFuture<List<Person>> futurePersons = 
        CompletableFutures.instance().lift(Lists.instance().lift(Person::new)).apply(
            futureFirstNames, 
            futureLastNames
        );
```

We are lifting `Person::new` twice:

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
