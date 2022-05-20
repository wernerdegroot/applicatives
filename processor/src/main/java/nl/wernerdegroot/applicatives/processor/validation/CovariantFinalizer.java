package nl.wernerdegroot.applicatives.processor.validation;

import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;

import java.util.Objects;

public class CovariantFinalizer {

    private final String name;
    private final Type parameterType;
    private final TypeConstructor toFinalizeTypeConstructor;
    private final TypeConstructor finalizedTypeConstructor;

    public CovariantFinalizer(String name, Type parameterType, TypeConstructor toFinalizeTypeConstructor, TypeConstructor finalizedTypeConstructor) {
        this.name = name;
        this.parameterType = parameterType;
        this.toFinalizeTypeConstructor = toFinalizeTypeConstructor;
        this.finalizedTypeConstructor = finalizedTypeConstructor;
    }

    public static CovariantFinalizer of(String name, Type parameterType, TypeConstructor toFinalizeTypeConstructor, TypeConstructor finalizedTypeConstructor) {
        return new CovariantFinalizer(name, parameterType, toFinalizeTypeConstructor, finalizedTypeConstructor);
    }

    public String getName() {
        return name;
    }

    public Type getParameterType() {
        return parameterType;
    }

    public TypeConstructor getToFinalizeTypeConstructor() {
        return toFinalizeTypeConstructor;
    }

    public TypeConstructor getFinalizedTypeConstructor() {
        return finalizedTypeConstructor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CovariantFinalizer that = (CovariantFinalizer) o;
        return getName().equals(that.getName()) && getParameterType().equals(that.getParameterType()) && getToFinalizeTypeConstructor().equals(that.getToFinalizeTypeConstructor()) && getFinalizedTypeConstructor().equals(that.getFinalizedTypeConstructor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getParameterType(), getToFinalizeTypeConstructor(), getFinalizedTypeConstructor());
    }

    @Override
    public String toString() {
        return "CovariantFinalizer{" +
                "name='" + name + '\'' +
                ", parameterType=" + parameterType +
                ", toFinalizeTypeConstructor=" + toFinalizeTypeConstructor +
                ", finalizedTypeConstructor=" + finalizedTypeConstructor +
                '}';
    }
}
