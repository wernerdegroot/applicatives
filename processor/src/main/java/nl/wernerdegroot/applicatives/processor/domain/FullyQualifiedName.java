package nl.wernerdegroot.applicatives.processor.domain;

import nl.wernerdegroot.applicatives.processor.domain.type.Type;

import java.util.List;
import java.util.Objects;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

public class FullyQualifiedName {

    private final String fullyQualifiedName;

    public FullyQualifiedName(String fullyQualifiedName) {
        this.fullyQualifiedName = fullyQualifiedName;
    }

    public static FullyQualifiedName of(String fullyQualifiedName) {
        return new FullyQualifiedName(fullyQualifiedName);
    }

    public Type with(List<Type> typeParameters) {
        return Type.concrete(this, typeParameters);
    }

    public Type with(Type... typeParameters) {
        return with(asList(typeParameters));
    }

    public Type asType() {
        return with(emptyList());
    }

    public FullyQualifiedName withClassName(ClassName className) {
        return FullyQualifiedName.of(fullyQualifiedName + "." + className.raw());
    }

    public String raw() {
        return fullyQualifiedName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FullyQualifiedName that = (FullyQualifiedName) o;
        return raw().equals(that.raw());
    }

    @Override
    public int hashCode() {
        return Objects.hash(raw());
    }

    @Override
    public String toString() {
        return "FullyQualifiedName{" +
                "fullyQualifiedName='" + fullyQualifiedName + '\'' +
                '}';
    }
}
