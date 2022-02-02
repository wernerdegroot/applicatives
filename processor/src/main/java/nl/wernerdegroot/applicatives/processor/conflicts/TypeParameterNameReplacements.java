package nl.wernerdegroot.applicatives.processor.conflicts;

import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Contains mappings between the type parameter names the programmer supplied
 * and their conflict-free alternatives. If no potential conflicts are found
 * these mappings will map each of the programmer's parameter names to itself.
 * <p>
 * Because there are two places in which a programmer may declare type parameters
 * (by declaring class type parameters or by declaring additional type parameters
 * in the composition method) this class has two distinct mappings for each.
 */
class TypeParameterNameReplacements {

    private final Map<TypeParameterName, TypeParameterName> classTypeParameterReplacements;
    private final Map<TypeParameterName, TypeParameterName> secondaryMethodTypeParameterReplacements;

    public TypeParameterNameReplacements(Map<TypeParameterName, TypeParameterName> classTypeParameterReplacements, Map<TypeParameterName, TypeParameterName> secondaryMethodTypeParameterReplacements) {
        this.classTypeParameterReplacements = classTypeParameterReplacements;
        this.secondaryMethodTypeParameterReplacements = secondaryMethodTypeParameterReplacements;
    }

    public static TypeParameterNameReplacements of(Map<TypeParameterName, TypeParameterName> classTypeParameterReplacements, Map<TypeParameterName, TypeParameterName> secondaryMethodTypeParameterReplacements) {
        return new TypeParameterNameReplacements(classTypeParameterReplacements, secondaryMethodTypeParameterReplacements);
    }

    public static Builder builder() {
        return new Builder();
    }

    public Map<TypeParameterName, TypeParameterName> getClassTypeParameterReplacements() {
        return classTypeParameterReplacements;
    }

    public Map<TypeParameterName, TypeParameterName> getSecondaryMethodTypeParameterReplacements() {
        return secondaryMethodTypeParameterReplacements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeParameterNameReplacements that = (TypeParameterNameReplacements) o;
        return getClassTypeParameterReplacements().equals(that.getClassTypeParameterReplacements()) && getSecondaryMethodTypeParameterReplacements().equals(that.getSecondaryMethodTypeParameterReplacements());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClassTypeParameterReplacements(), getSecondaryMethodTypeParameterReplacements());
    }

    @Override
    public String toString() {
        return "TypeParameterNameReplacements{" +
                "classTypeParameterReplacements=" + classTypeParameterReplacements +
                ", secondaryMethodTypeParameterReplacements=" + secondaryMethodTypeParameterReplacements +
                '}';
    }

    public static class Builder {

        private final Map<TypeParameterName, TypeParameterName> classTypeParameterReplacements = new HashMap<>();
        private final Map<TypeParameterName, TypeParameterName> secondaryMethodTypeParameterReplacements = new HashMap<>();

        public Builder replaceClassTypeParameter(TypeParameterName toReplace, TypeParameterName replacement) {
            classTypeParameterReplacements.put(toReplace, replacement);
            return this;
        }

        public Builder replaceSecondaryMethodTypeParameter(TypeParameterName toReplace, TypeParameterName replacement) {
            secondaryMethodTypeParameterReplacements.put(toReplace, replacement);
            return this;
        }

        public TypeParameterNameReplacements build() {
            return new TypeParameterNameReplacements(classTypeParameterReplacements, secondaryMethodTypeParameterReplacements);
        }
    }
}
