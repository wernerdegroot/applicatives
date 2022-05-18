package nl.wernerdegroot.applicatives.processor.conflicts;

import nl.wernerdegroot.applicatives.processor.domain.TypeParameter;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static nl.wernerdegroot.applicatives.processor.conflicts.ConflictFinder.findClassTypeParameterNameReplacements;

/**
 * In the process of generating overloads, we will be introducing new parameters and type parameters.
 * These new parameters and type parameters might conflict with the parameters and type parameters
 * that the programmer supplied. The method {@link ConflictPrevention#preventConflicts(List, TypeConstructor, TypeConstructor, TypeConstructor) preventConflicts}.
 * might be used to resolve these conflicts.
 */
public class ConflictPrevention {

    /**
     * In the process of generating overloads, we will be introducing new parameters and type parameters.
     * These new parameters and type parameters might conflict with the parameters and type parameters
     * that the programmer supplied. This method might be used to resolve these conflicts.
     *
     * @param classTypeParameters
     * @param accumulationTypeConstructor
     * @param permissiveAccumulationTypeConstructor
     * @param inputTypeConstructor
     * @return {@link ConflictFree} with conflict-free parameter
     * names and type parameter names that can be used directly when generating code.
     */
    public static ConflictFree preventConflicts(
            List<TypeParameter> classTypeParameters,
            TypeConstructor accumulationTypeConstructor,
            TypeConstructor permissiveAccumulationTypeConstructor,
            TypeConstructor inputTypeConstructor,
            Optional<TypeConstructor> optionalResultTypeConstructor) {

        Map<TypeParameterName, TypeParameterName> classTypeParameterNameReplacements = findClassTypeParameterNameReplacements(classTypeParameters);

        List<TypeParameter> conflictFreeClassTypeParameters = classTypeParameters
                .stream()
                .map(typeParameter -> typeParameter.replaceAllTypeParameterNames(classTypeParameterNameReplacements))
                .collect(Collectors.toList());

        TypeConstructor conflictFreeAccumulationTypeConstructor = accumulationTypeConstructor.replaceAllTypeParameterNames(classTypeParameterNameReplacements);
        TypeConstructor conflictFreePermissiveAccumulationTypeConstructor = permissiveAccumulationTypeConstructor.replaceAllTypeParameterNames(classTypeParameterNameReplacements);
        TypeConstructor conflictFreeInputTypeConstructor = inputTypeConstructor.replaceAllTypeParameterNames(classTypeParameterNameReplacements);
        Optional<TypeConstructor> conflictFreeOptionalResultTypeConstructor = optionalResultTypeConstructor.map(resultTypeConstructor -> resultTypeConstructor.replaceAllTypeParameterNames(classTypeParameterNameReplacements));

        return ConflictFree.of(
                conflictFreeClassTypeParameters,
                conflictFreeAccumulationTypeConstructor,
                conflictFreePermissiveAccumulationTypeConstructor,
                conflictFreeInputTypeConstructor,
                conflictFreeOptionalResultTypeConstructor
        );

    }
}
