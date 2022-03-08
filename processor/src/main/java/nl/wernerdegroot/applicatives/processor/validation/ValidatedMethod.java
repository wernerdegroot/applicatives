package nl.wernerdegroot.applicatives.processor.validation;

import nl.wernerdegroot.applicatives.processor.domain.Parameter;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameter;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

public interface ValidatedMethod {

    void match(Consumer<Valid> matchValid, Consumer<Invalid> matchInvalid);

    static Valid valid(List<TypeParameter> secondaryMethodTypeParameters, List<Parameter> secondaryParameters, TypeConstructor leftParameterTypeConstructor, TypeConstructor rightParameterTypeConstructor, TypeConstructor resultTypeConstructor, List<TypeParameter> classTypeParameters) {
        return Valid.of(secondaryMethodTypeParameters, secondaryParameters, leftParameterTypeConstructor, rightParameterTypeConstructor, resultTypeConstructor, classTypeParameters);
    }

    static Invalid invalid(Set<String> errorMessages) {
        return Invalid.of(errorMessages);
    }

    static Invalid invalid(String... errorMessages) {
        return invalid(Stream.of(errorMessages).collect(toSet()));
    }

    class Valid implements ValidatedMethod {

        private final List<TypeParameter> secondaryMethodTypeParameters;
        private final List<Parameter> secondaryParameters;
        private final TypeConstructor leftParameterTypeConstructor;
        private final TypeConstructor rightParameterTypeConstructor;
        private final TypeConstructor resultTypeConstructor;
        private final List<TypeParameter> classTypeParameters;

        public Valid(List<TypeParameter> secondaryMethodTypeParameters, List<Parameter> secondaryParameters, TypeConstructor leftParameterTypeConstructor, TypeConstructor rightParameterTypeConstructor, TypeConstructor resultTypeConstructor, List<TypeParameter> classTypeParameters) {
            this.secondaryMethodTypeParameters = secondaryMethodTypeParameters;
            this.secondaryParameters = secondaryParameters;
            this.leftParameterTypeConstructor = leftParameterTypeConstructor;
            this.rightParameterTypeConstructor = rightParameterTypeConstructor;
            this.resultTypeConstructor = resultTypeConstructor;
            this.classTypeParameters = classTypeParameters;
        }

        public static Valid of(List<TypeParameter> secondaryMethodTypeParameters, List<Parameter> secondaryParameters, TypeConstructor leftParameterTypeConstructor, TypeConstructor rightParameterTypeConstructor, TypeConstructor resultTypeConstructor, List<TypeParameter> classTypeParameters) {
            return new Valid(secondaryMethodTypeParameters, secondaryParameters, leftParameterTypeConstructor, rightParameterTypeConstructor, resultTypeConstructor, classTypeParameters);
        }

        @Override
        public void match(Consumer<Valid> matchValid, Consumer<Invalid> matchInvalid) {
            matchValid.accept(this);
        }

        public List<TypeParameter> getSecondaryMethodTypeParameters() {
            return secondaryMethodTypeParameters;
        }

        public List<Parameter> getSecondaryParameters() {
            return secondaryParameters;
        }

        public TypeConstructor getLeftParameterTypeConstructor() {
            return leftParameterTypeConstructor;
        }

        public TypeConstructor getRightParameterTypeConstructor() {
            return rightParameterTypeConstructor;
        }

        public TypeConstructor getResultTypeConstructor() {
            return resultTypeConstructor;
        }

        public List<TypeParameter> getClassTypeParameters() {
            return classTypeParameters;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Valid valid = (Valid) o;
            return getSecondaryMethodTypeParameters().equals(valid.getSecondaryMethodTypeParameters()) && getSecondaryParameters().equals(valid.getSecondaryParameters()) && getLeftParameterTypeConstructor().equals(valid.getLeftParameterTypeConstructor()) && getRightParameterTypeConstructor().equals(valid.getRightParameterTypeConstructor()) && getResultTypeConstructor().equals(valid.getResultTypeConstructor()) && getClassTypeParameters().equals(valid.getClassTypeParameters());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getSecondaryMethodTypeParameters(), getSecondaryParameters(), getLeftParameterTypeConstructor(), getRightParameterTypeConstructor(), getResultTypeConstructor(), getClassTypeParameters());
        }

        @Override
        public String toString() {
            return "Valid{" +
                    "secondaryMethodTypeParameters=" + secondaryMethodTypeParameters +
                    ", secondaryParameters=" + secondaryParameters +
                    ", leftParameterTypeConstructor=" + leftParameterTypeConstructor +
                    ", rightParameterTypeConstructor=" + rightParameterTypeConstructor +
                    ", resultTypeConstructor=" + resultTypeConstructor +
                    ", classTypeParameters=" + classTypeParameters +
                    '}';
        }
    }

    class Invalid implements ValidatedMethod {

        private final Set<String> errorMessages;

        public Invalid(Set<String> errorMessages) {
            this.errorMessages = errorMessages;
        }

        public static Invalid of(Set<String> errorMessages) {
            return new Invalid(errorMessages);
        }

        @Override
        public void match(Consumer<Valid> matchValid, Consumer<Invalid> matchInvalid) {
            matchInvalid.accept(this);
        }

        public Set<String> getErrorMessages() {
            return errorMessages;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Invalid invalid = (Invalid) o;
            return getErrorMessages().equals(invalid.getErrorMessages());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getErrorMessages());
        }

        @Override
        public String toString() {
            return "Invalid{" +
                    "errorMessages=" + errorMessages +
                    '}';
        }
    }
}
