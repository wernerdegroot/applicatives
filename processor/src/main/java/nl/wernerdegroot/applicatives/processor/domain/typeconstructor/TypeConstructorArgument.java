package nl.wernerdegroot.applicatives.processor.domain.typeconstructor;

import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import nl.wernerdegroot.applicatives.processor.domain.Variance;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import nl.wernerdegroot.applicatives.processor.domain.type.TypeArgument;

import java.util.Map;
import java.util.Objects;

import static nl.wernerdegroot.applicatives.processor.domain.Variance.INVARIANT;

public class TypeConstructorArgument {

    private final Variance variance;
    private final TypeConstructor typeConstructor;

    public TypeConstructorArgument(Variance variance, TypeConstructor typeConstructor) {
        this.variance = variance;
        this.typeConstructor = typeConstructor;
    }

    public static TypeConstructorArgument of(Variance variance, TypeConstructor typeConstructor) {
        return new TypeConstructorArgument(variance, typeConstructor);
    }

    public TypeConstructorArgument replaceAllTypeParameterNames(Map<TypeParameterName, TypeParameterName> replacement) {
        return new TypeConstructorArgument(variance, typeConstructor.replaceTypeParameterNames(replacement));
    }

    public boolean canAccept(TypeConstructorArgument that) {
        if (this.variance == that.variance || that.variance == INVARIANT) {
            switch (this.variance) {
                case INVARIANT:
                    return this.typeConstructor.equals(that.typeConstructor);
                case COVARIANT:
                    return this.typeConstructor.canAccept(that.typeConstructor);
                case CONTRAVARIANT:
                    return that.typeConstructor.canAccept(this.typeConstructor);
                default:
                    throw new RuntimeException("Not implemented");
            }
        } else {
            return false;
        }
    }

    public TypeArgument apply(Type toApplyTo) {
        return new TypeArgument(variance, typeConstructor.apply(toApplyTo));
    }

    public Variance getVariance() {
        return variance;
    }

    public TypeConstructor getTypeConstructor() {
        return typeConstructor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeConstructorArgument that = (TypeConstructorArgument) o;
        return getVariance() == that.getVariance() && getTypeConstructor().equals(that.getTypeConstructor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVariance(), getTypeConstructor());
    }

    @Override
    public String toString() {
        return "TypeConstructorArgument{" +
                "variance=" + variance +
                ", typeConstructor=" + typeConstructor +
                '}';
    }
}
