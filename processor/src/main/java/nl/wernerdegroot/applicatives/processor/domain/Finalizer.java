package nl.wernerdegroot.applicatives.processor.domain;

import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;

import java.util.Map;
import java.util.Objects;

public final class Finalizer implements HasReplaceableTypeParameterNames<Finalizer> {
    private final String name;
    private final TypeConstructor toFinalizeTypeConstructor;
    private final TypeConstructor finalizedTypeConstructor;

    public Finalizer(String name, TypeConstructor toFinalizeTypeConstructor, TypeConstructor finalizedTypeConstructor) {
        this.name = name;
        this.toFinalizeTypeConstructor = toFinalizeTypeConstructor;
        this.finalizedTypeConstructor = finalizedTypeConstructor;
    }

    public static Finalizer of(String name, TypeConstructor toFinalizeTypeConstructor, TypeConstructor finalizedTypeConstructor) {
        return new Finalizer(name, toFinalizeTypeConstructor, finalizedTypeConstructor);
    }

    @Override
    public Finalizer replaceTypeParameterNames(Map<TypeParameterName, TypeParameterName> replacements) {
        return Finalizer.of(
                name,
                toFinalizeTypeConstructor.replaceTypeParameterNames(replacements),
                finalizedTypeConstructor.replaceTypeParameterNames(replacements)
        );
    }

    public String getName() {
        return name;
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
        Finalizer that = (Finalizer) o;
        return Objects.equals(getName(), that.getName()) && Objects.equals(getToFinalizeTypeConstructor(), that.getToFinalizeTypeConstructor()) && Objects.equals(getFinalizedTypeConstructor(), that.getFinalizedTypeConstructor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getToFinalizeTypeConstructor(), getFinalizedTypeConstructor());
    }

    @Override
    public String toString() {
        return "CovariantFinalizer{" +
                "name='" + name + '\'' +
                ", toFinalizeTypeConstructor=" + toFinalizeTypeConstructor +
                ", finalizedTypeConstructor=" + finalizedTypeConstructor +
                '}';
    }
}
