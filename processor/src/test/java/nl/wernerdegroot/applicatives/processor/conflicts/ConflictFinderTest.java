package nl.wernerdegroot.applicatives.processor.conflicts;

import nl.wernerdegroot.applicatives.processor.domain.Parameter;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameter;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static nl.wernerdegroot.applicatives.processor.conflicts.ConflictFinder.findParameterNameReplacements;
import static nl.wernerdegroot.applicatives.processor.conflicts.ConflictFinder.findTypeParameterNameReplacements;
import static nl.wernerdegroot.applicatives.processor.conflicts.Conflicts.*;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.INTEGER;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.STRING;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConflictFinderTest {

    @Test
    public void findParameterNameReplacementsGivenNoConflicts() {
        List<Parameter> secondaryParameters = asList(
                Parameter.of(STRING, s),
                Parameter.of(INTEGER, i)
        );

        ParameterNameReplacements toVerify = findParameterNameReplacements(secondaryParameters);

        ParameterNameReplacements expected = ParameterNameReplacements.builder()
                .replaceSecondaryParameterName(s, s)
                .replaceSecondaryParameterName(i, i)
                .build();

        assertEquals(expected, toVerify);
    }

    @Test
    public void findParameterNameReplacementsGivenConflictWithNewPrimaryParameterNames() {
        PRIMARY_PARAMETER_NAMES.forEach(primaryParameterName -> {
            List<Parameter> secondaryParameters = asList(
                    Parameter.of(STRING, primaryParameterName),
                    Parameter.of(INTEGER, i)
            );

            ParameterNameReplacements toVerify = findParameterNameReplacements(secondaryParameters);

            ParameterNameReplacements expected = ParameterNameReplacements.builder()
                    .replaceSecondaryParameterName(primaryParameterName, s1)
                    .replaceSecondaryParameterName(i, s2)
                    .build();

            assertEquals(expected, toVerify);
        });
    }

    @Test
    public void findParameterNameReplacementsGivenConflictWithAnyOfTheOtherNewParameterNames() {
        Stream.of(SELF_PARAMETER_NAME, COMBINATOR_PARAMETER_NAME, MAX_TUPLE_SIZE_PARAMETER_NAME).forEach(otherParameterName -> {
            List<Parameter> secondaryParameters = asList(
                    Parameter.of(STRING, s),
                    Parameter.of(INTEGER, otherParameterName)
            );

            ParameterNameReplacements toVerify = findParameterNameReplacements(secondaryParameters);

            ParameterNameReplacements expected = ParameterNameReplacements.builder()
                    .replaceSecondaryParameterName(s, s1)
                    .replaceSecondaryParameterName(otherParameterName, s2)
                    .build();

            assertEquals(expected, toVerify);
        });
    }

    @Test
    public void findTypeParameterNameReplacementGivenNoConflicts() {
        List<TypeParameter> secondaryMethodTypeParameters = typeParameters(M);
        List<TypeParameter> classTypeParameters = typeParameters(C);

        TypeParameterNameReplacements toVerify = findTypeParameterNameReplacements(secondaryMethodTypeParameters, classTypeParameters);

        TypeParameterNameReplacements expected = TypeParameterNameReplacements.builder()
                .replaceSecondaryMethodTypeParameter(M, M)
                .replaceSecondaryMethodTypeParameter(C, C)
                .replaceClassTypeParameter(C, C)
                .build();

        assertEquals(toVerify, expected);
    }

    @Test
    public void findTypeParameterNameReplacementGivenNoConflictsButReplacementsForSecondaryMethodTypeParametersWouldConflictWithClassTypeParametersAndViceVersa() {
        List<TypeParameter> secondaryMethodTypeParameters = typeParameters(C1);
        List<TypeParameter> classTypeParameters = typeParameters(M1);

        TypeParameterNameReplacements toVerify = findTypeParameterNameReplacements(secondaryMethodTypeParameters, classTypeParameters);

        TypeParameterNameReplacements expected = TypeParameterNameReplacements.builder()
                .replaceSecondaryMethodTypeParameter(C1, C1)
                .replaceSecondaryMethodTypeParameter(M1, M1)
                .replaceClassTypeParameter(M1, M1)
                .build();

        assertEquals(toVerify, expected);
    }

    // Although it could be argued that we should leave pre-existing conflicts alone,
    // I think it might reduce confusion if we explicitly resolve those too. Choosing
    // to replace the secondary method type parameters over the class type parameters
    // is an arbitrary decision. Replacing the class type parameters instead would work
    // just as well.
    @Test
    public void findTypeParameterNameReplacementGivenConflictsBetweenSecondaryMethodTypeParametersAndClassTypeParameters() {
        List<TypeParameter> secondaryMethodTypeParameters = typeParameters(S);
        List<TypeParameter> classTypeParameters = typeParameters(S);

        TypeParameterNameReplacements toVerify = findTypeParameterNameReplacements(secondaryMethodTypeParameters, classTypeParameters);

        TypeParameterNameReplacements expected = TypeParameterNameReplacements.builder()
                .replaceSecondaryMethodTypeParameter(S, M1)
                .replaceClassTypeParameter(S, S)
                .build();

        assertEquals(toVerify, expected);
    }

    @Test
    public void findTypeParameterNameReplacementGivenConflictsBetweenSecondaryMethodTypeParametersAndNewPrimaryMethodTypeParametersOrNewResultTypeParameter() {
        PRIMARY_METHOD_TYPE_PARAMETER_NAMES_AND_RESULT_TYPE_PARAMETER_NAME.forEach(I -> {

            List<TypeParameter> secondaryMethodTypeParameters = typeParameters(I, M);
            List<TypeParameter> classTypeParameters = typeParameters(C);

            TypeParameterNameReplacements toVerify = findTypeParameterNameReplacements(secondaryMethodTypeParameters, classTypeParameters);

            TypeParameterNameReplacements expected = TypeParameterNameReplacements.builder()
                    .replaceSecondaryMethodTypeParameter(I, M1)
                    .replaceSecondaryMethodTypeParameter(M, M2)
                    .replaceSecondaryMethodTypeParameter(C, C)
                    .replaceClassTypeParameter(C, C)
                    .build();

            assertEquals(toVerify, expected);
        });
    }

    @Test
    public void findTypeParameterNameReplacementGivenConflictsBetweenSecondaryMethodTypeParametersAndNewPrimaryMethodTypeParametersOrNewResultTypeParameterButReplacementConflictsWithClassTypeParameters() {
        PRIMARY_METHOD_TYPE_PARAMETER_NAMES_AND_RESULT_TYPE_PARAMETER_NAME.forEach(I -> {

            List<TypeParameter> secondaryMethodTypeParameters = typeParameters(I, M);
            List<TypeParameter> classTypeParameters = typeParameters(M1);

            TypeParameterNameReplacements toVerify = findTypeParameterNameReplacements(secondaryMethodTypeParameters, classTypeParameters);

            TypeParameterNameReplacements expected = TypeParameterNameReplacements.builder()
                    .replaceSecondaryMethodTypeParameter(I, M1)
                    .replaceSecondaryMethodTypeParameter(M, M2)
                    .replaceSecondaryMethodTypeParameter(M1, C1)
                    .replaceClassTypeParameter(M1, C1)
                    .build();

            assertEquals(toVerify, expected);
        });
    }

    @Test
    public void findTypeParameterNameReplacementGivenConflictsBetweenSecondaryMethodTypeParametersAndNewPrimaryMethodTypeParametersOrNewResultTypeParameterButReplacementConflictsWithClassTypeParametersAndViceVersa() {
        PRIMARY_METHOD_TYPE_PARAMETER_NAMES_AND_RESULT_TYPE_PARAMETER_NAME.forEach(I -> {

            List<TypeParameter> secondaryMethodTypeParameters = typeParameters(I, C2);
            List<TypeParameter> classTypeParameters = typeParameters(M1, M2);

            TypeParameterNameReplacements toVerify = findTypeParameterNameReplacements(secondaryMethodTypeParameters, classTypeParameters);

            TypeParameterNameReplacements expected = TypeParameterNameReplacements.builder()
                    .replaceSecondaryMethodTypeParameter(I, M1)
                    .replaceSecondaryMethodTypeParameter(C2, M2)
                    .replaceSecondaryMethodTypeParameter(M1, C1)
                    .replaceSecondaryMethodTypeParameter(M2, C2)
                    .replaceClassTypeParameter(M1, C1)
                    .replaceClassTypeParameter(M2, C2)
                    .build();

            assertEquals(toVerify, expected);
        });
    }

    @Test
    public void findTypeParameterNameReplacementGivenConflictsBetweenSecondaryMethodTypeParametersAndBothClassTypeParametersAndNewPrimaryMethodTypeParametersOrNewResultTypeParameterIndependently() {
        PRIMARY_METHOD_TYPE_PARAMETER_NAMES_AND_RESULT_TYPE_PARAMETER_NAME.forEach(I -> {

            List<TypeParameter> secondaryMethodTypeParameters = typeParameters(I, S);
            List<TypeParameter> classTypeParameters = typeParameters(S);

            TypeParameterNameReplacements toVerify = findTypeParameterNameReplacements(secondaryMethodTypeParameters, classTypeParameters);

            TypeParameterNameReplacements expected = TypeParameterNameReplacements.builder()
                    .replaceSecondaryMethodTypeParameter(I, M1)
                    .replaceSecondaryMethodTypeParameter(S, M2)
                    .replaceClassTypeParameter(S, S)
                    .build();

            assertEquals(toVerify, expected);
        });
    }

    @Test
    public void findTypeParameterNameReplacementGivenConflictsBetweenClassTypeParametersAndNewPrimaryMethodTypeParametersOrNewResultTypeParameter() {
        PRIMARY_METHOD_TYPE_PARAMETER_NAMES_AND_RESULT_TYPE_PARAMETER_NAME.forEach(I -> {

            List<TypeParameter> secondaryMethodTypeParameters = typeParameters(M);
            List<TypeParameter> classTypeParameters = typeParameters(I, C);

            TypeParameterNameReplacements toVerify = findTypeParameterNameReplacements(secondaryMethodTypeParameters, classTypeParameters);

            TypeParameterNameReplacements expected = TypeParameterNameReplacements.builder()
                    .replaceSecondaryMethodTypeParameter(M, M)
                    .replaceSecondaryMethodTypeParameter(I, C1)
                    .replaceSecondaryMethodTypeParameter(C, C2)
                    .replaceClassTypeParameter(I, C1)
                    .replaceClassTypeParameter(C, C2)
                    .build();

            assertEquals(toVerify, expected);
        });
    }

    @Test
    public void findTypeParameterNameReplacementGivenConflictsBetweenClassTypeParametersAndNewPrimaryMethodTypeParametersOrNewResultTypeParameterButReplacementConflictsWithSecondaryMethodTypeParameters() {
        PRIMARY_METHOD_TYPE_PARAMETER_NAMES_AND_RESULT_TYPE_PARAMETER_NAME.forEach(I -> {

            List<TypeParameter> secondaryMethodTypeParameters = typeParameters(C1);
            List<TypeParameter> classTypeParameters = typeParameters(I, C);

            TypeParameterNameReplacements toVerify = findTypeParameterNameReplacements(secondaryMethodTypeParameters, classTypeParameters);

            TypeParameterNameReplacements expected = TypeParameterNameReplacements.builder()
                    .replaceSecondaryMethodTypeParameter(C1, M1)
                    .replaceSecondaryMethodTypeParameter(I, C1)
                    .replaceSecondaryMethodTypeParameter(C, C2)
                    .replaceClassTypeParameter(I, C1)
                    .replaceClassTypeParameter(C, C2)
                    .build();

            assertEquals(toVerify, expected);
        });
    }

    @Test
    public void findTypeParameterNameReplacementGivenConflictsBetweenClassTypeParametersAndNewPrimaryMethodTypeParametersOrNewResultTypeParameterButReplacementConflictsWithSecondaryMethodTypeParametersAndViceVersa() {
        PRIMARY_METHOD_TYPE_PARAMETER_NAMES_AND_RESULT_TYPE_PARAMETER_NAME.forEach(I -> {

            List<TypeParameter> secondaryMethodTypeParameters = typeParameters(C1, C2);
            List<TypeParameter> classTypeParameters = typeParameters(I, M2);

            TypeParameterNameReplacements toVerify = findTypeParameterNameReplacements(secondaryMethodTypeParameters, classTypeParameters);

            TypeParameterNameReplacements expected = TypeParameterNameReplacements.builder()
                    .replaceSecondaryMethodTypeParameter(C1, M1)
                    .replaceSecondaryMethodTypeParameter(C2, M2)
                    .replaceSecondaryMethodTypeParameter(I, C1)
                    .replaceSecondaryMethodTypeParameter(M2, C2)
                    .replaceClassTypeParameter(I, C1)
                    .replaceClassTypeParameter(M2, C2)
                    .build();

            assertEquals(toVerify, expected);
        });
    }

    @Test
    public void findTypeParameterNameReplacementGivenConflictsBetweenClassTypeParametersAndBothSecondaryMethodTypeParametersAndNewPrimaryMethodTypeParametersOrNewResultTypeParameterIndependently() {
        PRIMARY_METHOD_TYPE_PARAMETER_NAMES_AND_RESULT_TYPE_PARAMETER_NAME.forEach(I -> {

            List<TypeParameter> secondaryMethodTypeParameters = typeParameters(S);
            List<TypeParameter> classTypeParameters = typeParameters(I, S);

            TypeParameterNameReplacements toVerify = findTypeParameterNameReplacements(secondaryMethodTypeParameters, classTypeParameters);

            TypeParameterNameReplacements expected = TypeParameterNameReplacements.builder()
                    .replaceSecondaryMethodTypeParameter(S, S)
                    .replaceSecondaryMethodTypeParameter(I, C1)
                    .replaceClassTypeParameter(I, C1)
                    .replaceClassTypeParameter(S, C2)
                    .build();

            assertEquals(toVerify, expected);
        });
    }

    @Test
    public void findTypeParameterNameReplacementGivenBothSecondaryTypeParametersAndClassTypeParametersConflictWithDifferentNewPrimaryMethodTypeParametersOrNewResultTypeParameter() {
        PRIMARY_METHOD_TYPE_PARAMETER_NAMES_AND_RESULT_TYPE_PARAMETER_NAME.forEach(I -> {
            PRIMARY_METHOD_TYPE_PARAMETER_NAMES_AND_RESULT_TYPE_PARAMETER_NAME.forEach(J -> {

                // If both are in conflict with each other, skip.
                // There is a separate test for this.
                if (Objects.equals(I, J)) {
                    return;
                }

                List<TypeParameter> secondaryMethodTypeParameters = typeParameters(I, M);
                List<TypeParameter> classTypeParameters = typeParameters(J, C);

                TypeParameterNameReplacements toVerify = findTypeParameterNameReplacements(secondaryMethodTypeParameters, classTypeParameters);

                TypeParameterNameReplacements expected = TypeParameterNameReplacements.builder()
                        .replaceSecondaryMethodTypeParameter(I, M1)
                        .replaceSecondaryMethodTypeParameter(M, M2)
                        .replaceSecondaryMethodTypeParameter(J, C1)
                        .replaceSecondaryMethodTypeParameter(C, C2)
                        .replaceClassTypeParameter(J, C1)
                        .replaceClassTypeParameter(C, C2)
                        .build();

                assertEquals(toVerify, expected);
            });
        });
    }

    @Test
    public void findTypeParameterNameReplacementGivenSecondaryTypeParametersAndClassTypeParametersConflictWithSameNewPrimaryMethodTypeParametersOrNewResultTypeParameter() {
        PRIMARY_METHOD_TYPE_PARAMETER_NAMES_AND_RESULT_TYPE_PARAMETER_NAME.forEach(I -> {

            List<TypeParameter> secondaryMethodTypeParameters = typeParameters(I, M);
            List<TypeParameter> classTypeParameters = typeParameters(I, C);

            TypeParameterNameReplacements toVerify = findTypeParameterNameReplacements(secondaryMethodTypeParameters, classTypeParameters);

            TypeParameterNameReplacements expected = TypeParameterNameReplacements.builder()
                    .replaceSecondaryMethodTypeParameter(I, M1)
                    .replaceSecondaryMethodTypeParameter(M, M2)
                    .replaceSecondaryMethodTypeParameter(C, C2)
                    .replaceClassTypeParameter(I, C1)
                    .replaceClassTypeParameter(C, C2)
                    .build();

            assertEquals(toVerify, expected);
        });
    }

    private List<TypeParameter> typeParameters(TypeParameterName... typeParameterNames) {
        return Stream.of(typeParameterNames).map(TypeParameterName::asTypeParameter).collect(toList());
    }

    private static final TypeParameterName M = TypeParameterName.of("M");
    private static final TypeParameterName C = TypeParameterName.of("C");
    private static final TypeParameterName S = TypeParameterName.of("S");
    private static final TypeParameterName M1 = TypeParameterName.of("M1");
    private static final TypeParameterName M2 = TypeParameterName.of("M2");
    private static final TypeParameterName C1 = TypeParameterName.of("C1");
    private static final TypeParameterName C2 = TypeParameterName.of("C2");
    private static final String s = "s";
    private static final String i = "i";
    private static final String s1 = "s1";
    private static final String s2 = "s2";

    private static final List<TypeParameterName> PRIMARY_METHOD_TYPE_PARAMETER_NAMES_AND_RESULT_TYPE_PARAMETER_NAME;

    static {
        PRIMARY_METHOD_TYPE_PARAMETER_NAMES_AND_RESULT_TYPE_PARAMETER_NAME = new ArrayList<>();
        PRIMARY_METHOD_TYPE_PARAMETER_NAMES_AND_RESULT_TYPE_PARAMETER_NAME.addAll(PRIMARY_METHOD_TYPE_PARAMETER_NAMES);
        PRIMARY_METHOD_TYPE_PARAMETER_NAMES_AND_RESULT_TYPE_PARAMETER_NAME.add(RESULT_TYPE_PARAMETER_NAME);
    }
}
