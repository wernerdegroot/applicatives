package nl.wernerdegroot.applicatives.processor.validation;

import nl.wernerdegroot.applicatives.processor.domain.Parameter;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameter;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.*;
import static org.junit.jupiter.api.Assertions.*;

public class ContravariantParametersAndTypeParametersValidatorTest {

    private final TypeParameterName I = TypeParameterName.of("I");
    private final TypeParameterName T = TypeParameterName.of("T");
    private final TypeParameterName U = TypeParameterName.of("U");
    private final TypeParameterName V = TypeParameterName.of("V");
    private final TypeParameterName R = TypeParameterName.of("R");

    @Test
    public void givenValidParametersAndTypeParameters() {
        Set<String> errorMessages = new HashSet<>();

        List<Parameter> parameters = asList(
                Parameter.of(COMPARATOR.with(T), "left"),
                Parameter.of(COMPARATOR.with(U), "right"),
                Parameter.of(FUNCTION.with(V.asType().contravariant(), I.asType().covariant()), "toIntermediate"),
                Parameter.of(FUNCTION.with(I.asType().contravariant(), T.asType().covariant()), "extractLeft"),
                Parameter.of(FUNCTION.with(I.asType().contravariant(), U.asType().covariant()), "extractRight")
        );

        List<TypeParameter> typeParameters = asList(T.asTypeParameter(), U.asTypeParameter(), I.asTypeParameter(), V.asTypeParameter());

        ContravariantParametersAndTypeParametersValidator.Result result = new ContravariantParametersAndTypeParametersValidator().validateTypeParametersAndParameters(
                typeParameters,
                parameters,
                errorMessages
        );

        ParametersAndTypeParametersValidator.Result expectedResult = ParametersAndTypeParametersValidator.Result.of(
                T.asTypeParameter(),
                U.asTypeParameter(),
                V.asTypeParameter(),
                COMPARATOR.with(T),
                COMPARATOR.with(U)
        );

        assertEquals(expectedResult, result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void validateGivenLessThanFourTypeParameters() {
        Set<String> errorMessages = new HashSet<>();

        List<Parameter> parameters = asList(
                Parameter.of(COMPARATOR.with(T), "left"),
                Parameter.of(COMPARATOR.with(U), "right"),
                Parameter.of(FUNCTION.with(V.asType().contravariant(), V.asType().covariant()), "toIntermediate"),
                Parameter.of(FUNCTION.with(V.asType().contravariant(), T.asType().covariant()), "extractLeft"),
                Parameter.of(FUNCTION.with(V.asType().contravariant(), U.asType().covariant()), "extractRight")
        );

        List<TypeParameter> typeParameters = asList(T.asTypeParameter(), U.asTypeParameter(), V.asTypeParameter());

        ContravariantParametersAndTypeParametersValidator.Result result = new ContravariantParametersAndTypeParametersValidator().validateTypeParametersAndParameters(
                typeParameters,
                parameters,
                errorMessages
        );

        Set<String> expectedErrorMessages = Stream.of("Method requires exactly 4 type parameters, but found 3").collect(toSet());

        assertNull(result);
        assertEquals(expectedErrorMessages, errorMessages);
    }

    @Test
    public void validateGivenMoreThanFourTypeParameters() {
        Set<String> errorMessages = new HashSet<>();

        List<Parameter> parameters = asList(
                Parameter.of(FUNCTION.with(T, R), "left"),
                Parameter.of(FUNCTION.with(U, R), "right"),
                Parameter.of(FUNCTION.with(V.asType().contravariant(), I.asType().covariant()), "toIntermediate"),
                Parameter.of(FUNCTION.with(I.asType().contravariant(), T.asType().covariant()), "extractLeft"),
                Parameter.of(FUNCTION.with(I.asType().contravariant(), U.asType().covariant()), "extractRight")
        );

        List<TypeParameter> typeParameters = asList(T.asTypeParameter(), U.asTypeParameter(), I.asTypeParameter(), V.asTypeParameter(), R.asTypeParameter());

        ContravariantParametersAndTypeParametersValidator.Result result = new ContravariantParametersAndTypeParametersValidator().validateTypeParametersAndParameters(
                typeParameters,
                parameters,
                errorMessages
        );

        Set<String> expectedErrorMessages = Stream.of("Method requires exactly 4 type parameters, but found 5").collect(toSet());

        assertNull(result);
        assertEquals(expectedErrorMessages, errorMessages);
    }

    @Test
    public void validateGivenLessThanFiveParameters() {
        Set<String> errorMessages = new HashSet<>();

        List<Parameter> parameters = asList(
                Parameter.of(COMPARATOR.with(T), "left"),
                Parameter.of(COMPARATOR.with(U), "right"),
                Parameter.of(FUNCTION.with(I.asType().contravariant(), T.asType().covariant()), "extractLeft"),
                Parameter.of(FUNCTION.with(I.asType().contravariant(), U.asType().covariant()), "extractRight")
        );

        List<TypeParameter> typeParameters = asList(T.asTypeParameter(), U.asTypeParameter(), I.asTypeParameter(), V.asTypeParameter());

        ContravariantParametersAndTypeParametersValidator.Result result = new ContravariantParametersAndTypeParametersValidator().validateTypeParametersAndParameters(
                typeParameters,
                parameters,
                errorMessages
        );

        Set<String> expectedErrorMessages = Stream.of("Method requires exactly 5 parameters, but found 4").collect(toSet());

        assertNull(result);
        assertEquals(expectedErrorMessages, errorMessages);
    }

    @Test
    public void validateGivenMoreThanFiveParameters() {
        Set<String> errorMessages = new HashSet<>();

        List<Parameter> parameters = asList(
                Parameter.of(FUNCTION.with(T, R), "left"),
                Parameter.of(FUNCTION.with(U, R), "right"),
                Parameter.of(FUNCTION.with(V, I), "toIntermediate"),
                Parameter.of(FUNCTION.with(I.asType().contravariant(), T.asType().covariant()), "extractLeft"),
                Parameter.of(FUNCTION.with(I.asType().contravariant(), U.asType().covariant()), "extractRight"),
                Parameter.of(BI_FUNCTION.with(R, R, R), "combineResults")
        );

        List<TypeParameter> typeParameters = asList(T.asTypeParameter(), U.asTypeParameter(), I.asTypeParameter(), V.asTypeParameter());

        ContravariantParametersAndTypeParametersValidator.Result result = new ContravariantParametersAndTypeParametersValidator().validateTypeParametersAndParameters(
                typeParameters,
                parameters,
                errorMessages
        );

        Set<String> expectedErrorMessages = Stream.of("Method requires exactly 5 parameters, but found 6").collect(toSet());

        assertNull(result);
        assertEquals(expectedErrorMessages, errorMessages);
    }

    @Test
    public void validateGivenThirdParameterThatIsNotAFunction() {
        Set<String> errorMessages = new HashSet<>();

        List<Parameter> parameters = asList(
                Parameter.of(COMPARATOR.with(T), "left"),
                Parameter.of(COMPARATOR.with(U), "right"),
                Parameter.of(OBJECT, "toIntermediate"),
                Parameter.of(FUNCTION.with(I.asType().contravariant(), T.asType().covariant()), "extractLeft"),
                Parameter.of(FUNCTION.with(I.asType().contravariant(), U.asType().covariant()), "extractRight")
        );

        List<TypeParameter> typeParameters = asList(T.asTypeParameter(), U.asTypeParameter(), I.asTypeParameter(), V.asTypeParameter());

        ContravariantParametersAndTypeParametersValidator.Result result = new ContravariantParametersAndTypeParametersValidator().validateTypeParametersAndParameters(
                typeParameters,
                parameters,
                errorMessages
        );

        Set<String> expectedErrorMessages = Stream.of("Expected third argument to be a java.util.function.Function<? super V, ? extends I> but was java.lang.Object").collect(toSet());

        assertNull(result);
        assertEquals(expectedErrorMessages, errorMessages);
    }

    @Test
    public void validateGivenFourthParameterThatIsNotAFunction() {
        Set<String> errorMessages = new HashSet<>();

        List<Parameter> parameters = asList(
                Parameter.of(COMPARATOR.with(T), "left"),
                Parameter.of(COMPARATOR.with(U), "right"),
                Parameter.of(FUNCTION.with(V.asType().contravariant(), I.asType().covariant()), "toIntermediate"),
                Parameter.of(OBJECT, "extractLeft"),
                Parameter.of(FUNCTION.with(I.asType().contravariant(), U.asType().covariant()), "extractRight")
        );

        List<TypeParameter> typeParameters = asList(T.asTypeParameter(), U.asTypeParameter(), I.asTypeParameter(), V.asTypeParameter());

        ContravariantParametersAndTypeParametersValidator.Result result = new ContravariantParametersAndTypeParametersValidator().validateTypeParametersAndParameters(
                typeParameters,
                parameters,
                errorMessages
        );

        Set<String> expectedErrorMessages = Stream.of("Expected fourth argument to be a java.util.function.Function<? super I, ? extends T> but was java.lang.Object").collect(toSet());

        assertNull(result);
        assertEquals(expectedErrorMessages, errorMessages);
    }

    @Test
    public void validateGivenFifthParameterThatIsNotAFunction() {
        Set<String> errorMessages = new HashSet<>();

        List<Parameter> parameters = asList(
                Parameter.of(COMPARATOR.with(T), "left"),
                Parameter.of(COMPARATOR.with(U), "right"),
                Parameter.of(FUNCTION.with(V.asType().contravariant(), I.asType().covariant()), "toIntermediate"),
                Parameter.of(FUNCTION.with(I.asType().contravariant(), T.asType().covariant()), "extractLeft"),
                Parameter.of(OBJECT, "extractRight")
        );

        List<TypeParameter> typeParameters = asList(T.asTypeParameter(), U.asTypeParameter(), I.asTypeParameter(), V.asTypeParameter());

        ContravariantParametersAndTypeParametersValidator.Result result = new ContravariantParametersAndTypeParametersValidator().validateTypeParametersAndParameters(
                typeParameters,
                parameters,
                errorMessages
        );

        Set<String> expectedErrorMessages = Stream.of("Expected fifth argument to be a java.util.function.Function<? super I, ? extends U> but was java.lang.Object").collect(toSet());

        assertNull(result);
        assertEquals(expectedErrorMessages, errorMessages);
    }

    @Test
    public void validateGivenMethodWithThirdParameterThatIsFunctionWithWrongTypeArguments() {
        Set<String> errorMessages = new HashSet<>();

        List<Parameter> parameters = asList(
                Parameter.of(COMPARATOR.with(T), "left"),
                Parameter.of(COMPARATOR.with(U), "right"),
                Parameter.of(FUNCTION.with(V.asType().contravariant(), V.asType().covariant()), "toIntermediate"),
                Parameter.of(FUNCTION.with(I.asType().contravariant(), T.asType().covariant()), "extractLeft"),
                Parameter.of(FUNCTION.with(I.asType().contravariant(), U.asType().covariant()), "extractRight")
        );

        List<TypeParameter> typeParameters = asList(T.asTypeParameter(), U.asTypeParameter(), I.asTypeParameter(), V.asTypeParameter());

        ContravariantParametersAndTypeParametersValidator.Result result = new ContravariantParametersAndTypeParametersValidator().validateTypeParametersAndParameters(
                typeParameters,
                parameters,
                errorMessages
        );

        Set<String> expectedErrorMessages = Stream.of("Expected third argument to be a java.util.function.Function<? super V, ? extends I> but was java.util.function.Function<? super V, ? extends V>").collect(toSet());

        assertNull(result);
        assertEquals(expectedErrorMessages, errorMessages);
    }

    @Test
    public void validateGivenMethodWithFourthParameterThatIsFunctionWithWrongTypeArguments() {
        Set<String> errorMessages = new HashSet<>();

        List<Parameter> parameters = asList(
                Parameter.of(COMPARATOR.with(T), "left"),
                Parameter.of(COMPARATOR.with(U), "right"),
                Parameter.of(FUNCTION.with(V.asType().contravariant(), I.asType().covariant()), "toIntermediate"),
                Parameter.of(FUNCTION.with(V.asType().contravariant(), T.asType().covariant()), "extractLeft"),
                Parameter.of(FUNCTION.with(I.asType().contravariant(), U.asType().covariant()), "extractRight")
        );

        List<TypeParameter> typeParameters = asList(T.asTypeParameter(), U.asTypeParameter(), I.asTypeParameter(), V.asTypeParameter());

        ContravariantParametersAndTypeParametersValidator.Result result = new ContravariantParametersAndTypeParametersValidator().validateTypeParametersAndParameters(
                typeParameters,
                parameters,
                errorMessages
        );

        Set<String> expectedErrorMessages = Stream.of("Expected fourth argument to be a java.util.function.Function<? super I, ? extends T> but was java.util.function.Function<? super V, ? extends T>").collect(toSet());

        assertNull(result);
        assertEquals(expectedErrorMessages, errorMessages);
    }

    @Test
    public void validateGivenMethodWithFifthParameterThatIsFunctionWithWrongTypeArguments() {
        Set<String> errorMessages = new HashSet<>();

        List<Parameter> parameters = asList(
                Parameter.of(COMPARATOR.with(T), "left"),
                Parameter.of(COMPARATOR.with(U), "right"),
                Parameter.of(FUNCTION.with(V.asType().contravariant(), I.asType().covariant()), "toIntermediate"),
                Parameter.of(FUNCTION.with(I.asType().contravariant(), T.asType().covariant()), "extractLeft"),
                Parameter.of(FUNCTION.with(V.asType().contravariant(), U.asType().covariant()), "extractRight")
        );

        List<TypeParameter> typeParameters = asList(T.asTypeParameter(), U.asTypeParameter(), I.asTypeParameter(), V.asTypeParameter());

        ContravariantParametersAndTypeParametersValidator.Result result = new ContravariantParametersAndTypeParametersValidator().validateTypeParametersAndParameters(
                typeParameters,
                parameters,
                errorMessages
        );

        Set<String> expectedErrorMessages = Stream.of("Expected fifth argument to be a java.util.function.Function<? super I, ? extends U> but was java.util.function.Function<? super V, ? extends U>").collect(toSet());

        assertNull(result);
        assertEquals(expectedErrorMessages, errorMessages);
    }
}
