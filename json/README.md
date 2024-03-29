# JSON using Applicatives

Leverages the [Applicatives library](https://github.com/wernerdegroot/applicatives) and the [JSR-374 specification](https://javaee.github.io/jsonp/index.html) to offer marshalling and unmarshalling of JSON without any annotations or reflection. It moves processing from the annotation-level to regular Java with all the benefits that brings.

This module is inspired by (some would even say a port of) [Play JSON](https://github.com/playframework/play-json).

## Getting started

Java 8 or higher is required.

Include the following dependency:

```xml
<dependency>
    <groupId>nl.wernerdegroot.applicatives</groupId>
    <artifactId>json</artifactId>
    <version>1.2.1</version>
</dependency>
```

This library uses the [JSR-374](https://javaee.github.io/jsonp/) specification for JSON parsing. To use this library, you will also need to provide an implementation of this specification. For example:

```xml
<dependency>
    <groupId>org.glassfish</groupId>
    <artifactId>javax.json</artifactId>
    <version>1.1</version>
</dependency>
```

## Reading and writing JSON

This library provides a convenient DSL for constructing a `JsonReader` (for reading JSON), a `JsonWriter` (for writing JSON) and a `JsonFormat` (for both reading and writing JSON).

### Reading

Assume we have the following records:

```java
public record Location(double latitude, double longitude) { }

public record Resident(String name, int age, Optional<String> role) { }

public record Place(String name, Location location, List<Resident> residents) { }
```

We construct a `JsonReader` for each of these as follows:

```java
JsonReader<Location> locationReader = Json.instance().reader(
    key("latitude").using(doubleReader),
    key("longitude").using(doubleReader),
    Location::new
);

JsonReader<Resident> residentReader = Json.instance().reader(
    key("name").using(stringReader),
    key("age").using(intReader),
    key("role").using(stringReader.optional()),
    Resident::new
);

JsonReader<Place> placeReader = Json.instance().reader(
    key("name").using(stringReader),
    key("location").using(locationReader),
    key("residents").using(residentReader.list()),
    Resident::new
);

String json = "...";

Json.Result<Place> place = placeReader.readString(json);
```

As you can see, a `JsonReader` leverages the constructor of a class or record to create instances. No setters or mutable properties required!

## Writing

Similar to a `JsonReader`, we could construct a `JsonWriter` as:

```java
JsonWriter<Location> locationWriter = Json.instance().writer(
    key("latitude").using(doubleWriter),
    key("longitude").using(doubleWriter),
    Decomposition.of(Location::latitude, Location::longitude)
);

JsonWriter<Resident> residentWriter = Json.instance().writer(
    key("name").using(stringWriter),
    key("age").using(intWriter),
    key("role").using(stringWriter.optional()),
    Decomposition.of(Resident::name, Resident::age, Resident::role)
);

JsonWriter<Place> placeWriter = Json.instance().writer(
    key("name").using(stringWriter),
    key("location").using(locationWriter),
    key("residents").using(residentWriter.list()),
    Decomposition.of(Place::name, Place::location, Place::residents)
);

Place place = new Place(
    "Watership Down",
    new Location(51.235685, -1.309197),
    asList(
        new Resident("Fiver", 4, Optional.empty()),
        new Resident("Bigwig", 6, Optional.of("Boswa"))
    )
);

String json = placeWriter.writeString(place);
```

A `JsonReader` requires a constructor to create a record (like `Place`) from its constituent parts (like `String`, `Location` and `List<Resident>`). A `JsonWriter` requires the opposite. We have to provide a way to _decompose_ a record (like `Place`) into its constituent parts (like `String`, `Location` and `List<Resident>`). We use a `Decomposition` for that. Unfortunately, Java [is not smart enough (yet)](https://openjdk.org/projects/amber/design-notes/towards-better-serialization#sidebar-pattern-matching) to figure this out on its own. If you don't mind a little reflection, its likely a good idea to modify these records and implement one of the interfaces provided in [Records for Applicatives](https://github.com/wernerdegroot/applicatives/tree/main/records). For example:

```java
public record Place(String name, Location location, List<Resident> residents)
    implements Record3<Place, String, Location, List<Resident>> { }
```

This small investment yields great returns. I no longer have to provide a `Decomposition`:

```java
JsonWriter<Place> placeWriter = Json.instance().writer(
    key("name").using(stringWriter),
    key("location").using(locationWriter),
    key("residents").using(residentWriter.list())
);
```

In what follows, we will assume that the records `Location`, `Resident` and `Place` implement these interfaces.

## `JsonFormat`

A separate `JsonReader` and `JsonWriter` that both describe the same JSON object is not ideal. In cases such as this, it is often more natural to combine to two into a `JsonFormat`:

```java
JsonFormat<Location> locationFormat = Json.instance().format(
    key("latitude").using(doubleFormat),
    key("longitude").using(doubleFormat),
    Location::new
);

JsonFormat<Resident> residentFormat = Json.instance().format(
    key("name").using(stringFormat),
    key("age").using(intFormat),
    key("role").using(stringFormat.optional()),
    Resident::new
);

JsonFormat<Place> placeFormat = Json.instance().format(
    key("name").using(stringFormat),
    key("location").using(locationFormat),
    key("residents").using(residentFormat.list()),
    Resident::new
);
```

A `JsonFormat` supports both `readString` and `writeString`:

```java
Place place = new Place(
    "Watership Down",
    new Location(51.235685, -1.309197),
    asList(
        new Resident("Fiver", 4, Optional.empty()),
        new Resident("Bigwig", 6, Optional.of("Boswa"))
    )
);

Json.Result<Place> backAndForth = placeFormat.readString(placeFormat.writeString(place));
```

## All together

If you are so inclined, you can even condense the whole `JsonFormat` into a single expression:

```java
JsonFormat<Place> placeFormat = Json.instance().format(
    key("name").using(stringFormat),
    key("location").using(
        Json.instance().format(
            key("latitude").using(doubleFormat),
            key("longitude").using(doubleFormat),
            Location::new
        )
    ),
    key("residents").using(
        Json.instance().format(
            key("name").using(stringFormat),
            key("age").using(intFormat),
            key("role").using(stringFormat.optional()),
            Resident::new
        ).list()
    ),
    Resident::new
);
```

## Type hierarchies

Until now we have written `JsonReader`s and `JsonWriter`s that deal exclusively with one type of data. What do we do when we need to serialize a set of possible types? Consider the following type hierarchy:

```java
public sealed interface Shape permits Ellipse, Rectangle { }

public record Ellipse(int width, int height) 
    implements Shape, Record2<Ellipse, Integer, Integer> { }

public record Rectangle(int width, int height)
    implements Shape, Record2<Rectangle, Integer, Integer> { }
```

We can write each of these types as a JSON object with two fields: `width` and `height`. However, when it comes to reading JSON we have a problem. We no longer know what type of shape we’re dealing with.

We can solve this problem by adding the type information to the JSON as metadata. For example, we can add a `type` attribute with value `"Ellipse"` or `"Rectangle"` to indicate the type of shape.

Let’s see this in action. We begin by defining a `JsonFormat` for each subtype:

```java
JsonObjectFormat<Ellipse> ellipseFormat = Json.instance().format(
    key("width").using(intFormat),
    key("height").using(intFormat),
    Ellipse::new
);

JsonObjectFormat<Rectangle> rectangleFormat = Json.instance().format(
    key("width").using(intFormat),
    key("height").using(intFormat),
    Rectangle::new
);
```

We then write a `JsonFormat` for `Shape` that adds in the `type` attribute. We delegate to `ellipseFormat` and `rectangleFormat` using pattern matching:

```java
JsonFormat<Shape> shapeFormat = JsonFormat.of(
    key("type").using(stringReader).flatMap(type -> 
        switch (type) {
            case "Ellipse" -> ellipseFormat;
            case "Rectangle" -> rectangleFormat;
            default -> JsonReader.fail("unknown.shape", type);
        }
    ),
    shape -> 
        switch (shape) {
            case Ellipse ellipse ->
                ellipseFormat
                    .combineWith(key("type").using(stringWriter).withValue("Ellipse"))
                    .write(ellipse);
            case Rectangle rectangle ->
                rectangleFormat
                    .combineWith(key("type").using(stringWriter).withValue("Rectangle"))
                    .write(rectangle);
        }
);
```

## Validation

You can extend a `JsonReader` or a `JsonFormat` with additional validation rules to ensure the data we parse is correct:

```java
JsonReader<Location> locationReader = Json.instance().reader(
    key("latitude").using(doubleReader.verify((latitude, context) -> {
        if (latitude < -90.0 || latitude > 90){
            context.notifyFailure("invalid.latitude", latitude);
        }
    })),
    key("longitude").using(doubleReader.verify((longitude, context) -> {
        if (longitude < -180.0 || longitude > 180){
            context.notifyFailure("invalid.longitude", longitude);
        }
    })),
    Location::new
);
```

A `context` (of type `ValidationContext`) is used to signal validation failures. This is similar to the way [Hibernate validator works](https://docs.jboss.org/hibernate/validator/5.0/reference/en-US/html/validator-customconstraints.html#validator-customconstraints-validator). When validation for the `latitude` or the `longitude` fails, the reader will return a `Json.Result<Place>` that signals the failure (`Json.Failed<Place>`).