package nl.wernerdegroot.applicatives.processor.conflicts;

import nl.wernerdegroot.applicatives.processor.domain.FullyQualifiedName;
import nl.wernerdegroot.applicatives.processor.domain.Parameter;
import nl.wernerdegroot.applicatives.processor.domain.TypeBuilder;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        PRIMARY_METHOD_TYPE_PARAMETER_NAMES_AND_RESULT_TYPE_PARAMETER_NAME.forEach(I -> {
            PRIMARY_METHOD_TYPE_PARAMETER_NAMES_AND_RESULT_TYPE_PARAMETER_NAME.forEach(J -> {

                // If both are in conflict with each other, skip.
                if (Objects.equals(I, J)) {
                    return;
                }

                ConflictFree conflictFree = ConflictPrevention.preventConflicts(
                        // Secondary method type parameters:
                        asList(I.extending(ARDUOUS.with(I, M, J, C)), M.asTypeParameter()),

                        // Class type parameters:
                        asList(J.extending(PROFUSE.with(J, C)), C.asTypeParameter()),

                        // Secondary parameters:
                        asList(Parameter.of(SAPIENT.with(I, M, J, C), first), Parameter.of(BUOYANT.with(S), second)),

                        // Parameter type constructor:
                        ERUDITE.with(I.asTypeConstructor().invariant(), M.asTypeConstructor().covariant(), placeholder().contravariant(), J.asTypeConstructor().invariant(), C.asTypeConstructor().covariant()),

                        // Result type constructor:
                        VOLUBLE.with(S.asTypeConstructor().contravariant(), placeholder().invariant())
                );

                // Primary method type parameters:
                assertEquals(PRIMARY_METHOD_TYPE_PARAMETERS, conflictFree.getPrimaryMethodTypeParameters());

                // Result type parameter:
                assertEquals(RESULT_TYPE_PARAMETER, conflictFree.getResultTypeParameter());

                // Secondary method type parameters:
                assertEquals(asList(M1.extending(ARDUOUS.with(M1, M2, C1, C2)), M2.asTypeParameter()), conflictFree.getSecondaryMethodTypeParameters());

                // Class type parameters:
                assertEquals(asList(C1.extending(PROFUSE.with(C1, C2)), C2.asTypeParameter()), conflictFree.getClassTypeParameters());

                // Primary parameters:
                assertEquals(EXPECTED_PRIMARY_METHOD_PARAMETER_NAMES, conflictFree.getPrimaryParameterNames());

                // Secondary parameters:
                assertEquals(asList(Parameter.of(SAPIENT.with(M1, M2, C1, C2), s1), Parameter.of(BUOYANT.with(S), s2)), conflictFree.getSecondaryParameters());

                // Parameter type constructor:
                assertEquals(ERUDITE.with(M1.asTypeConstructor().invariant(), M2.asTypeConstructor().covariant(), placeholder().contravariant(), C1.asTypeConstructor().invariant(), C2.asTypeConstructor().covariant()), conflictFree.getParameterTypeConstructor());

                // Result type constructor:
                assertEquals(VOLUBLE.with(S.asTypeConstructor().contravariant(), placeholder().invariant()), conflictFree.getResultTypeConstructor());
            });
        });
    }

    private static final TypeParameterName M = TypeParameterName.of("M");
    private static final TypeParameterName C = TypeParameterName.of("C");
    private static final TypeParameterName S = TypeParameterName.of("S");
    private static final TypeParameterName M1 = TypeParameterName.of("M1");
    private static final TypeParameterName M2 = TypeParameterName.of("M2");
    private static final TypeParameterName C1 = TypeParameterName.of("C1");
    private static final TypeParameterName C2 = TypeParameterName.of("C2");
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

    private static final List<TypeParameterName> PRIMARY_METHOD_TYPE_PARAMETER_NAMES_AND_RESULT_TYPE_PARAMETER_NAME;

    static {
        PRIMARY_METHOD_TYPE_PARAMETER_NAMES_AND_RESULT_TYPE_PARAMETER_NAME = new ArrayList<>();
        PRIMARY_METHOD_TYPE_PARAMETER_NAMES_AND_RESULT_TYPE_PARAMETER_NAME.addAll(PRIMARY_METHOD_TYPE_PARAMETER_NAMES);
        PRIMARY_METHOD_TYPE_PARAMETER_NAMES_AND_RESULT_TYPE_PARAMETER_NAME.add(RESULT_TYPE_PARAMETER_NAME);
    }

    private static final List<String> EXPECTED_PRIMARY_METHOD_PARAMETER_NAMES = ORDINALS;
}
