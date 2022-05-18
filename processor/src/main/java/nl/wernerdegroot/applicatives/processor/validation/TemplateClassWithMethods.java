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
    private final Optional<TypeConstructor> optionalResultTypeConstructor;
    private final Optional<String> optionalInitializerMethodName;
    private final String accumulatorMethodName;
    private final Optional<String> optionalFinalizerMethodName;

    public TemplateClassWithMethods(List<TypeParameter> classTypeParameters, TypeConstructor accumulationTypeConstructor, TypeConstructor permissiveAccumulationTypeConstructor, TypeConstructor inputTypeConstructor, Optional<TypeConstructor> optionalResultTypeConstructor, Optional<String> optionalInitializerMethodName, String accumulatorMethodName, Optional<String> optionalFinalizerMethodName) {
        this.classTypeParameters = classTypeParameters;
        this.accumulationTypeConstructor = accumulationTypeConstructor;
        this.permissiveAccumulationTypeConstructor = permissiveAccumulationTypeConstructor;
        this.inputTypeConstructor = inputTypeConstructor;
        this.optionalResultTypeConstructor = optionalResultTypeConstructor;
        this.optionalInitializerMethodName = optionalInitializerMethodName;
        this.accumulatorMethodName = accumulatorMethodName;
        this.optionalFinalizerMethodName = optionalFinalizerMethodName;
    }

    public static TemplateClassWithMethods of(List<TypeParameter> classTypeParameters, TypeConstructor accumulationTypeConstructor, TypeConstructor permissiveAccumulationTypeConstructor, TypeConstructor inputTypeConstructor, Optional<TypeConstructor> optionalResultTypeConstructor, Optional<String> optionalInitializerMethodName, String accumulatorMethodName, Optional<String> optionalFinalizerMethodName) {
        return new TemplateClassWithMethods(classTypeParameters, accumulationTypeConstructor, permissiveAccumulationTypeConstructor, inputTypeConstructor, optionalResultTypeConstructor, optionalInitializerMethodName, accumulatorMethodName, optionalFinalizerMethodName);
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

    public Optional<TypeConstructor> getOptionalResultTypeConstructor() {
        return optionalResultTypeConstructor;
    }

    public Optional<String> getOptionalInitializerMethodName() {
        return optionalInitializerMethodName;
    }

    public String getAccumulatorMethodName() {
        return accumulatorMethodName;
    }

    public Optional<String> getOptionalFinalizerMethodName() {
        return optionalFinalizerMethodName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TemplateClassWithMethods that = (TemplateClassWithMethods) o;
        return getClassTypeParameters().equals(that.getClassTypeParameters()) && getAccumulationTypeConstructor().equals(that.getAccumulationTypeConstructor()) && getPermissiveAccumulationTypeConstructor().equals(that.getPermissiveAccumulationTypeConstructor()) && getInputTypeConstructor().equals(that.getInputTypeConstructor()) && getOptionalResultTypeConstructor().equals(that.getOptionalResultTypeConstructor()) && getOptionalInitializerMethodName().equals(that.getOptionalInitializerMethodName()) && getAccumulatorMethodName().equals(that.getAccumulatorMethodName()) && getOptionalFinalizerMethodName().equals(that.getOptionalFinalizerMethodName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClassTypeParameters(), getAccumulationTypeConstructor(), getPermissiveAccumulationTypeConstructor(), getInputTypeConstructor(), getOptionalResultTypeConstructor(), getOptionalInitializerMethodName(), getAccumulatorMethodName(), getOptionalFinalizerMethodName());
    }

    @Override
    public String toString() {
        return "TemplateClassWithMethods{" +
                "classTypeParameters=" + classTypeParameters +
                ", accumulationTypeConstructor=" + accumulationTypeConstructor +
                ", permissiveAccumulationTypeConstructor=" + permissiveAccumulationTypeConstructor +
                ", inputTypeConstructor=" + inputTypeConstructor +
                ", optionalResultTypeConstructor=" + optionalResultTypeConstructor +
                ", optionalInitializerMethodName=" + optionalInitializerMethodName +
                ", accumulatorMethodName='" + accumulatorMethodName + '\'' +
                ", optionalFinalizerMethodName=" + optionalFinalizerMethodName +
                '}';
    }
}
