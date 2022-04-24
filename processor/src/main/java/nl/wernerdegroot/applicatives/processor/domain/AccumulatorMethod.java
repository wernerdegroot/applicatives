package nl.wernerdegroot.applicatives.processor.domain;

import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;

import java.util.Objects;

public class AccumulatorMethod {

    private final String name;
    private final TypeConstructor accumulationTypeConstructor;
    private final TypeConstructor permissiveAccumulationTypeConstructor;
    private final TypeConstructor inputTypeConstructor;

    public AccumulatorMethod(String name, TypeConstructor accumulationTypeConstructor, TypeConstructor permissiveAccumulationTypeConstructor, TypeConstructor inputTypeConstructor) {
        this.name = name;
        this.accumulationTypeConstructor = accumulationTypeConstructor;
        this.permissiveAccumulationTypeConstructor = permissiveAccumulationTypeConstructor;
        this.inputTypeConstructor = inputTypeConstructor;
    }

    public static AccumulatorMethod of(String name, TypeConstructor accumulationTypeConstructor, TypeConstructor permissiveAccumulationTypeConstructor, TypeConstructor inputTypeConstructor) {
        return new AccumulatorMethod(name, accumulationTypeConstructor, permissiveAccumulationTypeConstructor, inputTypeConstructor);
    }

    public String getName() {
        return name;
    }

    public TypeConstructor getAccumulationTypeConstructor() {
        return accumulationTypeConstructor;
    }

    public TypeConstructor getPermissiveAccumulationTypeConstructor() {
        return permissiveAccumulationTypeConstructor;
    }

    public TypeConstructor getInputTypeConstructor() {
        return inputTypeConstructor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccumulatorMethod that = (AccumulatorMethod) o;
        return getName().equals(that.getName()) && getAccumulationTypeConstructor().equals(that.getAccumulationTypeConstructor()) && getPermissiveAccumulationTypeConstructor().equals(that.getPermissiveAccumulationTypeConstructor()) && getInputTypeConstructor().equals(that.getInputTypeConstructor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getAccumulationTypeConstructor(), getPermissiveAccumulationTypeConstructor(), getInputTypeConstructor());
    }

    @Override
    public String toString() {
        return "AccumulatorMethod{" +
                "name='" + name + '\'' +
                ", accumulationTypeConstructor=" + accumulationTypeConstructor +
                ", permissiveAccumulationTypeConstructor=" + permissiveAccumulationTypeConstructor +
                ", inputTypeConstructor=" + inputTypeConstructor +
                '}';
    }
}
