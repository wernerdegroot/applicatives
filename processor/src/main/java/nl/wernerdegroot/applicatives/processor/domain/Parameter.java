package nl.wernerdegroot.applicatives.processor.domain;

import nl.wernerdegroot.applicatives.processor.domain.type.Type;

import java.util.Map;
import java.util.Objects;

public class Parameter {

    private final Type type;
    private final String name;

    public Parameter(Type type, String name) {
        this.type = type;
        this.name = name;
    }

    public static Parameter of(Type type, String name) {
        return new Parameter(type, name);
    }

    public Parameter replaceAllTypeParameterNames(Map<TypeParameterName, TypeParameterName> replacement) {
        return Parameter.of(
                type.replaceAllTypeParameterNames(replacement),
                name
        );
    }

    public Parameter replaceParameterName(Map<String, String> replacement) {
        return Parameter.of(
                type,
                replacement.getOrDefault(name, name)
        );
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Parameter parameter = (Parameter) o;
        return getType().equals(parameter.getType()) && getName().equals(parameter.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getName());
    }

    @Override
    public String toString() {
        return "Parameter{" +
                "type=" + type +
                ", name='" + name + '\'' +
                '}';
    }
}
