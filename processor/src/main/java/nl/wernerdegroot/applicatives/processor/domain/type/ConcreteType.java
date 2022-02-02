package nl.wernerdegroot.applicatives.processor.domain.type;

import nl.wernerdegroot.applicatives.processor.domain.FullyQualifiedName;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.ConcreteTypeConstructor;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

public class ConcreteType implements Type {

    private final FullyQualifiedName fullyQualifiedName;
    private final List<Type> typeArguments;

    public ConcreteType(FullyQualifiedName fullyQualifiedName, List<Type> typeArguments) {
        this.fullyQualifiedName = fullyQualifiedName;
        this.typeArguments = typeArguments;
    }

    public static ConcreteType of(FullyQualifiedName fullyQualifiedName, List<Type> typeArguments) {
        return new ConcreteType(fullyQualifiedName, typeArguments);
    }

    @Override
    public <R> R match(Function<GenericType, R> matchGeneric, Function<ConcreteType, R> matchConcrete, Function<WildcardType, R> matchWildcard, Function<ArrayType, R> matchArray) {
        return matchConcrete.apply(this);
    }

    @Override
    public ConcreteTypeConstructor asTypeConstructor() {
        return TypeConstructor.concrete(
                fullyQualifiedName,
                typeArguments.stream().map(Type::asTypeConstructor).collect(toList())
        );
    }

    @Override
    public TypeConstructor asTypeConstructorWithPlaceholderFor(TypeParameterName needle) {
        return TypeConstructor.concrete(
                fullyQualifiedName,
                typeArguments.stream().map(typeArgument -> typeArgument.asTypeConstructorWithPlaceholderFor(needle)).collect(toList())
        );
    }

    @Override
    public boolean contains(TypeParameterName needle) {
        return typeArguments.stream().anyMatch(typeArgument -> typeArgument.contains(needle));
    }

    @Override
    public ConcreteType replaceAllTypeParameterNames(Map<TypeParameterName, TypeParameterName> replacement) {
        List<Type> replacedTypeArguments = typeArguments
                .stream()
                .map(typeArgument -> typeArgument.replaceAllTypeParameterNames(replacement))
                .collect(toList());

        return Type.concrete(fullyQualifiedName, replacedTypeArguments);
    }

    public FullyQualifiedName getFullyQualifiedName() {
        return fullyQualifiedName;
    }

    public List<Type> getTypeArguments() {
        return typeArguments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConcreteType that = (ConcreteType) o;
        return getFullyQualifiedName().equals(that.getFullyQualifiedName()) && getTypeArguments().equals(that.getTypeArguments());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFullyQualifiedName(), getTypeArguments());
    }

    @Override
    public String toString() {
        return "ConcreteType{" +
                "fullyQualifiedName=" + fullyQualifiedName +
                ", typeArguments=" + typeArguments +
                '}';
    }
}
