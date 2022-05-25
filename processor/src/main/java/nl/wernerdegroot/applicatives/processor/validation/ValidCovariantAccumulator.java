package nl.wernerdegroot.applicatives.processor.validation;

import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;

import java.util.Objects;

public class ValidCovariantAccumulator {

    private final String name;
    private final TypeConstructor inputTypeConstructor;
    private final TypeConstructor partiallyAccumulatedTypeConstructor;
    private final TypeConstructor accumulatedTypeConstructor;
    private final Type firstParameterType;

    public ValidCovariantAccumulator(String name, TypeConstructor inputTypeConstructor, TypeConstructor partiallyAccumulatedTypeConstructor, TypeConstructor accumulatedTypeConstructor, Type firstParameterType) {
        this.name = name;
        this.inputTypeConstructor = inputTypeConstructor;
        this.partiallyAccumulatedTypeConstructor = partiallyAccumulatedTypeConstructor;
        this.accumulatedTypeConstructor = accumulatedTypeConstructor;
        this.firstParameterType = firstParameterType;
    }

    public static ValidCovariantAccumulator of(String name, TypeConstructor inputTypeConstructor, TypeConstructor partiallyAccumulatedTypeConstructor, TypeConstructor accumulatedTypeConstructor, Type firstParameterType) {
        return new ValidCovariantAccumulator(name, inputTypeConstructor, partiallyAccumulatedTypeConstructor, accumulatedTypeConstructor, firstParameterType);
    }

    public String getName() {
        return name;
    }

    public TypeConstructor getInputTypeConstructor() {
        return inputTypeConstructor;
    }

    public TypeConstructor getPartiallyAccumulatedTypeConstructor() {
        return partiallyAccumulatedTypeConstructor;
    }

    public TypeConstructor getAccumulatedTypeConstructor() {
        return accumulatedTypeConstructor;
    }

    public Type getFirstParameterType() {
        return firstParameterType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValidCovariantAccumulator that = (ValidCovariantAccumulator) o;
        return getName().equals(that.getName()) && getInputTypeConstructor().equals(that.getInputTypeConstructor()) && getPartiallyAccumulatedTypeConstructor().equals(that.getPartiallyAccumulatedTypeConstructor()) && getAccumulatedTypeConstructor().equals(that.getAccumulatedTypeConstructor()) && getFirstParameterType().equals(that.getFirstParameterType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getInputTypeConstructor(), getPartiallyAccumulatedTypeConstructor(), getAccumulatedTypeConstructor(), getFirstParameterType());
    }

    @Override
    public String toString() {
        return "ValidCovariantAccumulator{" +
                "name='" + name + '\'' +
                ", inputTypeConstructor=" + inputTypeConstructor +
                ", partiallyAccumulatedTypeConstructor=" + partiallyAccumulatedTypeConstructor +
                ", accumulatedTypeConstructor=" + accumulatedTypeConstructor +
                ", firstParameterType=" + firstParameterType +
                '}';
    }
}
