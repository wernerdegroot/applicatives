package nl.wernerdegroot.applicatives.processor.validation;

import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;

import java.util.Objects;

public class ValidCovariantInitializer {

    private final String name;
    private final TypeConstructor initializedTypeConstructor;
    private final Type returnType;

    public ValidCovariantInitializer(String name, TypeConstructor initializedTypeConstructor, Type returnType) {
        this.name = name;
        this.returnType = returnType;
        this.initializedTypeConstructor = initializedTypeConstructor;
    }

    public static ValidCovariantInitializer of(String name, TypeConstructor initializedTypeConstructor, Type returnType) {
        return new ValidCovariantInitializer(name, initializedTypeConstructor, returnType);
    }

    public String getName() {
        return name;
    }

    public TypeConstructor getInitializedTypeConstructor() {
        return initializedTypeConstructor;
    }

    public Type getReturnType() {
        return returnType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValidCovariantInitializer that = (ValidCovariantInitializer) o;
        return getName().equals(that.getName()) && getInitializedTypeConstructor().equals(that.getInitializedTypeConstructor()) && getReturnType().equals(that.getReturnType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getInitializedTypeConstructor(), getReturnType());
    }

    @Override
    public String toString() {
        return "CovariantInitializer{" +
                "name='" + name + '\'' +
                ", initializedTypeConstructor=" + initializedTypeConstructor +
                ", returnType=" + returnType +
                '}';
    }
}
