package nl.wernerdegroot.applicatives.processor.domain;

import nl.wernerdegroot.applicatives.processor.domain.containing.ContainingClass;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;

import java.util.*;

import static java.util.Arrays.asList;

public class Method {
    private final Set<FullyQualifiedName> annotations;
    private final Set<Modifier> modifiers;
    private final List<TypeParameter> typeParameters;
    // Empty `Optional` means `void` return type:
    private final Optional<Type> returnType;
    private final String name;
    private final List<Parameter> parameters;

    public Method(Set<FullyQualifiedName> annotations, Set<Modifier> modifiers, List<TypeParameter> typeParameters, Optional<Type> returnType, String name, List<Parameter> parameters) {
        this.annotations = annotations;
        this.modifiers = modifiers;
        this.typeParameters = typeParameters;
        this.returnType = returnType;
        this.name = name;
        this.parameters = parameters;
    }

    public static Method of(Set<FullyQualifiedName> annotations, Set<Modifier> modifiers, List<TypeParameter> typeParameters, Optional<Type> returnType, String name, List<Parameter> parameters) {
        return new Method(annotations, modifiers, typeParameters, returnType, name, parameters);
    }

    public Set<FullyQualifiedName> getAnnotations() {
        return annotations;
    }

    public boolean hasAnnotation(FullyQualifiedName annotation) {
        return annotations.contains(annotation);
    }

    public boolean hasAnnotationOf(Collection<FullyQualifiedName> annotations) {
        return annotations.stream().anyMatch(this::hasAnnotation);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Method method = (Method) o;
        return getAnnotations().equals(method.getAnnotations()) && getModifiers().equals(method.getModifiers()) && getTypeParameters().equals(method.getTypeParameters()) && getReturnType().equals(method.getReturnType()) && getName().equals(method.getName()) && getParameters().equals(method.getParameters());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAnnotations(), getModifiers(), getTypeParameters(), getReturnType(), getName(), getParameters());
    }

    @Override
    public String toString() {
        return "Method{" +
                "annotations=" + annotations +
                ", modifiers=" + modifiers +
                ", typeParameters=" + typeParameters +
                ", returnType=" + returnType +
                ", name='" + name + '\'' +
                ", parameters=" + parameters +
                '}';
    }
}
