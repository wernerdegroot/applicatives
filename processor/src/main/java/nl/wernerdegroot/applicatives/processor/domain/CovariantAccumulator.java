package nl.wernerdegroot.applicatives.processor.domain;

import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;

import java.util.Map;
import java.util.Objects;

public class CovariantAccumulator implements HasReplaceableTypeParameterNames<CovariantAccumulator> {
    private final String name;
    private final TypeConstructor inputTypeConstructor;
    private final TypeConstructor partiallyAccumulatedTypeConstructor;
    private final TypeConstructor accumulatedTypeConstructor;

    public CovariantAccumulator(String name, TypeConstructor inputTypeConstructor, TypeConstructor partiallyAccumulatedTypeConstructor, TypeConstructor accumulatedTypeConstructor) {
        this.name = name;
        this.inputTypeConstructor = inputTypeConstructor;
        this.partiallyAccumulatedTypeConstructor = partiallyAccumulatedTypeConstructor;
        this.accumulatedTypeConstructor = accumulatedTypeConstructor;
    }

    public static CovariantAccumulator of(String name, TypeConstructor inputTypeConstructor, TypeConstructor partiallyAccumulatedTypeConstructor, TypeConstructor accumulatedTypeConstructor) {
        return new CovariantAccumulator(name, inputTypeConstructor, partiallyAccumulatedTypeConstructor, accumulatedTypeConstructor);
    }

    @Override
    public CovariantAccumulator replaceTypeParameterNames(Map<TypeParameterName, TypeParameterName> replacements) {
        return CovariantAccumulator.of(
                name,
                inputTypeConstructor.replaceTypeParameterNames(replacements),
                partiallyAccumulatedTypeConstructor.replaceTypeParameterNames(replacements),
                accumulatedTypeConstructor.replaceTypeParameterNames(replacements)
        );
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CovariantAccumulator that = (CovariantAccumulator) o;
        return getName().equals(that.getName()) && getInputTypeConstructor().equals(that.getInputTypeConstructor()) && getPartiallyAccumulatedTypeConstructor().equals(that.getPartiallyAccumulatedTypeConstructor()) && getAccumulatedTypeConstructor().equals(that.getAccumulatedTypeConstructor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getInputTypeConstructor(), getPartiallyAccumulatedTypeConstructor(), getAccumulatedTypeConstructor());
    }

    @Override
    public String toString() {
        return "CovariantAccumulator{" +
                "name='" + name + '\'' +
                ", inputTypeConstructor=" + inputTypeConstructor +
                ", partiallyAccumulatedTypeConstructor=" + partiallyAccumulatedTypeConstructor +
                ", accumulatedTypeConstructor=" + accumulatedTypeConstructor +
                '}';
    }
}
