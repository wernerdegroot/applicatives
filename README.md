# Applicatives

Java code generation for applicative functors, selective functors and more.

- [Applicatives](#applicatives)
    * [Motivating example](#motivating-example)
        + [Combining two `CompletableFuture`s](#combining-two--completablefuture-s)
        + [Combining more `CompletableFuture`s](#combining-more--completablefuture-s)
    * [Another motivating example](#another-motivating-example)
    * [What this library is about (short story)](#what-this-library-is-about--short-story-)
    * [How to use](#how-to-use)
        + [Prerequisites](#prerequisites)
        + [Combine two `CompletableFuture`s](#combine-two--completablefuture-s)
        + [Implement the generated interface](#implement-the-generated-interface)
        + [Use](#use)
        + [Random generators](#random-generators)
    * [Details](#details)
    * [Stacking](#stacking)
    * [What this library is about (long story)](#what-this-library-is-about--long-story-)

<small><i><a href='http://ecotrust-canada.github.io/markdown-toc/'>Table of contents generated with markdown-toc</a></i></small>

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

    // Getters, hashCode, equals and toString omitted for brevity
}
```

Let's pretend it will take some time for the application to come up with a `firstName` and a `lastName`. Perhaps you need to fetch those from a slow database, or make a network request.

Instead of blocking the main thread of your application, you decide to switch to non-blocking `CompletableFuture`s. Although we are still waiting on the `firstName` and `lastName` of our `Person`, at least the application is free to perform other, useful tasks:

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

// Do something useful while we wait for the `Person`. 
```

It turns out, combining a `CompletableFuture<String>` and another `CompletableFuture<String>` into a `CompletableFuture<Person>` is quite easy. However, if you want to combine more than two `CompletableFuture`s, you are out of luck.

### Combining more `CompletableFuture`s

Suppose you have a `Delivery` class like the following:

```java
import java.time.LocalDate;

public class Delivery {
    private final long orderId;
    private final List<OrderItem> items;
    private final LocalDate deliveryDate;
    private final Address address;
    
    public Delivery(long orderId, List<OrderItem> items, LocalDate deliveryDate, Address address) {
        this.orderId = orderId;
        this.items = items;
        this.deliveryDate = deliveryDate;
        this.address = address;
    }
    
    // Getters, hashCode, equals and toString omitted for brevity
}
```

Now imagine that all the attributes of such a `Delivery` need to be loaded from some different external system (perhaps your company is using microservices). Instead of having a `long`, `List<OrderItem>`, `LocalDate` and `Address` (which can be combined directly into a `Delivery`), you are stuck with a `CompletableFuture<Long>`, `CompletableFuture<List<OrderItem>>`, `CompletableFuture<LocalDate>` and `CompletableFuture<Address>`. 

```java
// Fetch the order id:
CompletableFuture<Long> futureOrderId = ...;

// Fetch the list of order items: 
CompletableFuture<List<OrderItem>> futureOrderItems = ...;

// Fetch the delivery date: 
CompletableFuture<LocalDate> futureDeliveryDate = ...;

// Fetch the address to deliver to: 
CompletableFuture<Address> futureAddress = ...;
```

How do you combine those?

Unfortunately, `thenCombine` won't be of much help. It's capable of combining two `CompletableFuture`s, but not any more.

One solution would be to switch to `CompletableFuture`'s `thenCompose`-method, and call it in quick succession:

```java
futureOrderId.thenCompose(orderId ->
    futureOrderItems.thenCompose(orderItems ->
        futureDeliveryDate.thenCompose(deliveryDate -> 
            futureAddress.thenApply(address ->
                new Delivery(orderId, orderItems, deliveryDate, address)
            )
        )        
    )
);
```

However, this is only a solution for people who hate their co-workers.

Wouldn't it be nice if the authors of Java's standard library would have provided overloads for `thenCombine` for three or more `CompletableFuture`s? Even though the Java standard library doesn't have such a thing, this library has your back! 

Before showing you this library can help, I would like to show you another example.

## Another motivating example

In order to test the new application that you and your co-workers are building, it would be convenient to be able to generate a bunch of random `Delivery` objects to seed the test environment with. One of your co-workers already did much of the hard work, and she wrote the following functions:

```java
// Generate a random order id:
Function<Random, Long> randomOrderId = ...;

// Generate a random list of order items: 
Function<Random, List<OrderItem>> randomOrderItems = ...;

// Generate a random delivery date: 
Function<Random, LocalDate> randomDeliveryDate = ...;

// Generate a random address to deliver to: 
Function<Random, Address> randomAddress = ...;
```

Note that your co-worker is pretty smart. By asking the code that calls these functions to provide an instance of `Random`, your co-worker has guaranteed that generating a random `Long`, `List<OrderItem>`, `LocalDate` or `Address` is predictable and repeatable.

All that is left for you is to combine those four separate generators into a generator for `Delivery` objects (`Function<Random, Delivery>`). Although the following code works, it won't win any beauty contests:

```java
Function<Random, Delivery> randomDelivery = random -> {
    long orderId = randomOrderId.apply(random);
    List<OrderItem> orderItems = randomOrderItems.apply(random);
    LocalDate deliveryDate = randomDeliveryDate.apply(random);
    Address address = randomAddres.apply(random);
    
    return new Delivery(orderId, orderItems, deliveryDate, address);
};
```

Instead of manually combining these four generator functions, you decide to use this library.

## What this library is about (short story)

Using the method `thenCombine` to combine two `CompletableFuture`s, this will library will generate the code necessary to combine three or more `CompletableFuture`s. 

By providing a method to combine two random generator `Function`s (like the ones our co-worker wrote), this library will generate the code necessary to combine three or more of those too. 

In fact, this library works well with any data structure that can be combined. Common examples from the Java standard library are:

* `java.util.Optional`
* `java.util.List`
* `java.util.Stream`
* `java.util.concurrent.CompletableFuture`
* `java.util.function.Function`

However, there are many, many more data structures that can be combined. Think about parser combinators, validators, predicates, etc.

Moreover, any combination of these data structures (a `List` of `Optional`s, or a `Function` that returns a `CompletableFuture`) can automatically be combined this way too!

## How to use

In what follows, we'll go through the steps required to combine `CompletableFuture`s together.

### Prerequisites

Java 8 or higher is required to run the annotation processor.

Add the required dependencies:

```xml
<dependency>
    <groupId>nl.wernerdegroot.applicatives</groupId>
    <artifactId>runtime</artifactId>
    <version>1.0.0</version>
</dependency>

<dependency>
    <groupId>nl.wernerdegroot.applicatives</groupId>
    <artifactId>processor</artifactId>
    <version>1.0.0</version>
    <scope>provided</scope>
</dependency>
```

### Combine two `CompletableFuture`s

Create a class with a single method, capable of combining two `CompletableFuture`s (or any other data structure you might want to combine). Annotate that method with `@Covariant` in which you specify the name of the class to generate.

```java
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

public class CompletableFutures {

    @Covariant(className = "CompletableFuturesMixin")
    public <A, B, C> CompletableFuture<C> combine(
            CompletableFuture<A> left,
            CompletableFuture<B> right,
            BiFunction<? extends A, ? extends B, ? super C> fn) {
        
        // Implementation already conveniently provided by
        // the Java standard library authors:
        return left.thenCombine(right, fn);
    }
}
```

When you compile, an interface named `CompletableFuturesMixin` will be generated. This interface will have `default` overloads for the `combine`-method which accept three or more `CompletableFuture`s to combine.

### Implement the generated interface

To dot the i's and cross the t's, implement `CompletableFuturesMixin` and decorate the `combine`-method with an `@Override` annotation.

```java
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

public class CompletableFutures implements CompletableFuturesMixin {

    @Override
    @Covariant(className = "CompletableFuturesMixin")
    public <A, B, C> CompletableFuture<C> combine(
            CompletableFuture<A> left,
            CompletableFuture<B> right,
            BiFunction<? extends A, ? extends B, ? super C> fn) {

        // Implementation already conveniently provided by
        // the Java standard library authors:
        return left.thenCombine(right, fn);
    }
}
```

### Use

```java
// Fetch the order id:
CompletableFuture<Long> futureOrderId = ...;

// Fetch the list of order items: 
CompletableFuture<List<OrderItem>> futureOrderItems = ...;

// Fetch the delivery date: 
CompletableFuture<LocalDate> futureDeliveryDate = ...;

// Fetch the address to deliver to: 
CompletableFuture<Address> futureAddress = ...;

// Combine:
CompletableFuture<Delivery> futureDelivery = new CompletableFutures()
    .combine(
        futureOrderId,
        futureOrderItems,
        futureDeliveryDate,
        futureAddress,
        Delivery::new
    );
```

### Random generators

Another, complete example might help you to connect the dots:

```java
public class RandomGeneratorFunctions implements RandomGeneratorFunctionsMixin {
   
    @Override
    @Covariant(className = "RandomGeneratorFunctionsMixin")
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

with

```java
// Generate a random order id:
Function<Random, Long> randomOrderId = ...;

// Generate a random list of order items: 
Function<Random, List<OrderItem>> randomOrderItems = ...;

// Generate a random delivery date: 
Function<Random, LocalDate> randomDeliveryDate = ...;

// Generate a random address to deliver to: 
Function<Random, Address> randomAddress = ...;

// Combine:
Function<Random, Delivery> randomDelivery = new RandomGeneratorFunctions()
    .combine(
        randomOrderId,
        randomOderItems,
        randomDeliveryDate,
        randomAddress,
        Delivery::new
    );
```

## Details

* The name of the method doesn't matter.
* The names of the type parameters (`A`, `B` and `C` in the examples above) don't matter.
* The method can accept additional parameters too.
* The method can declare additional type parameters too.

## Stacking

TODO

## What this library is about (long story)

TODO
