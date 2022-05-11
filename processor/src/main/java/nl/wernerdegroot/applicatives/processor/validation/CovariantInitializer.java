package nl.wernerdegroot.applicatives.processor.validation;

import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;

import java.util.Objects;

public class CovariantInitializer {

    private final String name;
    private final Type returnType;
    private final TypeConstructor permissiveAccumulationTypeConstructor;

    public CovariantInitializer(String name, Type returnType, TypeConstructor permissiveAccumulationTypeConstructor) {
        this.name = name;
        this.returnType = returnType;
        this.permissiveAccumulationTypeConstructor = permissiveAccumulationTypeConstructor;
    }

    public static CovariantInitializer of(String name, Type returnType, TypeConstructor permissiveAccumulationTypeConstructor) {
        return new CovariantInitializer(name, returnType, permissiveAccumulationTypeConstructor);
    }

    public String getName() {
        return name;
    }

    public Type getReturnType() {
        return returnType;
    }

    public TypeConstructor getPermissiveAccumulationTypeConstructor() {
        return permissiveAccumulationTypeConstructor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CovariantInitializer that = (CovariantInitializer) o;
        return getName().equals(that.getName()) && getReturnType().equals(that.getReturnType()) && getPermissiveAccumulationTypeConstructor().equals(that.getPermissiveAccumulationTypeConstructor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getReturnType(), getPermissiveAccumulationTypeConstructor());
    }

    @Override
    public String toString() {
        return "CovariantInitializer{" +
                "name='" + name + '\'' +
                ", returnType=" + returnType +
                ", permissiveAccumulationTypeConstructor=" + permissiveAccumulationTypeConstructor +
                '}';
    }
}
