package nl.wernerdegroot.applicatives.processor.domain;

import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;

import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public class TypeBuilder {
    private final FullyQualifiedName fullyQualifiedName;

    public TypeBuilder(FullyQualifiedName fullyQualifiedName) {
        this.fullyQualifiedName = fullyQualifiedName;
    }

    public FullyQualifiedName getFullyQualifiedName() {
        return fullyQualifiedName;
    }

    public Type of(Type... typeArguments) {
        return Type.concrete(fullyQualifiedName, asList(typeArguments));
    }

    public Type of(TypeParameterName... typeArguments) {
        return Type.concrete(fullyQualifiedName, Stream.of(typeArguments).map(Type::generic).collect(toList()));
    }

    public TypeConstructor asTypeConstructor() {
        return TypeConstructor.concrete(fullyQualifiedName, asList(TypeConstructor.placeholder()));
    }

    public TypeConstructor of(TypeConstructor... typeArguments) {
        return TypeConstructor.concrete(fullyQualifiedName, asList(typeArguments));
    }
}
