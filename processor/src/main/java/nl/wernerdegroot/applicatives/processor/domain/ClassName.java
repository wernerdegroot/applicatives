package nl.wernerdegroot.applicatives.processor.domain;

import java.util.Objects;

public class ClassName {

    private final String className;

    public ClassName(String className) {
        this.className = className;
    }

    public static ClassName of(String className) {
        return new ClassName(className);
    }

    public String raw() {
        return className;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassName className1 = (ClassName) o;
        return className.equals(className1.className);
    }

    @Override
    public int hashCode() {
        return Objects.hash(className);
    }

    @Override
    public String toString() {
        return "ClassName{" +
                "className='" + className + '\'' +
                '}';
    }
}
