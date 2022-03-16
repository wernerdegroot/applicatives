package nl.wernerdegroot.applicatives.processor.validation;

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

    static Valid valid(TypeConstructor leftParameterTypeConstructor, TypeConstructor rightParameterTypeConstructor, TypeConstructor resultTypeConstructor, List<TypeParameter> classTypeParameters) {
        return Valid.of(resultTypeConstructor, leftParameterTypeConstructor, rightParameterTypeConstructor, classTypeParameters);
    }

    static Invalid invalid(Set<String> errorMessages) {
        return Invalid.of(errorMessages);
    }

    static Invalid invalid(String... errorMessages) {
        return invalid(Stream.of(errorMessages).collect(toSet()));
    }

    class Valid implements ValidatedMethod {

        private final TypeConstructor accumulationTypeConstructor;
        private final TypeConstructor permissiveAccumulationTypeConstructor;
        private final TypeConstructor inputTypeConstructor;
        private final List<TypeParameter> classTypeParameters;

        public Valid(TypeConstructor accumulationTypeConstructor, TypeConstructor permissiveAccumulationTypeConstructor, TypeConstructor inputTypeConstructor, List<TypeParameter> classTypeParameters) {
            this.accumulationTypeConstructor = accumulationTypeConstructor;
            this.permissiveAccumulationTypeConstructor = permissiveAccumulationTypeConstructor;
            this.inputTypeConstructor = inputTypeConstructor;
            this.classTypeParameters = classTypeParameters;
        }

        public static Valid of(TypeConstructor accumulationTypeConstructor, TypeConstructor permissiveAccumulationTypeConstructor, TypeConstructor inputTypeConstructor, List<TypeParameter> classTypeParameters) {
            return new Valid(accumulationTypeConstructor, permissiveAccumulationTypeConstructor, inputTypeConstructor, classTypeParameters);
        }

        @Override
        public void match(Consumer<Valid> matchValid, Consumer<Invalid> matchInvalid) {
            matchValid.accept(this);
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
            Valid valid = (Valid) o;
            return getAccumulationTypeConstructor().equals(valid.getAccumulationTypeConstructor()) && getPermissiveAccumulationTypeConstructor().equals(valid.getPermissiveAccumulationTypeConstructor()) && getInputTypeConstructor().equals(valid.getInputTypeConstructor()) && getClassTypeParameters().equals(valid.getClassTypeParameters());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getAccumulationTypeConstructor(), getPermissiveAccumulationTypeConstructor(), getInputTypeConstructor(), getClassTypeParameters());
        }

        @Override
        public String toString() {
            return "Valid{" +
                    "accumulationTypeConstructor=" + accumulationTypeConstructor +
                    ", permissiveAccumulationTypeConstructor=" + permissiveAccumulationTypeConstructor +
                    ", inputTypeConstructor=" + inputTypeConstructor +
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
