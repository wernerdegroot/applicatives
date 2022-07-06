## Records for Applicatives

Companion for the [Applicatives library](https://github.com/wernerdegroot/applicatives) for projects using Java 17 and records. Implementing a decomposition becomes a piece of cake.

## Getting started

Java 17 or higher is required.

Include the following dependency:

```xml
<dependency>
    <groupId>nl.wernerdegroot.applicatives</groupId>
    <artifactId>records</artifactId>
    <version>1.2.0</version>
</dependency>
```

## Example

Using classes:

```java
import nl.wernerdegroot.applicatives.runtime.Function3;

public class Color implements Decomposable3<Integer, Integer, Integer> {
    private final int red;
    private final int green;
    private final int blue;

    public Color(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    @Override
    public <T> T decomposeTo(Function3<? super Integer, ? super Integer, ? super Integer, ? extends T> fn) {
        return fn.apply(red, green, blue);
    }
}
```

Using records:

```java
public record Color(int red, int green, int blue) implements Record3<Color, Integer, Integer, Integer> {
}
```

Because `Record3` and its siblings rely on reflection and are not completely type-safe it's a good idea to verify that the record's attributes and the type arguments to `Record3` align:

```java
Records.verify(Color::new);
```

You can add these to a unit test or in a static initializer block somewhere. If you made a mistake, the Java compiler will catch it for you. 

Check out the [unit tests](https://github.com/wernerdegroot/applicatives/blob/main/records/src/test/java/nl/wernerdegroot/applicatives/records/Record3Test.java) for an example.