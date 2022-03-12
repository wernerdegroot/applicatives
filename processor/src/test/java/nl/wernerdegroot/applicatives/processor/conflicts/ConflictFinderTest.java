package nl.wernerdegroot.applicatives.processor.conflicts;

import nl.wernerdegroot.applicatives.processor.domain.Parameter;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameter;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static nl.wernerdegroot.applicatives.processor.conflicts.ConflictFinder.findParameterNameReplacements;
import static nl.wernerdegroot.applicatives.processor.conflicts.ConflictFinder.findClassTypeParameterNameReplacements;
import static nl.wernerdegroot.applicatives.processor.conflicts.Conflicts.*;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConflictFinderTest {

    @Test
    public void findParameterNameReplacementsGivenNoConflicts() {
        List<Parameter> secondaryParameters = asList(
                Parameter.of(STRING, s),
                Parameter.of(INTEGER, i)
        );

        Map<String, String> toVerify = findParameterNameReplacements(secondaryParameters);

        Map<String, String> expected = new HashMap<>();
        expected.put(s, s);
        expected.put(i, i);

        assertEquals(expected, toVerify);
    }

    @Test
    public void findParameterNameReplacementsGivenConflictWithNewPrimaryParameterNames() {
        PRIMARY_PARAMETER_NAMES.forEach(primaryParameterName -> {
            List<Parameter> secondaryParameters = asList(
                    Parameter.of(STRING, primaryParameterName),
                    Parameter.of(INTEGER, i)
            );

            Map<String, String> toVerify = findParameterNameReplacements(secondaryParameters);

            Map<String, String> expected = new HashMap<>();
            expected.put(primaryParameterName, s1);
            expected.put(i, s2);

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

            Map<String, String> toVerify = findParameterNameReplacements(secondaryParameters);

            Map<String, String> expected = new HashMap<>();
            expected.put(s, s1);
            expected.put(otherParameterName, s2);

            assertEquals(expected, toVerify);
        });
    }

    @Test
    public void findTypeParameterNameReplacementGivenNoConflicts() {
        List<TypeParameter> classTypeParameters = typeParameters(T, U, V);

        Map<TypeParameterName, TypeParameterName> toVerify = findClassTypeParameterNameReplacements(classTypeParameters);

        Map<TypeParameterName, TypeParameterName> expected = new HashMap<>();
        expected.put(T, T);
        expected.put(U, U);
        expected.put(V, V);

        assertEquals(toVerify, expected);
    }

    @Test
    public void findClassTypeParameterNameReplacementGivenConflictWithNewMethodTypeParameters() {
        PARTICIPANT_TYPE_PARAMETER_NAMES_AND_RESULT_TYPE_PARAMETER_NAME.forEach(conflictingTypeParameter -> {
            List<TypeParameter> classTypeParameters = typeParameters(T, conflictingTypeParameter, V);

            Map<TypeParameterName, TypeParameterName> toVerify = findClassTypeParameterNameReplacements(classTypeParameters);

            Map<TypeParameterName, TypeParameterName> expected = new HashMap<>();
            expected.put(T, C1);
            expected.put(conflictingTypeParameter, C2);
            expected.put(V, C3);

            assertEquals(toVerify, expected);
        });
    }

    private List<TypeParameter> typeParameters(TypeParameterName... typeParameterNames) {
        return Stream.of(typeParameterNames).map(TypeParameterName::asTypeParameter).collect(toList());
    }

    private static final TypeParameterName T = TypeParameterName.of("T");
    private static final TypeParameterName U = TypeParameterName.of("U");
    private static final TypeParameterName V = TypeParameterName.of("V");
    private static final TypeParameterName C1 = TypeParameterName.of("C1");
    private static final TypeParameterName C2 = TypeParameterName.of("C2");
    private static final TypeParameterName C3 = TypeParameterName.of("C3");
    private static final String s = "s";
    private static final String i = "i";
    private static final String s1 = "s1";
    private static final String s2 = "s2";

    private static final List<TypeParameterName> PARTICIPANT_TYPE_PARAMETER_NAMES_AND_RESULT_TYPE_PARAMETER_NAME;

    static {
        PARTICIPANT_TYPE_PARAMETER_NAMES_AND_RESULT_TYPE_PARAMETER_NAME = new ArrayList<>();
        PARTICIPANT_TYPE_PARAMETER_NAMES_AND_RESULT_TYPE_PARAMETER_NAME.addAll(PARTICIPANT_TYPE_PARAMETER_NAMES);
        PARTICIPANT_TYPE_PARAMETER_NAMES_AND_RESULT_TYPE_PARAMETER_NAME.add(RESULT_TYPE_PARAMETER_NAME);
    }
}
