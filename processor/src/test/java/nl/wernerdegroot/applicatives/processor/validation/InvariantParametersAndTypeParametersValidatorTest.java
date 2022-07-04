package nl.wernerdegroot.applicatives.processor.validation;

import nl.wernerdegroot.applicatives.processor.domain.Parameter;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameter;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.*;
import static org.junit.jupiter.api.Assertions.*;

public class InvariantParametersAndTypeParametersValidatorTest {

    private final TypeParameterName I = TypeParameterName.of("I");
    private final TypeParameterName T = TypeParameterName.of("T");
    private final TypeParameterName U = TypeParameterName.of("U");
    private final TypeParameterName V = TypeParameterName.of("V");
    private final TypeParameterName R = TypeParameterName.of("R");

    @Test
    public void givenValidParametersAndTypeParameters() {
        List<String> errorMessages = new ArrayList<>();

        List<Parameter> parameters = asList(
                Parameter.of(UNARY_OPERATOR.with(T), "left"),
                Parameter.of(UNARY_OPERATOR.with(U), "right"),
                Parameter.of(BI_FUNCTION.with(T.asType().contravariant(), U.asType().contravariant(), V.asType().covariant()), "combinator"),
                Parameter.of(FUNCTION.with(V.asType().contravariant(), I.asType().covariant()), "toIntermediate"),
                Parameter.of(FUNCTION.with(I.asType().contravariant(), T.asType().covariant()), "extractLeft"),
                Parameter.of(FUNCTION.with(I.asType().contravariant(), U.asType().covariant()), "extractRight")
        );

        List<TypeParameter> typeParameters = asList(T.asTypeParameter(), U.asTypeParameter(), I.asTypeParameter(), V.asTypeParameter());

        InvariantParametersAndTypeParametersValidator.Result result = new InvariantParametersAndTypeParametersValidator().validateTypeParametersAndParameters(
                typeParameters,
                parameters,
                errorMessages
        );

        ParametersAndTypeParametersValidator.Result expectedResult = ParametersAndTypeParametersValidator.Result.of(
                T.asTypeParameter(),
                U.asTypeParameter(),
                V.asTypeParameter(),
                UNARY_OPERATOR.with(T),
                UNARY_OPERATOR.with(U)
        );

        assertEquals(expectedResult, result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void validateGivenLessThanFourTypeParameters() {
        List<String> errorMessages = new ArrayList<>();

        List<Parameter> parameters = asList(
                Parameter.of(UNARY_OPERATOR.with(T), "left"),
                Parameter.of(UNARY_OPERATOR.with(U), "right"),
                Parameter.of(BI_FUNCTION.with(T.asType().contravariant(), U.asType().contravariant(), V.asType().covariant()), "combinator"),
                Parameter.of(FUNCTION.with(V.asType().contravariant(), I.asType().covariant()), "toIntermediate"),
                Parameter.of(FUNCTION.with(I.asType().contravariant(), T.asType().covariant()), "extractLeft"),
                Parameter.of(FUNCTION.with(I.asType().contravariant(), U.asType().covariant()), "extractRight")
        );

        List<TypeParameter> typeParameters = asList(T.asTypeParameter(), U.asTypeParameter(), V.asTypeParameter());

        InvariantParametersAndTypeParametersValidator.Result result = new InvariantParametersAndTypeParametersValidator().validateTypeParametersAndParameters(
                typeParameters,
                parameters,
                errorMessages
        );

        List<String> expectedErrorMessages = asList("Method requires exactly 4 type parameters, but found 3");

        assertNull(result);
        assertEquals(expectedErrorMessages, errorMessages);
    }

    @Test
    public void validateGivenMoreThanFourTypeParameters() {
        List<String> errorMessages = new ArrayList<>();

        List<Parameter> parameters = asList(
                Parameter.of(UNARY_OPERATOR.with(T), "left"),
                Parameter.of(UNARY_OPERATOR.with(U), "right"),
                Parameter.of(BI_FUNCTION.with(T.asType().contravariant(), U.asType().contravariant(), V.asType().covariant()), "combinator"),
                Parameter.of(FUNCTION.with(V.asType().contravariant(), I.asType().covariant()), "toIntermediate"),
                Parameter.of(FUNCTION.with(I.asType().contravariant(), T.asType().covariant()), "extractLeft"),
                Parameter.of(FUNCTION.with(I.asType().contravariant(), U.asType().covariant()), "extractRight")
        );

        List<TypeParameter> typeParameters = asList(T.asTypeParameter(), U.asTypeParameter(), I.asTypeParameter(), V.asTypeParameter(), R.asTypeParameter());

        InvariantParametersAndTypeParametersValidator.Result result = new InvariantParametersAndTypeParametersValidator().validateTypeParametersAndParameters(
                typeParameters,
                parameters,
                errorMessages
        );

        List<String> expectedErrorMessages = asList("Method requires exactly 4 type parameters, but found 5");

        assertNull(result);
        assertEquals(expectedErrorMessages, errorMessages);
    }

    @Test
    public void validateGivenLessThanSixParameters() {
        List<String> errorMessages = new ArrayList<>();

        List<Parameter> parameters = asList(
                Parameter.of(UNARY_OPERATOR.with(T), "left"),
                Parameter.of(UNARY_OPERATOR.with(U), "right"),
                Parameter.of(BI_FUNCTION.with(T.asType().contravariant(), U.asType().contravariant(), V.asType().covariant()), "combinator"),
                Parameter.of(FUNCTION.with(I.asType().contravariant(), T.asType().covariant()), "extractLeft"),
                Parameter.of(FUNCTION.with(I.asType().contravariant(), U.asType().covariant()), "extractRight")
        );

        List<TypeParameter> typeParameters = asList(T.asTypeParameter(), U.asTypeParameter(), I.asTypeParameter(), V.asTypeParameter());

        InvariantParametersAndTypeParametersValidator.Result result = new InvariantParametersAndTypeParametersValidator().validateTypeParametersAndParameters(
                typeParameters,
                parameters,
                errorMessages
        );

        List<String> expectedErrorMessages = asList("Method requires exactly 6 parameters, but found 5");

        assertNull(result);
        assertEquals(expectedErrorMessages, errorMessages);
    }

    @Test
    public void validateGivenMoreThanSixParameters() {
        List<String> errorMessages = new ArrayList<>();

        List<Parameter> parameters = asList(
                Parameter.of(UNARY_OPERATOR.with(T), "left"),
                Parameter.of(UNARY_OPERATOR.with(U), "right"),
                Parameter.of(BI_FUNCTION.with(T.asType().contravariant(), U.asType().contravariant(), V.asType().covariant()), "combinator"),
                Parameter.of(FUNCTION.with(V.asType().contravariant(), I.asType().covariant()), "toIntermediate"),
                Parameter.of(FUNCTION.with(I.asType().contravariant(), T.asType().covariant()), "extractLeft"),
                Parameter.of(FUNCTION.with(I.asType().contravariant(), U.asType().covariant()), "extractRight"),
                Parameter.of(STRING, "someString")
        );

        List<TypeParameter> typeParameters = asList(T.asTypeParameter(), U.asTypeParameter(), I.asTypeParameter(), V.asTypeParameter());

        InvariantParametersAndTypeParametersValidator.Result result = new InvariantParametersAndTypeParametersValidator().validateTypeParametersAndParameters(
                typeParameters,
                parameters,
                errorMessages
        );

        List<String> expectedErrorMessages = asList("Method requires exactly 6 parameters, but found 7");

        assertNull(result);
        assertEquals(expectedErrorMessages, errorMessages);
    }

    @Test
    public void validateGivenThirdParameterThatIsNotABiFunction() {
        List<String> errorMessages = new ArrayList<>();

        List<Parameter> parameters = asList(
                Parameter.of(UNARY_OPERATOR.with(T), "left"),
                Parameter.of(UNARY_OPERATOR.with(U), "right"),
                Parameter.of(OBJECT, "combinator"),
                Parameter.of(FUNCTION.with(V.asType().contravariant(), I.asType().covariant()), "toIntermediate"),
                Parameter.of(FUNCTION.with(I.asType().contravariant(), T.asType().covariant()), "extractLeft"),
                Parameter.of(FUNCTION.with(I.asType().contravariant(), U.asType().covariant()), "extractRight")
        );

        List<TypeParameter> typeParameters = asList(T.asTypeParameter(), U.asTypeParameter(), I.asTypeParameter(), V.asTypeParameter());

        InvariantParametersAndTypeParametersValidator.Result result = new InvariantParametersAndTypeParametersValidator().validateTypeParametersAndParameters(
                typeParameters,
                parameters,
                errorMessages
        );

        List<String> expectedErrorMessages = asList("Expected third argument to be a java.util.function.BiFunction<? super T, ? super U, ? extends V> but was java.lang.Object");

        assertNull(result);
        assertEquals(expectedErrorMessages, errorMessages);
    }

    @Test
    public void validateGivenFourthParameterThatIsNotAFunction() {
        List<String> errorMessages = new ArrayList<>();

        List<Parameter> parameters = asList(
                Parameter.of(UNARY_OPERATOR.with(T), "left"),
                Parameter.of(UNARY_OPERATOR.with(U), "right"),
                Parameter.of(BI_FUNCTION.with(T.asType().contravariant(), U.asType().contravariant(), V.asType().covariant()), "combinator"),
                Parameter.of(OBJECT, "toIntermediate"),
                Parameter.of(FUNCTION.with(I.asType().contravariant(), T.asType().covariant()), "extractLeft"),
                Parameter.of(FUNCTION.with(I.asType().contravariant(), U.asType().covariant()), "extractRight")
        );

        List<TypeParameter> typeParameters = asList(T.asTypeParameter(), U.asTypeParameter(), I.asTypeParameter(), V.asTypeParameter());

        InvariantParametersAndTypeParametersValidator.Result result = new InvariantParametersAndTypeParametersValidator().validateTypeParametersAndParameters(
                typeParameters,
                parameters,
                errorMessages
        );

        List<String> expectedErrorMessages = asList("Expected fourth argument to be a java.util.function.Function<? super V, ? extends I> but was java.lang.Object");

        assertNull(result);
        assertEquals(expectedErrorMessages, errorMessages);
    }

    @Test
    public void validateGivenFifthParameterThatIsNotAFunction() {
        List<String> errorMessages = new ArrayList<>();

        List<Parameter> parameters = asList(
                Parameter.of(UNARY_OPERATOR.with(T), "left"),
                Parameter.of(UNARY_OPERATOR.with(U), "right"),
                Parameter.of(BI_FUNCTION.with(T.asType().contravariant(), U.asType().contravariant(), V.asType().covariant()), "combinator"),
                Parameter.of(FUNCTION.with(V.asType().contravariant(), I.asType().covariant()), "toIntermediate"),
                Parameter.of(OBJECT, "extractLeft"),
                Parameter.of(FUNCTION.with(I.asType().contravariant(), U.asType().covariant()), "extractRight")
        );

        List<TypeParameter> typeParameters = asList(T.asTypeParameter(), U.asTypeParameter(), I.asTypeParameter(), V.asTypeParameter());

        InvariantParametersAndTypeParametersValidator.Result result = new InvariantParametersAndTypeParametersValidator().validateTypeParametersAndParameters(
                typeParameters,
                parameters,
                errorMessages
        );

        List<String> expectedErrorMessages = asList("Expected fifth argument to be a java.util.function.Function<? super I, ? extends T> but was java.lang.Object");

        assertNull(result);
        assertEquals(expectedErrorMessages, errorMessages);
    }

    @Test
    public void validateGivenSixthParameterThatIsNotAFunction() {
        List<String> errorMessages = new ArrayList<>();

        List<Parameter> parameters = asList(
                Parameter.of(UNARY_OPERATOR.with(T), "left"),
                Parameter.of(UNARY_OPERATOR.with(U), "right"),
                Parameter.of(BI_FUNCTION.with(T.asType().contravariant(), U.asType().contravariant(), V.asType().covariant()), "combinator"),
                Parameter.of(FUNCTION.with(V.asType().contravariant(), I.asType().covariant()), "toIntermediate"),
                Parameter.of(FUNCTION.with(I.asType().contravariant(), T.asType().covariant()), "extractLeft"),
                Parameter.of(OBJECT, "extractRight")
        );

        List<TypeParameter> typeParameters = asList(T.asTypeParameter(), U.asTypeParameter(), I.asTypeParameter(), V.asTypeParameter());

        InvariantParametersAndTypeParametersValidator.Result result = new InvariantParametersAndTypeParametersValidator().validateTypeParametersAndParameters(
                typeParameters,
                parameters,
                errorMessages
        );

        List<String> expectedErrorMessages = asList("Expected sixth argument to be a java.util.function.Function<? super I, ? extends U> but was java.lang.Object");

        assertNull(result);
        assertEquals(expectedErrorMessages, errorMessages);
    }

    @Test
    public void validateGivenMethodWithThirdParameterThatIsBiFunctionWithWrongTypeArguments() {
        List<String> errorMessages = new ArrayList<>();

        List<Parameter> parameters = asList(
                Parameter.of(UNARY_OPERATOR.with(T), "left"),
                Parameter.of(UNARY_OPERATOR.with(U), "right"),
                Parameter.of(BI_FUNCTION.with(R.asType().contravariant(), R.asType().contravariant(), R.asType().covariant()), "combinator"),
                Parameter.of(FUNCTION.with(V.asType().contravariant(), I.asType().covariant()), "toIntermediate"),
                Parameter.of(FUNCTION.with(I.asType().contravariant(), T.asType().covariant()), "extractLeft"),
                Parameter.of(FUNCTION.with(I.asType().contravariant(), U.asType().covariant()), "extractRight")
        );

        List<TypeParameter> typeParameters = asList(T.asTypeParameter(), U.asTypeParameter(), I.asTypeParameter(), V.asTypeParameter());

        InvariantParametersAndTypeParametersValidator.Result result = new InvariantParametersAndTypeParametersValidator().validateTypeParametersAndParameters(
                typeParameters,
                parameters,
                errorMessages
        );

        List<String> expectedErrorMessages = asList("Expected third argument to be a java.util.function.BiFunction<? super T, ? super U, ? extends V> but was java.util.function.BiFunction<? super R, ? super R, ? extends R>");

        assertNull(result);
        assertEquals(expectedErrorMessages, errorMessages);
    }

    @Test
    public void validateGivenMethodWithFourthParameterThatIsFunctionWithWrongTypeArguments() {
        List<String> errorMessages = new ArrayList<>();

        List<Parameter> parameters = asList(
                Parameter.of(UNARY_OPERATOR.with(T), "left"),
                Parameter.of(UNARY_OPERATOR.with(U), "right"),
                Parameter.of(BI_FUNCTION.with(T.asType().contravariant(), U.asType().contravariant(), V.asType().covariant()), "combinator"),
                Parameter.of(FUNCTION.with(V.asType().contravariant(), V.asType().covariant()), "toIntermediate"),
                Parameter.of(FUNCTION.with(I.asType().contravariant(), T.asType().covariant()), "extractLeft"),
                Parameter.of(FUNCTION.with(I.asType().contravariant(), U.asType().covariant()), "extractRight")
        );

        List<TypeParameter> typeParameters = asList(T.asTypeParameter(), U.asTypeParameter(), I.asTypeParameter(), V.asTypeParameter());

        InvariantParametersAndTypeParametersValidator.Result result = new InvariantParametersAndTypeParametersValidator().validateTypeParametersAndParameters(
                typeParameters,
                parameters,
                errorMessages
        );

        List<String> expectedErrorMessages = asList("Expected fourth argument to be a java.util.function.Function<? super V, ? extends I> but was java.util.function.Function<? super V, ? extends V>");

        assertNull(result);
        assertEquals(expectedErrorMessages, errorMessages);
    }

    @Test
    public void validateGivenMethodWithFifthParameterThatIsFunctionWithWrongTypeArguments() {
        List<String> errorMessages = new ArrayList<>();

        List<Parameter> parameters = asList(
                Parameter.of(UNARY_OPERATOR.with(T), "left"),
                Parameter.of(UNARY_OPERATOR.with(U), "right"),
                Parameter.of(BI_FUNCTION.with(T.asType().contravariant(), U.asType().contravariant(), V.asType().covariant()), "combinator"),
                Parameter.of(FUNCTION.with(V.asType().contravariant(), I.asType().covariant()), "toIntermediate"),
                Parameter.of(FUNCTION.with(V.asType().contravariant(), T.asType().covariant()), "extractLeft"),
                Parameter.of(FUNCTION.with(I.asType().contravariant(), U.asType().covariant()), "extractRight")
        );

        List<TypeParameter> typeParameters = asList(T.asTypeParameter(), U.asTypeParameter(), I.asTypeParameter(), V.asTypeParameter());

        InvariantParametersAndTypeParametersValidator.Result result = new InvariantParametersAndTypeParametersValidator().validateTypeParametersAndParameters(
                typeParameters,
                parameters,
                errorMessages
        );

        List<String> expectedErrorMessages = asList("Expected fifth argument to be a java.util.function.Function<? super I, ? extends T> but was java.util.function.Function<? super V, ? extends T>");

        assertNull(result);
        assertEquals(expectedErrorMessages, errorMessages);
    }

    @Test
    public void validateGivenMethodWithSixthParameterThatIsFunctionWithWrongTypeArguments() {
        List<String> errorMessages = new ArrayList<>();

        List<Parameter> parameters = asList(
                Parameter.of(UNARY_OPERATOR.with(T), "left"),
                Parameter.of(UNARY_OPERATOR.with(U), "right"),
                Parameter.of(BI_FUNCTION.with(T.asType().contravariant(), U.asType().contravariant(), V.asType().covariant()), "combinator"),
                Parameter.of(FUNCTION.with(V.asType().contravariant(), I.asType().covariant()), "toIntermediate"),
                Parameter.of(FUNCTION.with(I.asType().contravariant(), T.asType().covariant()), "extractLeft"),
                Parameter.of(FUNCTION.with(V.asType().contravariant(), U.asType().covariant()), "extractRight")
        );

        List<TypeParameter> typeParameters = asList(T.asTypeParameter(), U.asTypeParameter(), I.asTypeParameter(), V.asTypeParameter());

        InvariantParametersAndTypeParametersValidator.Result result = new InvariantParametersAndTypeParametersValidator().validateTypeParametersAndParameters(
                typeParameters,
                parameters,
                errorMessages
        );

        List<String> expectedErrorMessages = asList("Expected sixth argument to be a java.util.function.Function<? super I, ? extends U> but was java.util.function.Function<? super V, ? extends U>");

        assertNull(result);
        assertEquals(expectedErrorMessages, errorMessages);
    }
}
