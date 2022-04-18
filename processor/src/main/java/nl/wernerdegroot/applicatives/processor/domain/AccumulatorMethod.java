package nl.wernerdegroot.applicatives.processor.domain;

import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;

import java.util.List;
import java.util.Objects;

public class AccumulatorMethod {

    private final TypeConstructor accumulationTypeConstructor;
    private final TypeConstructor permissiveAccumulationTypeConstructor;
    private final TypeConstructor inputTypeConstructor;
    private final List<TypeParameter> classTypeParameters;

    public AccumulatorMethod(TypeConstructor accumulationTypeConstructor, TypeConstructor permissiveAccumulationTypeConstructor, TypeConstructor inputTypeConstructor, List<TypeParameter> classTypeParameters) {
        this.accumulationTypeConstructor = accumulationTypeConstructor;
        this.permissiveAccumulationTypeConstructor = permissiveAccumulationTypeConstructor;
        this.inputTypeConstructor = inputTypeConstructor;
        this.classTypeParameters = classTypeParameters;
    }

    public static AccumulatorMethod of(TypeConstructor accumulationTypeConstructor, TypeConstructor permissiveAccumulationTypeConstructor, TypeConstructor inputTypeConstructor, List<TypeParameter> classTypeParameters) {
        return new AccumulatorMethod(accumulationTypeConstructor, permissiveAccumulationTypeConstructor, inputTypeConstructor, classTypeParameters);
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

    public List<TypeParameter> getClassTypeParameters() {
        return classTypeParameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccumulatorMethod that = (AccumulatorMethod) o;
        return getAccumulationTypeConstructor().equals(that.getAccumulationTypeConstructor()) && getPermissiveAccumulationTypeConstructor().equals(that.getPermissiveAccumulationTypeConstructor()) && getInputTypeConstructor().equals(that.getInputTypeConstructor()) && getClassTypeParameters().equals(that.getClassTypeParameters());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAccumulationTypeConstructor(), getPermissiveAccumulationTypeConstructor(), getInputTypeConstructor(), getClassTypeParameters());
    }

    @Override
    public String toString() {
        return "AccumulatorMethod{" +
                "accumulationTypeConstructor=" + accumulationTypeConstructor +
                ", permissiveAccumulationTypeConstructor=" + permissiveAccumulationTypeConstructor +
                ", inputTypeConstructor=" + inputTypeConstructor +
                ", classTypeParameters=" + classTypeParameters +
                '}';
    }
}
