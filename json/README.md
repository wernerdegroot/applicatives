# JSON using Applicatives

Leverages the [Applicatives library](https://github.com/wernerdegroot/applicatives) and the [JSR-374 specification](https://javaee.github.io/jsonp/index.html) to offer marshalling and unmarshalling of JSON without annotations. It moves processing from the annotation-level to regular Java with all the benefits that brings.

## Getting started

Java 8 or higher is required.

Include the following dependency:

```xml
<dependency>
    <groupId>nl.wernerdegroot.applicatives</groupId>
    <artifactId>json</artifactId>
    <version>1.1.0</version>
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

Then we could construct a `JsonReader` for them as follows:

```java
JsonReader<Location> locationReader = Json.instance().reader(
    key("latitude").asDouble(),
    key("longitude").asDouble(),
    Location::new
);

JsonReader<Resident> residentReader = Json.instance().reader(
    key("name").asString(),
    key("age").asInt(),
    key("role").asOptionalString(),
    Resident::new
);

JsonReader<Place> placeReader = Json.instance().reader(
    key("name").asString(),
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
    key("latitude").asDouble(),
    key("longitude").asDouble(),
    Decomposition.of(Location::latitude, Location::longitude)
);

JsonWriter<Resident> residentWriter = Json.instance().writer(
    key("name").asString(),
    key("age").asInt(),
    key("role").asOptionalString(),
    Decomposition.of(Resident::name, Resident::age, Resident::role)
);

JsonWriter<Place> placeWriter = Json.instance().writer(
    key("name").asString(),
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

Similar (but opposite) to the `JsonReader`, we have to show Java how deconstruct each `record` (like `Place`) into its constituent parts (like `String`, `Location` and `List<Resident>`). We use a `Decomposition` for that. Unfortunately, Java [is not smart enough (yet)](https://openjdk.org/projects/amber/design-notes/towards-better-serialization#sidebar-pattern-matching) to figure this out on its own.

## `JsonFormat`

Having a separate `JsonReader` and `JsonWriter` that both describe the same properties is a bit of a code smell. In cases such as this, it is often more natural to combine to two into a `JsonFormat`:

```java
JsonFormat<Location> locationFormat = Json.instance().format(
    key("latitude").asDouble(),
    key("longitude").asDouble(),
    Location::new,
    Decomposition.of(Location::latitude, Location::longitude)
);

JsonFormat<Resident> residentFormat = Json.instance().format(
    key("name").asString(),
    key("age").asInt(),
    key("role").asOptionalString(),
    Resident::new,
    Decomposition.of(Resident::name, Resident::age, Resident::role)
);

JsonFormat<Place> placeFormat = Json.instance().format(
    key("name").asString(),
    key("location").using(locationFormat),
    key("residents").using(residentFormat.list()),
    Resident::new,
    Decomposition.of(Place::name, Place::location, Place::residents)
);
```

A `JsonFormat` supports both `readString` and `writeString`, so we can delete the `JsonReader`s and `JsonWriter`s from before. Viz.

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

## Type hierarchies

Until now we have written `JsonReader`s and `JsonWriter`s that deal exclusively with one type of data. What do we do when we need to serialize a set of possible types? Consider the following type hierarchy:

```java
public sealed interface Shape permits Ellipse, Rectangle { }

public record Ellipse(int width, int height) implements Shape { }

public record Rectangle(int width, int height) implements Shape { }
```

We can write each of these types as a JSON object with two fields: `width` and `height`. However, when it comes to reading JSON we have a problem. We no longer know what type of shape we’re dealing with.

We can solve this problem by adding the type information to the JSON as metadata. For example, we can add a `type` attribute with value `"Ellipse"` or `"Rectangle"` to indicate the type of shape.

Let’s see this in action. We begin by defining a `JsonFormat` for each subtype:

```java
JsonObjectFormat<Ellipse> ellipseFormat = Json.instance().format(
    key("width").asInt(),
    key("height").asInt(),
    Ellipse::new,
    Decomposition.of(Ellipse::width, Ellipse::height)
);

JsonObjectFormat<Rectangle> rectangleFormat = Json.instance().format(
    key("width").asInt(),
    key("height").asInt(),
    Rectangle::new,
    Decomposition.of(Rectangle::width, Rectangle::height)
);
```

We then write a `JsonFormat` for `Shape` that adds in the `type` attribute. We delegate to `ellipseFormat` and `rectangleFormat` using pattern matching:

```java
JsonFormat<Shape> shapeFormat = JsonFormat.of(
    key("type").asString().flatMap(type -> 
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
                    .combineWith(key("type").asString().withValue("Ellipse"))
                    .write(ellipse);
            case Rectangle rectangle ->
                rectangleFormat
                    .combineWith(key("type").asString().withValue("Rectangle"))
                    .write(rectangle);
        }
);
```

## Validation

You can extend a `JsonReader` or a `JsonFormat` with additional validation rules to ensure the data we parse is correct:

```java
JsonReader<Location> locationReader = Json.instance().reader(
    key("latitude").asDouble().verify((latitude, context) -> {
        if (latitude < -90.0 || latitude > 90){
            context.notifyFailure("invalid.latitude", latitude);
        }
    }),
    key("longitude").asDouble().validate((longitude, context) -> {
        if (longitude < -180.0 || longitude > 180){
            context.notifyFailure("invalid.longitude", longitude);
        }
    }),
    Location::new
);
```

When validation for the `latitude` or the `longitude`, the reader will return a `Json.Result<Place>` that signals the failure (`Json.Failed<Place>`).