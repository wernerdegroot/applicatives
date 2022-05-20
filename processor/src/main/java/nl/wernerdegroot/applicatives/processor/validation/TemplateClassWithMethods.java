package nl.wernerdegroot.applicatives.processor.validation;

import nl.wernerdegroot.applicatives.processor.domain.HasReplaceableTypeParameterNames;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameter;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class TemplateClassWithMethods implements HasReplaceableTypeParameterNames<TemplateClassWithMethods> {

    private final List<TypeParameter> classTypeParameters;
    private final Optional<String> optionalInitializerMethodName;
    private final Optional<TypeConstructor> optionalInitializedTypeConstructor;
    private final String accumulatorMethodName;
    private final TypeConstructor inputTypeConstructor;
    private final TypeConstructor partiallyAccumulatedTypeConstructor;
    private final TypeConstructor accumulatedTypeConstructor;
    private final Optional<String> optionalFinalizerMethodName;
    private final Optional<TypeConstructor> optionalToFinalizeTypeConstructor;
    private final Optional<TypeConstructor> optionalFinalizedTypeConstructor;

    public TemplateClassWithMethods(List<TypeParameter> classTypeParameters, Optional<String> optionalInitializerMethodName, Optional<TypeConstructor> optionalInitializedTypeConstructor, String accumulatorMethodName, TypeConstructor inputTypeConstructor, TypeConstructor partiallyAccumulatedTypeConstructor, TypeConstructor accumulatedTypeConstructor, Optional<String> optionalFinalizerMethodName, Optional<TypeConstructor> optionalToFinalizeTypeConstructor, Optional<TypeConstructor> optionalFinalizedTypeConstructor) {
        this.classTypeParameters = classTypeParameters;
        this.optionalInitializerMethodName = optionalInitializerMethodName;
        this.optionalInitializedTypeConstructor = optionalInitializedTypeConstructor;
        this.accumulatorMethodName = accumulatorMethodName;
        this.inputTypeConstructor = inputTypeConstructor;
        this.partiallyAccumulatedTypeConstructor = partiallyAccumulatedTypeConstructor;
        this.accumulatedTypeConstructor = accumulatedTypeConstructor;
        this.optionalFinalizerMethodName = optionalFinalizerMethodName;
        this.optionalToFinalizeTypeConstructor = optionalToFinalizeTypeConstructor;
        this.optionalFinalizedTypeConstructor = optionalFinalizedTypeConstructor;
    }

    public static TemplateClassWithMethods of(List<TypeParameter> classTypeParameters, Optional<String> optionalInitializerMethodName, Optional<TypeConstructor> optionalInitializedTypeConstructor, String accumulatorMethodName, TypeConstructor inputTypeConstructor, TypeConstructor partiallyAccumulatedTypeConstructor, TypeConstructor accumulatedTypeConstructor, Optional<String> optionalFinalizerMethodName, Optional<TypeConstructor> optionalToFinalizeTypeConstructor, Optional<TypeConstructor> optionalFinalizedTypeConstructor) {
        return new TemplateClassWithMethods(classTypeParameters, optionalInitializerMethodName, optionalInitializedTypeConstructor, accumulatorMethodName, inputTypeConstructor, partiallyAccumulatedTypeConstructor, accumulatedTypeConstructor, optionalFinalizerMethodName, optionalToFinalizeTypeConstructor, optionalFinalizedTypeConstructor);
    }

    @Override
    public TemplateClassWithMethods replaceTypeParameterNames(Map<TypeParameterName, TypeParameterName> replacements) {
        return TemplateClassWithMethods.of(
                classTypeParameters.stream().map(r -> r.replaceAllTypeParameterNames(replacements)).collect(toList()),
                optionalInitializerMethodName,
                optionalInitializedTypeConstructor.map(r -> r.replaceAllTypeParameterNames(replacements)),
                accumulatorMethodName,
                inputTypeConstructor.replaceAllTypeParameterNames(replacements),
                partiallyAccumulatedTypeConstructor.replaceAllTypeParameterNames(replacements),
                accumulatedTypeConstructor.replaceAllTypeParameterNames(replacements),
                optionalFinalizerMethodName,
                optionalToFinalizeTypeConstructor.map(r -> r.replaceAllTypeParameterNames(replacements)),
                optionalFinalizedTypeConstructor.map(r -> r.replaceAllTypeParameterNames(replacements))
        );
    }

    public List<TypeParameter> getClassTypeParameters() {
        return classTypeParameters;
    }

    public Optional<String> getOptionalInitializerMethodName() {
        return optionalInitializerMethodName;
    }

    public Optional<TypeConstructor> getOptionalInitializedTypeConstructor() {
        return optionalInitializedTypeConstructor;
    }

    public String getAccumulatorMethodName() {
        return accumulatorMethodName;
    }

    public TypeConstructor getInputTypeConstructor() {
        return inputTypeConstructor;
    }

    public TypeConstructor getPartiallyAccumulatedTypeConstructor() {
        return partiallyAccumulatedTypeConstructor;
    }

    public TypeConstructor getAccumulatedTypeConstructor() {
        return accumulatedTypeConstructor;
    }

    public Optional<String> getOptionalFinalizerMethodName() {
        return optionalFinalizerMethodName;
    }

    public Optional<TypeConstructor> getOptionalToFinalizeTypeConstructor() {
        return optionalToFinalizeTypeConstructor;
    }

    public Optional<TypeConstructor> getOptionalFinalizedTypeConstructor() {
        return optionalFinalizedTypeConstructor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TemplateClassWithMethods that = (TemplateClassWithMethods) o;
        return getClassTypeParameters().equals(that.getClassTypeParameters()) && getOptionalInitializerMethodName().equals(that.getOptionalInitializerMethodName()) && getOptionalInitializedTypeConstructor().equals(that.getOptionalInitializedTypeConstructor()) && getAccumulatorMethodName().equals(that.getAccumulatorMethodName()) && getInputTypeConstructor().equals(that.getInputTypeConstructor()) && getPartiallyAccumulatedTypeConstructor().equals(that.getPartiallyAccumulatedTypeConstructor()) && getAccumulatedTypeConstructor().equals(that.getAccumulatedTypeConstructor()) && getOptionalFinalizerMethodName().equals(that.getOptionalFinalizerMethodName()) && getOptionalToFinalizeTypeConstructor().equals(that.getOptionalToFinalizeTypeConstructor()) && getOptionalFinalizedTypeConstructor().equals(that.getOptionalFinalizedTypeConstructor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClassTypeParameters(), getOptionalInitializerMethodName(), getOptionalInitializedTypeConstructor(), getAccumulatorMethodName(), getInputTypeConstructor(), getPartiallyAccumulatedTypeConstructor(), getAccumulatedTypeConstructor(), getOptionalFinalizerMethodName(), getOptionalToFinalizeTypeConstructor(), getOptionalFinalizedTypeConstructor());
    }

    @Override
    public String toString() {
        return "TemplateClassWithMethods{" +
                "classTypeParameters=" + classTypeParameters +
                ", optionalInitializerMethodName=" + optionalInitializerMethodName +
                ", optionalInitializedTypeConstructor=" + optionalInitializedTypeConstructor +
                ", accumulatorMethodName='" + accumulatorMethodName + '\'' +
                ", inputTypeConstructor=" + inputTypeConstructor +
                ", partiallyAccumulatedTypeConstructor=" + partiallyAccumulatedTypeConstructor +
                ", accumulatedTypeConstructor=" + accumulatedTypeConstructor +
                ", optionalFinalizerMethodName=" + optionalFinalizerMethodName +
                ", optionalToFinalizeTypeConstructor=" + optionalToFinalizeTypeConstructor +
                ", optionalFinalizedTypeConstructor=" + optionalFinalizedTypeConstructor +
                '}';
    }
}
