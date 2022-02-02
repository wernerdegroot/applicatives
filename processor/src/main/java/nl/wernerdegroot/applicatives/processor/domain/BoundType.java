package nl.wernerdegroot.applicatives.processor.domain;

import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import nl.wernerdegroot.applicatives.processor.domain.type.WildcardType;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.WildcardTypeConstructor;

/**
 * Used to distinguish the two types of wildcards.
 */
public enum BoundType {
    EXTENDS("extends"),
    SUPER("super");

    private final String stringValue;

    BoundType(String stringValue) {
        this.stringValue = stringValue;
    }

    @Override
    public String toString() {
        return stringValue;
    }

    public WildcardType type(TypeParameterName typeParameterName) {
        return Type.wildcard(this, typeParameterName.asType());
    }

    public WildcardType type(Type type) {
        return Type.wildcard(this, type);
    }

    public WildcardTypeConstructor type(TypeConstructor typeConstructor) {
        return TypeConstructor.wildcard(this, typeConstructor);
    }

    public WildcardTypeConstructor asTypeConstructor() {
        return TypeConstructor.wildcard(this, TypeConstructor.placeholder());
    }
}
