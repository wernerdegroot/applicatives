package nl.wernerdegroot.applicatives.processor.domain;

import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;

import java.util.Map;
import java.util.Objects;

public final class CovariantInitializer implements HasReplaceableTypeParameterNames<CovariantInitializer> {
    private final String name;
    private final TypeConstructor initializedTypeConstructor;

    public CovariantInitializer(String name, TypeConstructor initializedTypeConstructor) {
        this.name = name;
        this.initializedTypeConstructor = initializedTypeConstructor;
    }

    public static CovariantInitializer of(String name, TypeConstructor initializedTypeConstructor) {
        return new CovariantInitializer(name, initializedTypeConstructor);
    }

    @Override
    public CovariantInitializer replaceTypeParameterNames(Map<TypeParameterName, TypeParameterName> replacements) {
        return CovariantInitializer.of(
                name,
                initializedTypeConstructor.replaceTypeParameterNames(replacements)
        );
    }

    public String getName() {
        return name;
    }

    public TypeConstructor getInitializedTypeConstructor() {
        return initializedTypeConstructor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CovariantInitializer that = (CovariantInitializer) o;
        return Objects.equals(getName(), that.getName()) && Objects.equals(getInitializedTypeConstructor(), that.getInitializedTypeConstructor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getInitializedTypeConstructor());
    }

    @Override
    public String toString() {
        return "CovariantInitializer{" +
                "name='" + name + '\'' +
                ", initializedTypeConstructor=" + initializedTypeConstructor +
                '}';
    }
}
