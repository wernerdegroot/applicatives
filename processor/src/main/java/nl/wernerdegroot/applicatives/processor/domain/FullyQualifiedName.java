package nl.wernerdegroot.applicatives.processor.domain;

import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import nl.wernerdegroot.applicatives.processor.domain.type.TypeArgument;

import java.util.List;
import java.util.Objects;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

public final class FullyQualifiedName {

    private final String fullyQualifiedName;

    public FullyQualifiedName(String fullyQualifiedName) {
        this.fullyQualifiedName = fullyQualifiedName;
    }

    public static FullyQualifiedName of(String fullyQualifiedName) {
        return new FullyQualifiedName(fullyQualifiedName);
    }

    public Type with(List<TypeArgument> typeArguments) {
        return Type.concrete(this, typeArguments);
    }

    public Type with(TypeArgument... typeArguments) {
        return with(asList(typeArguments));
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
        return Objects.equals(fullyQualifiedName, that.fullyQualifiedName);
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
