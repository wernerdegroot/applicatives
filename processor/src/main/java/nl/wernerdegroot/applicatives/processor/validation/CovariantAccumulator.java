package nl.wernerdegroot.applicatives.processor.validation;

import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;

import java.util.Objects;

public class CovariantAccumulator {

    private final String name;
    private final TypeConstructor accumulationTypeConstructor;
    private final Type firstParameterType;
    private final TypeConstructor permissiveAccumulationTypeConstructor;
    private final TypeConstructor inputTypeConstructor;

    public CovariantAccumulator(String name, TypeConstructor accumulationTypeConstructor, Type firstParameterType, TypeConstructor permissiveAccumulationTypeConstructor, TypeConstructor inputTypeConstructor) {
        this.name = name;
        this.accumulationTypeConstructor = accumulationTypeConstructor;
        this.firstParameterType = firstParameterType;
        this.permissiveAccumulationTypeConstructor = permissiveAccumulationTypeConstructor;
        this.inputTypeConstructor = inputTypeConstructor;
    }

    public static CovariantAccumulator of(String name, TypeConstructor accumulationTypeConstructor, Type firstParameterType, TypeConstructor permissiveAccumulationTypeConstructor, TypeConstructor inputTypeConstructor) {
        return new CovariantAccumulator(name, accumulationTypeConstructor, firstParameterType, permissiveAccumulationTypeConstructor, inputTypeConstructor);
    }

    public String getName() {
        return name;
    }

    public TypeConstructor getAccumulationTypeConstructor() {
        return accumulationTypeConstructor;
    }

    public Type getFirstParameterType() {
        return firstParameterType;
    }

    public TypeConstructor getPermissiveAccumulationTypeConstructor() {
        return permissiveAccumulationTypeConstructor;
    }

    public TypeConstructor getInputTypeConstructor() {
        return inputTypeConstructor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CovariantAccumulator that = (CovariantAccumulator) o;
        return getName().equals(that.getName()) && getAccumulationTypeConstructor().equals(that.getAccumulationTypeConstructor()) && getFirstParameterType().equals(that.getFirstParameterType()) && getPermissiveAccumulationTypeConstructor().equals(that.getPermissiveAccumulationTypeConstructor()) && getInputTypeConstructor().equals(that.getInputTypeConstructor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getAccumulationTypeConstructor(), getFirstParameterType(), getPermissiveAccumulationTypeConstructor(), getInputTypeConstructor());
    }

    @Override
    public String toString() {
        return "CovariantAccumulator{" +
                "name='" + name + '\'' +
                ", accumulationTypeConstructor=" + accumulationTypeConstructor +
                ", firstParameterType=" + firstParameterType +
                ", permissiveAccumulationTypeConstructor=" + permissiveAccumulationTypeConstructor +
                ", inputTypeConstructor=" + inputTypeConstructor +
                '}';
    }
}
