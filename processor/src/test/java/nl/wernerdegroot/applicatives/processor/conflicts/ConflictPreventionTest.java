package nl.wernerdegroot.applicatives.processor.conflicts;

import nl.wernerdegroot.applicatives.processor.domain.FullyQualifiedName;
import nl.wernerdegroot.applicatives.processor.domain.Parameter;
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
    // that the conflicts are resolved completely (not only by renaming parameter names and
    // type parameter names, but also by replacing the programmer's type parameter names in
    // all types by their conflict-free alternative). Figuring out if there are conflicts
    // and, if there are, what to do about them is delegated to `ConflictFinder` (which is
    // tested thoroughly). All that remains is making sure that all parameter names and type
    // parameter names are replaced everywhere. Even that will not be rigorously tested, as
    // replacing is already covered by the tests for `Type`, `TypeConstructor` and `Parameter`.
    // A single, well-designed test may suffice for what remains.
    @Test
    public void preventConflicts() {
        PARTICIPANT_TYPE_PARAMETER_NAMES_AND_RESULT_TYPE_PARAMETER_NAME.forEach(conflictingTypeParameter -> {

            ConflictFree conflictFree = ConflictPrevention.preventConflicts(
                    // Class type parameters:
                    asList(T.asTypeParameter(), conflictingTypeParameter.extending(PROFUSE.with(T, conflictingTypeParameter, V)), V.asTypeParameter()),

                    // Secondary parameters:
                    asList(Parameter.of(SAPIENT.with(T, conflictingTypeParameter, V), first), Parameter.of(BUOYANT.with(T, U, V), second)),

                    // Left parameter type constructor:
                    ERUDITE.with(T.asTypeConstructor().invariant(), conflictingTypeParameter.asTypeConstructor().covariant(), placeholder().contravariant()),

                    // Right parameter type constructor:
                    ARDUOUS.with(U.asTypeConstructor().invariant(), conflictingTypeParameter.asTypeConstructor().covariant(), placeholder().contravariant()),

                    // Result type constructor:
                    VOLUBLE.with(V.asTypeConstructor().invariant(), conflictingTypeParameter.asTypeConstructor().covariant(), placeholder().contravariant())
            );

            // Participant type parameters:
            assertEquals(PARTICIPANT_TYPE_PARAMETERS, conflictFree.getParticipantTypeParameters());

            // Result type parameter:
            assertEquals(RESULT_TYPE_PARAMETER, conflictFree.getResultTypeParameter());

            // Class type parameters:
            assertEquals(asList(C1.asTypeParameter(), C2.extending(PROFUSE.with(C1, C2, C3)), C3.asTypeParameter()), conflictFree.getClassTypeParameters());

            // Primary parameters:
            assertEquals(EXPECTED_PRIMARY_METHOD_PARAMETER_NAMES, conflictFree.getPrimaryParameterNames());

            // Secondary parameters:
            assertEquals(asList(Parameter.of(SAPIENT.with(C1, C2, C3), s1), Parameter.of(BUOYANT.with(C1, U, C3), s2)), conflictFree.getSecondaryParameters());

            // Left parameter type constructor:
            assertEquals(ERUDITE.with(C1.asTypeConstructor().invariant(), C2.asTypeConstructor().covariant(), placeholder().contravariant()), conflictFree.getLeftParameterTypeConstructor());

            // Right parameter type constructor:
            assertEquals(ARDUOUS.with(U.asTypeConstructor().invariant(), C2.asTypeConstructor().covariant(), placeholder().contravariant()), conflictFree.getRightParameterTypeConstructor());

            // Result type constructor:
            assertEquals(VOLUBLE.with(C3.asTypeConstructor().invariant(), C2.asTypeConstructor().covariant(), placeholder().contravariant()), conflictFree.getResultTypeConstructor());
        });
    }

    private static final TypeParameterName T = TypeParameterName.of("T");
    private static final TypeParameterName U = TypeParameterName.of("U");
    private static final TypeParameterName V = TypeParameterName.of("V");
    private static final TypeParameterName C1 = TypeParameterName.of("C1");
    private static final TypeParameterName C2 = TypeParameterName.of("C2");
    private static final TypeParameterName C3 = TypeParameterName.of("C3");
    private static final String first = "first";
    private static final String second = "second";
    private static final String s1 = "s1";
    private static final String s2 = "s2";
    private static final TypeBuilder ARDUOUS = new TypeBuilder(FullyQualifiedName.of("nl.wernerdegroot.Arduous"));
    private static final TypeBuilder SAPIENT = new TypeBuilder(FullyQualifiedName.of("nl.wernerdegroot.Sapient"));
    private static final TypeBuilder ERUDITE = new TypeBuilder(FullyQualifiedName.of("nl.wernerdegroot.Erudite"));
    private static final TypeBuilder PROFUSE = new TypeBuilder(FullyQualifiedName.of("nl.wernerdegroot.Profuse"));
    private static final TypeBuilder VOLUBLE = new TypeBuilder(FullyQualifiedName.of("nl.wernerdegroot.Voluble"));
    private static final TypeBuilder BUOYANT = new TypeBuilder(FullyQualifiedName.of("nl.wernerdegroot.Buoyant"));

    private static final List<TypeParameterName> PARTICIPANT_TYPE_PARAMETER_NAMES_AND_RESULT_TYPE_PARAMETER_NAME;

    static {
        PARTICIPANT_TYPE_PARAMETER_NAMES_AND_RESULT_TYPE_PARAMETER_NAME = new ArrayList<>();
        PARTICIPANT_TYPE_PARAMETER_NAMES_AND_RESULT_TYPE_PARAMETER_NAME.addAll(PARTICIPANT_TYPE_PARAMETER_NAMES);
        PARTICIPANT_TYPE_PARAMETER_NAMES_AND_RESULT_TYPE_PARAMETER_NAME.add(RESULT_TYPE_PARAMETER_NAME);
    }

    private static final List<String> EXPECTED_PRIMARY_METHOD_PARAMETER_NAMES = ORDINALS;
}
