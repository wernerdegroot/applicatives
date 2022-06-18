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

public class CovariantParametersAndTypeParametersValidatorTest {

    private final TypeParameterName T = TypeParameterName.of("T");
    private final TypeParameterName U = TypeParameterName.of("U");
    private final TypeParameterName V = TypeParameterName.of("V");
    private final TypeParameterName P = TypeParameterName.of("P");
    private final TypeParameterName W = TypeParameterName.of("W");

    @Test
    public void givenValidParametersAndTypeParameters() {
        Set<String> errorMessages = new HashSet<>();

        List<Parameter> parameters = asList(
                Parameter.of(OPTIONAL.with(T), "left"),
                Parameter.of(OPTIONAL.with(U), "right"),
                Parameter.of(BI_FUNCTION.with(T.asType().contravariant(), U.asType().contravariant(), V.asType().covariant()), "compose")
        );

        List<TypeParameter> typeParameters = asList(T.asTypeParameter(), U.asTypeParameter(), V.asTypeParameter());

        ContravariantParametersAndTypeParametersValidator.Result result = new CovariantParametersAndTypeParametersValidator().validateTypeParametersAndParameters(
                typeParameters,
                parameters,
                errorMessages
        );

        ParametersAndTypeParametersValidator.Result expectedResult = ParametersAndTypeParametersValidator.Result.of(
                T.asTypeParameter(),
                U.asTypeParameter(),
                V.asTypeParameter(),
                OPTIONAL.with(T),
                OPTIONAL.with(U)
        );

        assertEquals(expectedResult, result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void validateGivenLessThanThreeTypeParameters() {
        Set<String> errorMessages = new HashSet<>();

        List<Parameter> parameters = asList(
                Parameter.of(OPTIONAL.with(T), "left"),
                Parameter.of(OPTIONAL.with(T), "right"),
                Parameter.of(BI_FUNCTION.with(T, T, T), "compose")
        );

        List<TypeParameter> typeParameters = asList(T.asTypeParameter());

        CovariantParametersAndTypeParametersValidator.Result result = new CovariantParametersAndTypeParametersValidator().validateTypeParametersAndParameters(
                typeParameters,
                parameters,
                errorMessages
        );

        Set<String> expectedErrorMessages = Stream.of("Method requires exactly 3 type parameters, but found 1").collect(toSet());

        assertNull(result);
        assertEquals(expectedErrorMessages, errorMessages);
    }

    @Test
    public void validateGivenMoreThanThreeTypeParameters() {
        Set<String> errorMessages = new HashSet<>();

        List<Parameter> parameters = asList(
                Parameter.of(FUNCTION.with(P, T), "left"),
                Parameter.of(FUNCTION.with(P, U), "right"),
                Parameter.of(BI_FUNCTION.with(T.asType().contravariant(), U.asType().contravariant(), V.asType().covariant()), "compose")
        );

        List<TypeParameter> typeParameters = asList(T.asTypeParameter(), U.asTypeParameter(), V.asTypeParameter(), P.asTypeParameter());

        CovariantParametersAndTypeParametersValidator.Result result = new CovariantParametersAndTypeParametersValidator().validateTypeParametersAndParameters(
                typeParameters,
                parameters,
                errorMessages
        );

        Set<String> expectedErrorMessages = Stream.of("Method requires exactly 3 type parameters, but found 4").collect(toSet());

        assertNull(result);
        assertEquals(expectedErrorMessages, errorMessages);
    }

    @Test
    public void validateGivenLessThanThreeParameters() {
        Set<String> errorMessages = new HashSet<>();

        List<Parameter> parameters = asList(
                Parameter.of(OPTIONAL.with(T), "left"),
                Parameter.of(OPTIONAL.with(U), "right")
        );

        List<TypeParameter> typeParameters = asList(T.asTypeParameter(), U.asTypeParameter(), V.asTypeParameter());

        CovariantParametersAndTypeParametersValidator.Result result = new CovariantParametersAndTypeParametersValidator().validateTypeParametersAndParameters(
                typeParameters,
                parameters,
                errorMessages
        );

        Set<String> expectedErrorMessages = Stream.of("Method requires exactly 3 parameters, but found 2").collect(toSet());

        assertNull(result);
        assertEquals(expectedErrorMessages, errorMessages);
    }

    @Test
    public void validateGivenMoreThanThreeParameters() {
        Set<String> errorMessages = new HashSet<>();

        List<Parameter> parameters = asList(
                Parameter.of(COMPLETABLE_FUTURE.with(T), "left"),
                Parameter.of(COMPLETABLE_FUTURE.with(U), "right"),
                Parameter.of(BI_FUNCTION.with(T.asType().contravariant(), U.asType().contravariant(), V.asType().covariant()), "compose"),
                Parameter.of(EXECUTOR, "executor")
        );

        List<TypeParameter> typeParameters = asList(T.asTypeParameter(), U.asTypeParameter(), V.asTypeParameter());

        CovariantParametersAndTypeParametersValidator.Result result = new CovariantParametersAndTypeParametersValidator().validateTypeParametersAndParameters(
                typeParameters,
                parameters,
                errorMessages
        );

        Set<String> expectedErrorMessages = Stream.of("Method requires exactly 3 parameters, but found 4").collect(toSet());

        assertNull(result);
        assertEquals(expectedErrorMessages, errorMessages);
    }

    @Test
    public void validateGivenThirdParameterThatIsNotABiFunction() {
        Set<String> errorMessages = new HashSet<>();

        List<Parameter> parameters = asList(
                Parameter.of(OPTIONAL.with(T), "left"),
                Parameter.of(OPTIONAL.with(U), "right"),
                Parameter.of(OBJECT, "someObject")
        );

        List<TypeParameter> typeParameters = asList(T.asTypeParameter(), U.asTypeParameter(), V.asTypeParameter());

        CovariantParametersAndTypeParametersValidator.Result result = new CovariantParametersAndTypeParametersValidator().validateTypeParametersAndParameters(
                typeParameters,
                parameters,
                errorMessages
        );

        Set<String> expectedErrorMessages = Stream.of("Expected third argument to be a java.util.function.BiFunction<? super T, ? super U, ? extends V> but was java.lang.Object").collect(toSet());

        assertNull(result);
        assertEquals(expectedErrorMessages, errorMessages);
    }

    @Test
    public void validateGivenMethodWithThirdParameterThatIsBiFunctionWithWrongTypeArguments() {
        Set<String> errorMessages = new HashSet<>();

        List<Parameter> parameters = asList(
                Parameter.of(OPTIONAL.with(T), "left"),
                Parameter.of(OPTIONAL.with(U), "right"),
                Parameter.of(BI_FUNCTION.with(W, W, W), "compose")
        );

        List<TypeParameter> typeParameters = asList(T.asTypeParameter(), U.asTypeParameter(), V.asTypeParameter());

        CovariantParametersAndTypeParametersValidator.Result result = new CovariantParametersAndTypeParametersValidator().validateTypeParametersAndParameters(
                typeParameters,
                parameters,
                errorMessages
        );

        Set<String> expectedErrorMessages = Stream.of("Expected third argument to be a java.util.function.BiFunction<? super T, ? super U, ? extends V> but was java.util.function.BiFunction<W, W, W>").collect(toSet());

        assertNull(result);
        assertEquals(expectedErrorMessages, errorMessages);
    }
}
