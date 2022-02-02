package nl.wernerdegroot.applicatives.processor.domain;

import nl.wernerdegroot.applicatives.processor.domain.containing.ContainingClass;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class Method {
    private final Set<Modifier> modifiers;
    private final List<TypeParameter> typeParameters;
    // Empty `Optional` means `void` return type:
    private final Optional<Type> returnType;
    private final String name;
    private final List<Parameter> parameters;
    private final ContainingClass containingClass;

    public Method(Set<Modifier> modifiers, List<TypeParameter> typeParameters, Optional<Type> returnType, String name, List<Parameter> parameters, ContainingClass containingClass) {
        this.modifiers = modifiers;
        this.typeParameters = typeParameters;
        this.returnType = returnType;
        this.name = name;
        this.parameters = parameters;
        this.containingClass = containingClass;
    }

    public static Method of(Set<Modifier> modifiers, List<TypeParameter> typeParameters, Optional<Type> returnType, String name, List<Parameter> parameters, ContainingClass containingClass) {
        return new Method(modifiers, typeParameters, returnType, name, parameters, containingClass);
    }

    public Set<Modifier> getModifiers() {
        return modifiers;
    }

    public List<TypeParameter> getTypeParameters() {
        return typeParameters;
    }

    public Optional<Type> getReturnType() {
        return returnType;
    }

    public String getName() {
        return name;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public ContainingClass getContainingClass() {
        return containingClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Method method = (Method) o;
        return getModifiers().equals(method.getModifiers()) && getTypeParameters().equals(method.getTypeParameters()) && getReturnType().equals(method.getReturnType()) && getName().equals(method.getName()) && getParameters().equals(method.getParameters()) && getContainingClass().equals(method.getContainingClass());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getModifiers(), getTypeParameters(), getReturnType(), getName(), getParameters(), getContainingClass());
    }

    @Override
    public String toString() {
        return "Method{" +
                "modifiers=" + modifiers +
                ", typeParameters=" + typeParameters +
                ", returnType=" + returnType +
                ", name='" + name + '\'' +
                ", parameters=" + parameters +
                ", containing=" + containingClass +
                '}';
    }
}
