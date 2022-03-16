package nl.wernerdegroot.applicatives.processor.conflicts;

import nl.wernerdegroot.applicatives.processor.domain.TypeParameter;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static nl.wernerdegroot.applicatives.processor.conflicts.ConflictFinder.findClassTypeParameterNameReplacements;
import static nl.wernerdegroot.applicatives.processor.conflicts.Conflicts.INPUT_TYPE_CONSTRUCTOR_ARGUMENT_NAMES;
import static nl.wernerdegroot.applicatives.processor.conflicts.Conflicts.RESULT_TYPE_CONSTRUCTOR_ARGUMENT_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConflictFinderTest {

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
    public void findClassTypeParameterNameReplacementGivenConflictWithNewInputTypeConstructorArguments() {
        INPUT_AND_RESULT_TYPE_CONSTRUCTOR_ARGUMENT_NAMES.forEach(conflictingTypeParameter -> {
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

    private static final List<TypeParameterName> INPUT_AND_RESULT_TYPE_CONSTRUCTOR_ARGUMENT_NAMES;

    static {
        INPUT_AND_RESULT_TYPE_CONSTRUCTOR_ARGUMENT_NAMES = new ArrayList<>();
        INPUT_AND_RESULT_TYPE_CONSTRUCTOR_ARGUMENT_NAMES.addAll(INPUT_TYPE_CONSTRUCTOR_ARGUMENT_NAMES);
        INPUT_AND_RESULT_TYPE_CONSTRUCTOR_ARGUMENT_NAMES.add(RESULT_TYPE_CONSTRUCTOR_ARGUMENT_NAME);
    }
}
