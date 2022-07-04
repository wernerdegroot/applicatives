package nl.wernerdegroot.applicatives.processor.domain.typeconstructor;

import nl.wernerdegroot.applicatives.processor.domain.FullyQualifiedName;
import nl.wernerdegroot.applicatives.processor.domain.MayContainReferenceToTypeParameter;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import nl.wernerdegroot.applicatives.processor.domain.type.ConcreteType;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import nl.wernerdegroot.applicatives.processor.domain.type.TypeArgument;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

public final class ConcreteTypeConstructor implements TypeConstructor {

    private final FullyQualifiedName fullyQualifiedName;
    private final List<TypeConstructorArgument> typeConstructorArguments;

    public ConcreteTypeConstructor(FullyQualifiedName fullyQualifiedName, List<TypeConstructorArgument> typeConstructorArguments) {
        this.fullyQualifiedName = fullyQualifiedName;
        this.typeConstructorArguments = typeConstructorArguments;
    }

    public static ConcreteTypeConstructor of(FullyQualifiedName fullyQualifiedName, List<TypeConstructorArgument> typeConstructorArguments) {
        return new ConcreteTypeConstructor(fullyQualifiedName, typeConstructorArguments);
    }

    @Override
    public ConcreteTypeConstructor replaceTypeParameterNames(Map<TypeParameterName, TypeParameterName> replacement) {
        List<TypeConstructorArgument> replacedTypeConstructorArguments = typeConstructorArguments
                .stream()
                .map(typeConstructorArgument -> typeConstructorArgument.replaceAllTypeParameterNames(replacement))
                .collect(toList());

        return TypeConstructor.concrete(fullyQualifiedName, replacedTypeConstructorArguments);
    }

    @Override
    public boolean referencesTypeParameter(TypeParameterName typeParameterName) {
        return typeConstructorArguments.stream().anyMatch(argument -> argument.referencesTypeParameter(typeParameterName));
    }

    @Override
    public boolean canAccept(TypeConstructor typeConstructor) {
        if (typeConstructor instanceof ConcreteTypeConstructor) {
            ConcreteTypeConstructor that = (ConcreteTypeConstructor) typeConstructor;

            return this.fullyQualifiedNameEqualToThatOf(that) && this.typeArgumentsCanAccept(that);
        } else {
            return false;
        }
    }

    private boolean fullyQualifiedNameEqualToThatOf(ConcreteTypeConstructor that) {
        return Objects.equals(this.fullyQualifiedName, that.fullyQualifiedName);
    }

    private boolean typeArgumentsCanAccept(ConcreteTypeConstructor that) {
        if (this.typeConstructorArguments.size() != that.typeConstructorArguments.size()) {
            return false;
        }

        for (int i = 0; i < typeConstructorArguments.size(); ++i) {
            TypeConstructorArgument fromThis = this.typeConstructorArguments.get(i);
            TypeConstructorArgument fromThat = that.typeConstructorArguments.get(i);

            if (!fromThis.canAccept(fromThat)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ConcreteType apply(Type toApplyTo) {
        List<TypeArgument> typeArguments = typeConstructorArguments
                .stream()
                .map(typeArgument -> typeArgument.apply(toApplyTo))
                .collect(toList());

        return Type.concrete(fullyQualifiedName, typeArguments);
    }

    public FullyQualifiedName getFullyQualifiedName() {
        return fullyQualifiedName;
    }

    public List<TypeConstructorArgument> getTypeConstructorArguments() {
        return typeConstructorArguments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConcreteTypeConstructor that = (ConcreteTypeConstructor) o;
        return Objects.equals(getFullyQualifiedName(), that.getFullyQualifiedName()) && Objects.equals(getTypeConstructorArguments(), that.getTypeConstructorArguments());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFullyQualifiedName(), getTypeConstructorArguments());
    }

    @Override
    public String toString() {
        return "ConcreteTypeConstructor{" +
                "fullyQualifiedName=" + fullyQualifiedName +
                ", typeConstructorArguments=" + typeConstructorArguments +
                '}';
    }
}
