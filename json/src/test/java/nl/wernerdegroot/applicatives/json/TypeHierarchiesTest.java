package nl.wernerdegroot.applicatives.json;

import nl.wernerdegroot.applicatives.runtime.decompositions.Decomposable2;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.function.BiFunction;

import static nl.wernerdegroot.applicatives.json.Json.*;
import static nl.wernerdegroot.applicatives.json.Key.key;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TypeHierarchiesTest {

    public interface Shape {

    }

    public static class Ellipse implements Shape, Decomposable2<Integer, Integer> {
        private final int width;
        private final int height;

        public Ellipse(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        @Override
        public <T> T decomposeTo(BiFunction<? super Integer, ? super Integer, T> fn) {
            return fn.apply(width, height);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Ellipse ellipse = (Ellipse) o;
            return getWidth() == ellipse.getWidth() && getHeight() == ellipse.getHeight();
        }

        @Override
        public int hashCode() {
            return Objects.hash(getWidth(), getHeight());
        }

        @Override
        public String toString() {
            return "Ellipse{" +
                    "width=" + width +
                    ", height=" + height +
                    '}';
        }
    }

    public static class Rectangle implements Shape, Decomposable2<Integer, Integer> {
        private final int width;
        private final int height;

        public Rectangle(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        @Override
        public <T> T decomposeTo(BiFunction<? super Integer, ? super Integer, T> fn) {
            return fn.apply(width, height);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Rectangle ellipse = (Rectangle) o;
            return getWidth() == ellipse.getWidth() && getHeight() == ellipse.getHeight();
        }

        @Override
        public int hashCode() {
            return Objects.hash(getWidth(), getHeight());
        }

        @Override
        public String toString() {
            return "Rectangle{" +
                    "width=" + width +
                    ", height=" + height +
                    '}';
        }
    }

    private final JsonObjectFormat<Ellipse> ellipseFormat = Json.instance().format(
            key("width").using(intFormat),
            key("height").using(intFormat),
            Ellipse::new
    );

    private final JsonObjectFormat<Rectangle> rectangleFormat = Json.instance().format(
            key("width").using(intFormat),
            key("height").using(intFormat),
            Rectangle::new
    );

    private final JsonFormat<Shape> shapeFormat = JsonFormat.of(
            key("type").using(stringReader).flatMap(type -> {
                switch (type) {
                    case "Ellipse":
                        return ellipseFormat;
                    case "Rectangle":
                        return rectangleFormat;
                    default:
                        return JsonReader.fail("unknown.shape", type);
                }
            }),
            shape -> {
                if (shape instanceof Ellipse) {
                    return ellipseFormat
                            .combineWith(key("type").using(stringWriter).withValue("Ellipse"))
                            .write((Ellipse) shape);
                } else if (shape instanceof Rectangle) {
                    return rectangleFormat
                            .combineWith(key("type").using(stringWriter).withValue("Rectangle"))
                            .write((Rectangle) shape);
                } else {
                    throw new IllegalArgumentException("No such shape!");
                }
            }
    );

    @Test
    public void typeHierarchies() {
        Shape shape = new Ellipse(4, 3);
        Json.Result<Shape> expected = Json.success(shape);
        Json.Result<Shape> toVerify = shapeFormat.readString(shapeFormat.write(shape).toString());
        assertEquals(expected, toVerify);
    }
}
