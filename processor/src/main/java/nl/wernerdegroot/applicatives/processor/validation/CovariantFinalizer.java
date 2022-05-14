package nl.wernerdegroot.applicatives.processor.validation;

import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;

import java.util.Objects;

public class CovariantFinalizer {

    private final String name;
    private final Type parameterType;
    private final TypeConstructor accumulationTypeConstructor;
    private final TypeConstructor resultTypeConstructor;

    public CovariantFinalizer(String name, Type parameterType, TypeConstructor accumulationTypeConstructor, TypeConstructor resultTypeConstructor) {
        this.name = name;
        this.parameterType = parameterType;
        this.accumulationTypeConstructor = accumulationTypeConstructor;
        this.resultTypeConstructor = resultTypeConstructor;
    }

    public static CovariantFinalizer of(String name, Type parameterType, TypeConstructor accumulationTypeConstructor, TypeConstructor resultTypeConstructor) {
        return new CovariantFinalizer(name, parameterType, accumulationTypeConstructor, resultTypeConstructor);
    }

    public String getName() {
        return name;
    }

    public Type getParameterType() {
        return parameterType;
    }

    public TypeConstructor getAccumulationTypeConstructor() {
        return accumulationTypeConstructor;
    }

    public TypeConstructor getResultTypeConstructor() {
        return resultTypeConstructor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CovariantFinalizer that = (CovariantFinalizer) o;
        return getName().equals(that.getName()) && getParameterType().equals(that.getParameterType()) && getAccumulationTypeConstructor().equals(that.getAccumulationTypeConstructor()) && getResultTypeConstructor().equals(that.getResultTypeConstructor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getParameterType(), getAccumulationTypeConstructor(), getResultTypeConstructor());
    }

    @Override
    public String toString() {
        return "CovariantFinalizer{" +
                "name='" + name + '\'' +
                ", parameterType=" + parameterType +
                ", accumulationTypeConstructor=" + accumulationTypeConstructor +
                ", resultTypeConstructor=" + resultTypeConstructor +
                '}';
    }
}
