package nl.wernerdegroot.applicatives.processor.validation;

import nl.wernerdegroot.applicatives.processor.domain.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class TemplateClassWithMethods implements HasReplaceableTypeParameterNames<TemplateClassWithMethods> {

    private final List<TypeParameter> classTypeParameters;
    private final Optional<CovariantInitializer> optionalInitializer;
    private final CovariantAccumulator accumulator;
    private final Optional<CovariantFinalizer> optionalFinalizer;

    public TemplateClassWithMethods(List<TypeParameter> classTypeParameters, Optional<CovariantInitializer> optionalInitializer, CovariantAccumulator accumulator, Optional<CovariantFinalizer> optionalFinalizer) {
        this.classTypeParameters = classTypeParameters;
        this.optionalInitializer = optionalInitializer;
        this.accumulator = accumulator;
        this.optionalFinalizer = optionalFinalizer;
    }

    public static TemplateClassWithMethods of(List<TypeParameter> classTypeParameters, Optional<CovariantInitializer> optionalInitializer, CovariantAccumulator accumulator, Optional<CovariantFinalizer> optionalFinalizer) {
        return new TemplateClassWithMethods(classTypeParameters, optionalInitializer, accumulator, optionalFinalizer);
    }

    @Override
    public TemplateClassWithMethods replaceTypeParameterNames(Map<TypeParameterName, TypeParameterName> replacements) {
        return TemplateClassWithMethods.of(
                classTypeParameters.stream().map(r -> r.replaceAllTypeParameterNames(replacements)).collect(toList()),
                optionalInitializer.map(initializer -> initializer.replaceTypeParameterNames(replacements)),
                accumulator.replaceTypeParameterNames(replacements),
                optionalFinalizer.map(finalizer -> finalizer.replaceTypeParameterNames(replacements))
        );
    }

    public List<TypeParameter> getClassTypeParameters() {
        return classTypeParameters;
    }

    public Optional<CovariantInitializer> getOptionalInitializer() {
        return optionalInitializer;
    }

    public CovariantAccumulator getAccumulator() {
        return accumulator;
    }

    public Optional<CovariantFinalizer> getOptionalFinalizer() {
        return optionalFinalizer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TemplateClassWithMethods that = (TemplateClassWithMethods) o;
        return getClassTypeParameters().equals(that.getClassTypeParameters()) && getOptionalInitializer().equals(that.getOptionalInitializer()) && getAccumulator().equals(that.getAccumulator()) && getOptionalFinalizer().equals(that.getOptionalFinalizer());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClassTypeParameters(), getOptionalInitializer(), getAccumulator(), getOptionalFinalizer());
    }

    @Override
    public String toString() {
        return "TemplateClassWithMethods{" +
                "classTypeParameters=" + classTypeParameters +
                ", optionalInitializer=" + optionalInitializer +
                ", accumulator=" + accumulator +
                ", optionalFinalizer=" + optionalFinalizer +
                '}';
    }
}
