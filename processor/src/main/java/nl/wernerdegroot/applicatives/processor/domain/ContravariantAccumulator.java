package nl.wernerdegroot.applicatives.processor.domain;

import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;

import java.util.Map;
import java.util.Objects;

public final class ContravariantAccumulator implements HasReplaceableTypeParameterNames<ContravariantAccumulator> {
    private final String name;
    private final TypeConstructor inputTypeConstructor;
    private final TypeConstructor partiallyAccumulatedTypeConstructor;
    private final TypeConstructor accumulatedTypeConstructor;

    public ContravariantAccumulator(String name, TypeConstructor inputTypeConstructor, TypeConstructor partiallyAccumulatedTypeConstructor, TypeConstructor accumulatedTypeConstructor) {
        this.name = name;
        this.inputTypeConstructor = inputTypeConstructor;
        this.partiallyAccumulatedTypeConstructor = partiallyAccumulatedTypeConstructor;
        this.accumulatedTypeConstructor = accumulatedTypeConstructor;
    }

    public static ContravariantAccumulator of(String name, TypeConstructor inputTypeConstructor, TypeConstructor partiallyAccumulatedTypeConstructor, TypeConstructor accumulatedTypeConstructor) {
        return new ContravariantAccumulator(name, inputTypeConstructor, partiallyAccumulatedTypeConstructor, accumulatedTypeConstructor);
    }

    @Override
    public ContravariantAccumulator replaceTypeParameterNames(Map<TypeParameterName, TypeParameterName> replacements) {
        return ContravariantAccumulator.of(
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
        ContravariantAccumulator that = (ContravariantAccumulator) o;
        return Objects.equals(getName(), that.getName()) && Objects.equals(getInputTypeConstructor(), that.getInputTypeConstructor()) && Objects.equals(getPartiallyAccumulatedTypeConstructor(), that.getPartiallyAccumulatedTypeConstructor()) && Objects.equals(getAccumulatedTypeConstructor(), that.getAccumulatedTypeConstructor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getInputTypeConstructor(), getPartiallyAccumulatedTypeConstructor(), getAccumulatedTypeConstructor());
    }

    @Override
    public String toString() {
        return "ContravariantAccumulator{" +
                "name='" + name + '\'' +
                ", inputTypeConstructor=" + inputTypeConstructor +
                ", partiallyAccumulatedTypeConstructor=" + partiallyAccumulatedTypeConstructor +
                ", accumulatedTypeConstructor=" + accumulatedTypeConstructor +
                '}';
    }
}
