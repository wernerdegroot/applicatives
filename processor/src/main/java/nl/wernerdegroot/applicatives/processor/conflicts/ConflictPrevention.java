package nl.wernerdegroot.applicatives.processor.conflicts;

import nl.wernerdegroot.applicatives.processor.domain.Parameter;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameter;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static nl.wernerdegroot.applicatives.processor.conflicts.ConflictFinder.findParameterNameReplacements;
import static nl.wernerdegroot.applicatives.processor.conflicts.ConflictFinder.findTypeParameterNameReplacements;
import static nl.wernerdegroot.applicatives.processor.conflicts.Conflicts.*;

/**
 * In the process of generating overloads, we will be introducing new parameters and type parameters.
 * These new parameters and type parameters might conflict with the parameters and type parameters
 * that the programmer supplied. The method {@link ConflictPrevention#preventConflicts(List, List, List, TypeConstructor, TypeConstructor) preventConflicts}.
 * might be used to resolve these conflicts.
 */
public class ConflictPrevention {

    /**
     * In the process of generating overloads, we will be introducing new parameters and type parameters.
     * These new parameters and type parameters might conflict with the parameters and type parameters
     * that the programmer supplied. This method might be used to resolve these conflicts.
     *
     * @param secondaryMethodTypeParameters
     * @param classTypeParameters
     * @param secondaryParameters
     * @param parameterTypeConstructor
     * @param resultTypeConstructor
     * @return {@link ConflictFree} with conflict-free parameter
     * names and type parameter names that can be used directly when generating code.
     */
    public static ConflictFree preventConflicts(
            List<TypeParameter> secondaryMethodTypeParameters,
            List<TypeParameter> classTypeParameters,
            List<Parameter> secondaryParameters,
            TypeConstructor parameterTypeConstructor,
            TypeConstructor resultTypeConstructor) {

        // == Step 1 ==
        // Find replacements for parameter names.

        ParameterNameReplacements parameterNameReplacements = findParameterNameReplacements(secondaryParameters);

        // == Step 2 ==
        // Find replacements for type parameter names.

        TypeParameterNameReplacements typeParameterNameReplacements = findTypeParameterNameReplacements(secondaryMethodTypeParameters, classTypeParameters);

        // == Step 3 ==
        // Resolve all conflicts using the replacements we have generated in Steps 1 and 2.

        List<TypeParameter> conflictFreeClassTypeParameters = classTypeParameters
                .stream()
                .map(typeParameter -> typeParameter.replaceAllTypeParameterNames(typeParameterNameReplacements.getClassTypeParameterReplacements()))
                .collect(Collectors.toList());

        List<TypeParameter> conflictFreeSecondaryMethodTypeParameters = secondaryMethodTypeParameters
                .stream()
                .map(typeParameter -> typeParameter.replaceAllTypeParameterNames(typeParameterNameReplacements.getSecondaryMethodTypeParameterReplacements()))
                .collect(Collectors.toList());

        TypeConstructor conflictFreeParameterTypeConstructor = parameterTypeConstructor.replaceAllTypeParameterNames(typeParameterNameReplacements.getSecondaryMethodTypeParameterReplacements());

        TypeConstructor conflictFreeResultTypeConstructor = resultTypeConstructor.replaceAllTypeParameterNames(typeParameterNameReplacements.getSecondaryMethodTypeParameterReplacements());

        List<Parameter> conflictFreeSecondaryParameters = secondaryParameters
                .stream()
                .map(parameter -> parameter.replaceAllTypeParameterNames(typeParameterNameReplacements.getSecondaryMethodTypeParameterReplacements()).replaceParameterName(parameterNameReplacements.getParameterNameReplacements()))
                .collect(Collectors.toList());

        // == Step 4 ==
        // Return.

        return ConflictFree.of(
                PRIMARY_METHOD_TYPE_PARAMETERS,
                RESULT_TYPE_PARAMETER,
                conflictFreeSecondaryMethodTypeParameters,
                conflictFreeClassTypeParameters,
                PRIMARY_PARAMETER_NAMES,
                conflictFreeSecondaryParameters,
                SELF_PARAMETER_NAME,
                COMBINATOR_PARAMETER_NAME,
                MAX_TUPLE_SIZE_PARAMETER_NAME,
                conflictFreeParameterTypeConstructor,
                conflictFreeResultTypeConstructor
        );

    }
}
