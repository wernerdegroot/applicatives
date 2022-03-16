package nl.wernerdegroot.applicatives.processor.conflicts;

import nl.wernerdegroot.applicatives.processor.domain.FullyQualifiedName;
import nl.wernerdegroot.applicatives.processor.domain.TypeBuilder;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static nl.wernerdegroot.applicatives.processor.Ordinals.ORDINALS;
import static nl.wernerdegroot.applicatives.processor.conflicts.Conflicts.*;
import static nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor.placeholder;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConflictPreventionTest {

    // A single test may suffice. In this test, we will start with some conflicts and verify
    // that the conflicts are resolved completely (not only by renaming type parameter names,
    // but also by replacing the programmer's type parameter names in all types by their
    // conflict-free alternative). Figuring out if there are conflicts and, if there are, what
    // to do about them is delegated to `ConflictFinder` (which is tested thoroughly). All
    // that remains is making sure that all type parameter names are replaced everywhere. Even
    // that will not be rigorously tested, as replacing is already covered by the tests for
    // `Type`, `TypeConstructor` and `Parameter`. A single, well-designed test may suffice for
    // what remains.
    @Test
    public void preventConflicts() {
        INPUT_AND_RESULT_TYPE_CONSTRUCTOR_ARGUMENT_NAMES.forEach(conflictingTypeParameter -> {

            ConflictFree conflictFree = ConflictPrevention.preventConflicts(
                    // Class type parameters:
                    asList(T.asTypeParameter(), conflictingTypeParameter.extending(PROFUSE.with(T, conflictingTypeParameter, V)), V.asTypeParameter()),

                    // Accumulation type constructor:
                    VOLUBLE.with(V.asTypeConstructor().invariant(), conflictingTypeParameter.asTypeConstructor().covariant(), placeholder().contravariant()),

                    // Permissive accumulation type constructor:
                    ERUDITE.with(placeholder().covariant(), T.asTypeConstructor().contravariant(), conflictingTypeParameter.asTypeConstructor().invariant()),

                    // Input type constructor:
                    ARDUOUS.with(conflictingTypeParameter.asTypeConstructor().contravariant(), placeholder().invariant(), U.asTypeConstructor().covariant())
            );

            // Input type constructor arguments:
            assertEquals(INPUT_TYPE_CONSTRUCTOR_ARGUMENTS, conflictFree.getInputTypeConstructorArguments());

            // Result type constructor argument:
            assertEquals(RESULT_TYPE_CONSTRUCTOR_ARGUMENT, conflictFree.getResultTypeConstructorArguments());

            // Class type parameters:
            assertEquals(asList(C1.asTypeParameter(), C2.extending(PROFUSE.with(C1, C2, C3)), C3.asTypeParameter()), conflictFree.getClassTypeParameters());

            // Input parameters:
            assertEquals(EXPECTED_INPUT_PARAMETER_NAMES, conflictFree.getInputParameterNames());

            // Accumulation type constructor:
            assertEquals(VOLUBLE.with(C3.asTypeConstructor().invariant(), C2.asTypeConstructor().covariant(), placeholder().contravariant()), conflictFree.getAccumulationTypeConstructor());

            // Permissive accumulation type constructor:
            assertEquals(ERUDITE.with(placeholder().covariant(), C1.asTypeConstructor().contravariant(), C2.asTypeConstructor().invariant()), conflictFree.getPermissiveAccumulationTypeConstructor());

            // Input type constructor:
            assertEquals(ARDUOUS.with(C2.asTypeConstructor().contravariant(), placeholder().invariant(), U.asTypeConstructor().covariant()), conflictFree.getInputTypeConstructor());
        });
    }

    private static final TypeParameterName T = TypeParameterName.of("T");
    private static final TypeParameterName U = TypeParameterName.of("U");
    private static final TypeParameterName V = TypeParameterName.of("V");
    private static final TypeParameterName C1 = TypeParameterName.of("C1");
    private static final TypeParameterName C2 = TypeParameterName.of("C2");
    private static final TypeParameterName C3 = TypeParameterName.of("C3");
    private static final TypeBuilder ARDUOUS = new TypeBuilder(FullyQualifiedName.of("nl.wernerdegroot.Arduous"));
    private static final TypeBuilder ERUDITE = new TypeBuilder(FullyQualifiedName.of("nl.wernerdegroot.Erudite"));
    private static final TypeBuilder PROFUSE = new TypeBuilder(FullyQualifiedName.of("nl.wernerdegroot.Profuse"));
    private static final TypeBuilder VOLUBLE = new TypeBuilder(FullyQualifiedName.of("nl.wernerdegroot.Voluble"));

    private static final List<TypeParameterName> INPUT_AND_RESULT_TYPE_CONSTRUCTOR_ARGUMENT_NAMES;

    static {
        INPUT_AND_RESULT_TYPE_CONSTRUCTOR_ARGUMENT_NAMES = new ArrayList<>();
        INPUT_AND_RESULT_TYPE_CONSTRUCTOR_ARGUMENT_NAMES.addAll(INPUT_TYPE_CONSTRUCTOR_ARGUMENT_NAMES);
        INPUT_AND_RESULT_TYPE_CONSTRUCTOR_ARGUMENT_NAMES.add(RESULT_TYPE_CONSTRUCTOR_ARGUMENT_NAME);
    }

    private static final List<String> EXPECTED_INPUT_PARAMETER_NAMES = ORDINALS;
}
