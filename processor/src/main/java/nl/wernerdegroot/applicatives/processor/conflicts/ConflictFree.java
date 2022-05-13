package nl.wernerdegroot.applicatives.processor.conflicts;

import nl.wernerdegroot.applicatives.processor.domain.TypeParameter;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;

import java.util.List;
import java.util.Objects;

/**
 * The product of the conflict resolution algorithm. The fields in
 * this class are guaranteed to be free of conflicts and can be used
 * directly and without worry in code generation.
 */
public class ConflictFree {

    // For an explanation of these fields, check `README.md` in the `domain` package.

    private final List<TypeParameter> parameterTypeConstructorArguments;
    private final TypeParameter returnTypeConstructorArguments;
    private final List<TypeParameter> classTypeParameters;
    private final List<String> inputParameterNames;
    private final String selfParameterName;
    private final String combinatorParameterName;
    private final String maxTupleSizeParameterName;
    private final TypeConstructor accumulationTypeConstructor;
    private final TypeConstructor permissiveAccumulationTypeConstructor;
    private final TypeConstructor inputTypeConstructor;

    public ConflictFree(List<TypeParameter> parameterTypeConstructorArguments, TypeParameter returnTypeConstructorArguments, List<TypeParameter> classTypeParameters, List<String> inputParameterNames, String selfParameterName, String combinatorParameterName, String maxTupleSizeParameterName, TypeConstructor accumulationTypeConstructor, TypeConstructor permissiveAccumulationTypeConstructor, TypeConstructor inputTypeConstructor) {
        this.parameterTypeConstructorArguments = parameterTypeConstructorArguments;
        this.returnTypeConstructorArguments = returnTypeConstructorArguments;
        this.classTypeParameters = classTypeParameters;
        this.inputParameterNames = inputParameterNames;
        this.selfParameterName = selfParameterName;
        this.combinatorParameterName = combinatorParameterName;
        this.maxTupleSizeParameterName = maxTupleSizeParameterName;
        this.accumulationTypeConstructor = accumulationTypeConstructor;
        this.permissiveAccumulationTypeConstructor = permissiveAccumulationTypeConstructor;
        this.inputTypeConstructor = inputTypeConstructor;
    }

    public static ConflictFree of(List<TypeParameter> parameterTypeConstructorArguments, TypeParameter returnTypeConstructorArgument, List<TypeParameter> classTypeParameters, List<String> inputParameterNames, String selfParameterName, String combinatorParameterName, String maxTupleSizeParameterName, TypeConstructor accumulationTypeConstructor, TypeConstructor permissiveAccumulationTypeConstructor, TypeConstructor inputTypeConstructor) {
        return new ConflictFree(parameterTypeConstructorArguments, returnTypeConstructorArgument, classTypeParameters, inputParameterNames, selfParameterName, combinatorParameterName, maxTupleSizeParameterName, accumulationTypeConstructor, permissiveAccumulationTypeConstructor, inputTypeConstructor);
    }

    public List<TypeParameter> getParameterTypeConstructorArguments() {
        return parameterTypeConstructorArguments;
    }

    public TypeParameter getReturnTypeConstructorArguments() {
        return returnTypeConstructorArguments;
    }

    public List<TypeParameter> getClassTypeParameters() {
        return classTypeParameters;
    }

    public List<String> getInputParameterNames() {
        return inputParameterNames;
    }

    public String getSelfParameterName() {
        return selfParameterName;
    }

    public String getCombinatorParameterName() {
        return combinatorParameterName;
    }

    public String getMaxTupleSizeParameterName() {
        return maxTupleSizeParameterName;
    }

    public TypeConstructor getPermissiveAccumulationTypeConstructor() {
        return permissiveAccumulationTypeConstructor;
    }

    public TypeConstructor getInputTypeConstructor() {
        return inputTypeConstructor;
    }

    public TypeConstructor getAccumulationTypeConstructor() {
        return accumulationTypeConstructor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConflictFree that = (ConflictFree) o;
        return getParameterTypeConstructorArguments().equals(that.getParameterTypeConstructorArguments()) && getReturnTypeConstructorArguments().equals(that.getReturnTypeConstructorArguments()) && getClassTypeParameters().equals(that.getClassTypeParameters()) && getInputParameterNames().equals(that.getInputParameterNames()) && getSelfParameterName().equals(that.getSelfParameterName()) && getCombinatorParameterName().equals(that.getCombinatorParameterName()) && getMaxTupleSizeParameterName().equals(that.getMaxTupleSizeParameterName()) && getAccumulationTypeConstructor().equals(that.getAccumulationTypeConstructor()) && getPermissiveAccumulationTypeConstructor().equals(that.getPermissiveAccumulationTypeConstructor()) && getInputTypeConstructor().equals(that.getInputTypeConstructor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getParameterTypeConstructorArguments(), getReturnTypeConstructorArguments(), getClassTypeParameters(), getInputParameterNames(), getSelfParameterName(), getCombinatorParameterName(), getMaxTupleSizeParameterName(), getAccumulationTypeConstructor(), getPermissiveAccumulationTypeConstructor(), getInputTypeConstructor());
    }

    @Override
    public String toString() {
        return "ConflictFree{" +
                "parameterTypeConstructorArguments=" + parameterTypeConstructorArguments +
                ", returnTypeConstructorArguments=" + returnTypeConstructorArguments +
                ", classTypeParameters=" + classTypeParameters +
                ", inputParameterNames=" + inputParameterNames +
                ", selfParameterName='" + selfParameterName + '\'' +
                ", combinatorParameterName='" + combinatorParameterName + '\'' +
                ", maxTupleSizeParameterName='" + maxTupleSizeParameterName + '\'' +
                ", accumulationTypeConstructor=" + accumulationTypeConstructor +
                ", permissiveAccumulationTypeConstructor=" + permissiveAccumulationTypeConstructor +
                ", inputTypeConstructor=" + inputTypeConstructor +
                '}';
    }
}
