package nl.wernerdegroot.applicatives.processor.domain;

import nl.wernerdegroot.applicatives.processor.domain.type.Type;

import java.util.Objects;

public final class Parameter {

    private final Type type;
    private final String name;

    public Parameter(Type type, String name) {
        this.type = type;
        this.name = name;
    }

    public static Parameter of(Type type, String name) {
        return new Parameter(type, name);
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
        return Objects.equals(getType(), parameter.getType()) && Objects.equals(getName(), parameter.getName());
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
