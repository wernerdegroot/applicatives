package nl.wernerdegroot.applicatives.processor.domain.type;

import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import nl.wernerdegroot.applicatives.processor.domain.Variance;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructorArgument;

import java.util.Map;
import java.util.Objects;

import static nl.wernerdegroot.applicatives.processor.domain.Variance.COVARIANT;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.OBJECT;

public class TypeArgument {

    private final Variance variance;
    private final Type type;

    public TypeArgument(Variance variance, Type type) {
        this.variance = variance;
        this.type = type;
    }

    public static TypeArgument of(Variance variance, Type type) {
        return new TypeArgument(variance, type);
    }

    public static TypeArgument wildcard() {
        return new TypeArgument(COVARIANT, OBJECT);
    }

    public boolean contains(TypeParameterName needle) {
        return type.contains(needle);
    }

    public TypeArgument replaceAllTypeParameterNames(Map<TypeParameterName, TypeParameterName> replacement) {
        return new TypeArgument(variance, type.replaceAllTypeParameterNames(replacement));
    }

    public TypeConstructorArgument asTypeConstructorArgument() {
        return new TypeConstructorArgument(variance, type.asTypeConstructor());
    }

    public TypeConstructorArgument asTypeConstructorArgumentWithPlaceholderFor(TypeParameterName needle) {
        return new TypeConstructorArgument(variance, type.asTypeConstructorWithPlaceholderFor(needle));
    }

    public Variance getVariance() {
        return variance;
    }

    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeArgument that = (TypeArgument) o;
        return getVariance() == that.getVariance() && getType().equals(that.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVariance(), getType());
    }

    @Override
    public String toString() {
        return "TypeArgument{" +
                "variance=" + variance +
                ", type=" + type +
                '}';
    }
}
