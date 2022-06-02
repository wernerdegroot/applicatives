package nl.wernerdegroot.applicatives.processor.domain;

import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;

import java.util.Map;
import java.util.Objects;

public final class CovariantInitializer implements HasReplaceableTypeParameterNames<CovariantInitializer> {
    private final String name;
    private final TypeConstructor toInitializeTypeConstructor;
    private final TypeConstructor initializedTypeConstructor;

    public CovariantInitializer(String name, TypeConstructor toInitializeTypeConstructor, TypeConstructor initializedTypeConstructor) {
        this.name = name;
        this.toInitializeTypeConstructor = toInitializeTypeConstructor;
        this.initializedTypeConstructor = initializedTypeConstructor;
    }

    public static CovariantInitializer of(String name, TypeConstructor toInitializeTypeConstructor, TypeConstructor initializedTypeConstructor) {
        return new CovariantInitializer(name, toInitializeTypeConstructor, initializedTypeConstructor);
    }

    @Override
    public CovariantInitializer replaceTypeParameterNames(Map<TypeParameterName, TypeParameterName> replacements) {
        return CovariantInitializer.of(
                name,
                toInitializeTypeConstructor.replaceTypeParameterNames(replacements),
                initializedTypeConstructor.replaceTypeParameterNames(replacements)
        );
    }

    public String getName() {
        return name;
    }

    public TypeConstructor getToInitializeTypeConstructor() {
        return toInitializeTypeConstructor;
    }

    public TypeConstructor getInitializedTypeConstructor() {
        return initializedTypeConstructor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CovariantInitializer that = (CovariantInitializer) o;
        return Objects.equals(getName(), that.getName()) && Objects.equals(getToInitializeTypeConstructor(), that.getToInitializeTypeConstructor()) && Objects.equals(getInitializedTypeConstructor(), that.getInitializedTypeConstructor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getToInitializeTypeConstructor(), getInitializedTypeConstructor());
    }

    @Override
    public String toString() {
        return "CovariantInitializer{" +
                "name='" + name + '\'' +
                ", toInitializeTypeConstructor=" + toInitializeTypeConstructor +
                ", initializedTypeConstructor=" + initializedTypeConstructor +
                '}';
    }
}
