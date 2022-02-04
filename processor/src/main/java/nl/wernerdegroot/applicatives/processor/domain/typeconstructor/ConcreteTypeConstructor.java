package nl.wernerdegroot.applicatives.processor.domain.typeconstructor;

import nl.wernerdegroot.applicatives.processor.domain.FullyQualifiedName;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import nl.wernerdegroot.applicatives.processor.domain.type.ConcreteType;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

public class ConcreteTypeConstructor implements TypeConstructor {

    private final FullyQualifiedName fullyQualifiedName;
    private final List<TypeConstructor> typeArguments;

    public ConcreteTypeConstructor(FullyQualifiedName fullyQualifiedName, List<TypeConstructor> typeArguments) {
        this.fullyQualifiedName = fullyQualifiedName;
        this.typeArguments = typeArguments;
    }

    public static ConcreteTypeConstructor of(FullyQualifiedName fullyQualifiedName, List<TypeConstructor> typeArguments) {
        return new ConcreteTypeConstructor(fullyQualifiedName, typeArguments);
    }

    @Override
    public ConcreteTypeConstructor replaceAllTypeParameterNames(Map<TypeParameterName, TypeParameterName> replacement) {
        List<TypeConstructor> replacedTypeArguments = typeArguments
                .stream()
                .map(typeArgument -> typeArgument.replaceAllTypeParameterNames(replacement))
                .collect(toList());

        return TypeConstructor.concrete(fullyQualifiedName, replacedTypeArguments);
    }

    @Override
    public TypeConstructor replaceAll(TypeConstructor needle, TypeConstructor replacement) {
        if (Objects.equals(this, needle)) {
            return replacement;
        }

        List<TypeConstructor> replacedTypeArguments = typeArguments
                .stream()
                .map(typeArgument -> typeArgument.replaceAll(needle, replacement))
                .collect(toList());

        return TypeConstructor.concrete(fullyQualifiedName, replacedTypeArguments);
    }

    @Override
    public ConcreteType apply(Type toApplyTo) {
        List<Type> appliedTypeArguments = typeArguments
                .stream()
                .map(typeArgument -> typeArgument.apply(toApplyTo))
                .collect(toList());

        return Type.concrete(fullyQualifiedName, appliedTypeArguments);
    }

    public FullyQualifiedName getFullyQualifiedName() {
        return fullyQualifiedName;
    }

    public List<TypeConstructor> getTypeArguments() {
        return typeArguments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConcreteTypeConstructor that = (ConcreteTypeConstructor) o;
        return getFullyQualifiedName().equals(that.getFullyQualifiedName()) && getTypeArguments().equals(that.getTypeArguments());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFullyQualifiedName(), getTypeArguments());
    }

    @Override
    public String toString() {
        return "ConcreteTypeConstructor{" +
                "fullyQualifiedName=" + fullyQualifiedName +
                ", typeArguments=" + typeArguments +
                '}';
    }
}