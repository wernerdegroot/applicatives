package nl.wernerdegroot.applicatives.processor.conflicts;

import nl.wernerdegroot.applicatives.processor.domain.Parameter;
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

    private final List<TypeParameter> primaryMethodTypeParameters;
    private final TypeParameter resultTypeParameter;
    private final List<TypeParameter> secondaryMethodTypeParameters;
    private final List<TypeParameter> classTypeParameters;
    private final List<String> primaryParameterNames;
    private final List<Parameter> secondaryParameters;
    private final String selfParameterName;
    private final String combinatorParameterName;
    private final String maxTupleSizeParameterName;
    private final TypeConstructor parameterTypeConstructor;
    private final TypeConstructor resultTypeConstructor;

    public ConflictFree(List<TypeParameter> primaryMethodTypeParameters, TypeParameter resultTypeParameter, List<TypeParameter> secondaryMethodTypeParameters, List<TypeParameter> classTypeParameters, List<String> primaryParameterNames, List<Parameter> secondaryParameters, String selfParameterName, String combinatorParameterName, String maxTupleSizeParameterName, TypeConstructor parameterTypeConstructor, TypeConstructor resultTypeConstructor) {
        this.primaryMethodTypeParameters = primaryMethodTypeParameters;
        this.resultTypeParameter = resultTypeParameter;
        this.secondaryMethodTypeParameters = secondaryMethodTypeParameters;
        this.classTypeParameters = classTypeParameters;
        this.primaryParameterNames = primaryParameterNames;
        this.secondaryParameters = secondaryParameters;
        this.selfParameterName = selfParameterName;
        this.combinatorParameterName = combinatorParameterName;
        this.maxTupleSizeParameterName = maxTupleSizeParameterName;
        this.parameterTypeConstructor = parameterTypeConstructor;
        this.resultTypeConstructor = resultTypeConstructor;
    }

    public static ConflictFree of(List<TypeParameter> primaryMethodTypeParameters, TypeParameter resultTypeParameter, List<TypeParameter> secondaryMethodTypeParameters, List<TypeParameter> classTypeParameters, List<String> primaryParameterNames, List<Parameter> secondaryParameters, String selfParameterName, String combinatorParameterName, String maxTupleSizeParameterName, TypeConstructor parameterTypeConstructor, TypeConstructor resultTypeConstructor) {
        return new ConflictFree(primaryMethodTypeParameters, resultTypeParameter, secondaryMethodTypeParameters, classTypeParameters, primaryParameterNames, secondaryParameters, selfParameterName, combinatorParameterName, maxTupleSizeParameterName, parameterTypeConstructor, resultTypeConstructor);
    }

    public List<TypeParameter> getPrimaryMethodTypeParameters() {
        return primaryMethodTypeParameters;
    }

    public TypeParameter getResultTypeParameter() {
        return resultTypeParameter;
    }

    public List<TypeParameter> getSecondaryMethodTypeParameters() {
        return secondaryMethodTypeParameters;
    }

    public List<TypeParameter> getClassTypeParameters() {
        return classTypeParameters;
    }

    public List<String> getPrimaryParameterNames() {
        return primaryParameterNames;
    }

    public List<Parameter> getSecondaryParameters() {
        return secondaryParameters;
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

    public TypeConstructor getParameterTypeConstructor() {
        return parameterTypeConstructor;
    }

    public TypeConstructor getResultTypeConstructor() {
        return resultTypeConstructor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConflictFree conflictFree = (ConflictFree) o;
        return getPrimaryMethodTypeParameters().equals(conflictFree.getPrimaryMethodTypeParameters()) && getResultTypeParameter().equals(conflictFree.getResultTypeParameter()) && getSecondaryMethodTypeParameters().equals(conflictFree.getSecondaryMethodTypeParameters()) && getClassTypeParameters().equals(conflictFree.getClassTypeParameters()) && getPrimaryParameterNames().equals(conflictFree.getPrimaryParameterNames()) && getSecondaryParameters().equals(conflictFree.getSecondaryParameters()) && getSelfParameterName().equals(conflictFree.getSelfParameterName()) && getCombinatorParameterName().equals(conflictFree.getCombinatorParameterName()) && getMaxTupleSizeParameterName().equals(conflictFree.getMaxTupleSizeParameterName()) && getParameterTypeConstructor().equals(conflictFree.getParameterTypeConstructor()) && getResultTypeConstructor().equals(conflictFree.getResultTypeConstructor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPrimaryMethodTypeParameters(), getResultTypeParameter(), getSecondaryMethodTypeParameters(), getClassTypeParameters(), getPrimaryParameterNames(), getSecondaryParameters(), getSelfParameterName(), getCombinatorParameterName(), getMaxTupleSizeParameterName(), getParameterTypeConstructor(), getResultTypeConstructor());
    }

    @Override
    public String toString() {
        return "ConflictFree{" +
                "primaryMethodTypeParameters=" + primaryMethodTypeParameters +
                ", resultTypeParameter=" + resultTypeParameter +
                ", secondaryMethodTypeParameters=" + secondaryMethodTypeParameters +
                ", classTypeParameters=" + classTypeParameters +
                ", primaryParameterNames=" + primaryParameterNames +
                ", secondaryParameters=" + secondaryParameters +
                ", selfParameterName='" + selfParameterName + '\'' +
                ", combinatorParameterName='" + combinatorParameterName + '\'' +
                ", maxTupleSizeParameterName='" + maxTupleSizeParameterName + '\'' +
                ", parameterTypeConstructor=" + parameterTypeConstructor +
                ", resultTypeConstructor=" + resultTypeConstructor +
                '}';
    }
}
