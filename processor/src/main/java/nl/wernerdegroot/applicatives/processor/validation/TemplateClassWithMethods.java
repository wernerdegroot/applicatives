package nl.wernerdegroot.applicatives.processor.validation;

import nl.wernerdegroot.applicatives.processor.domain.TypeParameter;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TemplateClassWithMethods {

    private final List<TypeParameter> classTypeParameters;
    private final TypeConstructor accumulationTypeConstructor;
    private final TypeConstructor permissiveAccumulationTypeConstructor;
    private final TypeConstructor inputTypeConstructor;
    private final Optional<String> optionalInitializerMethodName;
    private final String accumulatorMethodName;

    public TemplateClassWithMethods(List<TypeParameter> classTypeParameters, TypeConstructor accumulationTypeConstructor, TypeConstructor permissiveAccumulationTypeConstructor, TypeConstructor inputTypeConstructor, Optional<String> optionalInitializerMethodName, String accumulatorMethodName) {
        this.classTypeParameters = classTypeParameters;
        this.accumulationTypeConstructor = accumulationTypeConstructor;
        this.permissiveAccumulationTypeConstructor = permissiveAccumulationTypeConstructor;
        this.inputTypeConstructor = inputTypeConstructor;
        this.optionalInitializerMethodName = optionalInitializerMethodName;
        this.accumulatorMethodName = accumulatorMethodName;
    }

    public static TemplateClassWithMethods of(List<TypeParameter> classTypeParameters, TypeConstructor accumulationTypeConstructor, TypeConstructor permissiveAccumulationTypeConstructor, TypeConstructor inputTypeConstructor, Optional<String> optionalInitializerMethodName, String accumulatorMethodName) {
        return new TemplateClassWithMethods(classTypeParameters, accumulationTypeConstructor, permissiveAccumulationTypeConstructor, inputTypeConstructor, optionalInitializerMethodName, accumulatorMethodName);
    }

    public List<TypeParameter> getClassTypeParameters() {
        return classTypeParameters;
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

    public Optional<String> getOptionalInitializerMethodName() {
        return optionalInitializerMethodName;
    }

    public String getAccumulatorMethodName() {
        return accumulatorMethodName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TemplateClassWithMethods that = (TemplateClassWithMethods) o;
        return getClassTypeParameters().equals(that.getClassTypeParameters()) && getAccumulationTypeConstructor().equals(that.getAccumulationTypeConstructor()) && getPermissiveAccumulationTypeConstructor().equals(that.getPermissiveAccumulationTypeConstructor()) && getInputTypeConstructor().equals(that.getInputTypeConstructor()) && getOptionalInitializerMethodName().equals(that.getOptionalInitializerMethodName()) && getAccumulatorMethodName().equals(that.getAccumulatorMethodName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClassTypeParameters(), getAccumulationTypeConstructor(), getPermissiveAccumulationTypeConstructor(), getInputTypeConstructor(), getOptionalInitializerMethodName(), getAccumulatorMethodName());
    }

    @Override
    public String toString() {
        return "TemplateClassWithMethods{" +
                "classTypeParameters=" + classTypeParameters +
                ", accumulationTypeConstructor=" + accumulationTypeConstructor +
                ", permissiveAccumulationTypeConstructor=" + permissiveAccumulationTypeConstructor +
                ", inputTypeConstructor=" + inputTypeConstructor +
                ", optionalInitializerMethodName=" + optionalInitializerMethodName +
                ", accumulatorMethodName='" + accumulatorMethodName + '\'' +
                '}';
    }
}
