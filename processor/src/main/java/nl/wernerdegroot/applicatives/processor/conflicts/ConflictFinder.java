package nl.wernerdegroot.applicatives.processor.conflicts;

import nl.wernerdegroot.applicatives.processor.domain.TypeParameter;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;

import java.util.*;
import java.util.function.IntFunction;

import static java.util.stream.Collectors.toSet;
import static nl.wernerdegroot.applicatives.processor.conflicts.Conflicts.PARAMETER_TYPE_CONSTRUCTOR_ARGUMENTS;
import static nl.wernerdegroot.applicatives.processor.conflicts.Conflicts.RETURN_TYPE_CONSTRUCTOR_ARGUMENT;

/**
 * In the process of generating overloads, we will be introducing new type parameters.
 * These new type parameters might conflict with the type parameters that the programmer
 * supplied. To avoid these kinds of conflicts, the methods in the class might
 * suggest renaming some of the programmer's type parameters.
 */
public class ConflictFinder {

    /**
     * In the process of generating overloads, we will be introducing new type parameters. This might
     * conflict with the class type parameter names the programmer has supplied. To avoid these kinds of
     * conflicts, this function might suggest renaming some of the programmer's class type parameters.
     * These suggestions are returned as {@code Map<TypeParameterName, TypeParameterName>}.
     *
     * @param classTypeParameters The class type parameters that the programmer supplied, which might
     *                            conflict with the new type parameters.
     * @return {@code Map<TypeParameterName, TypeParameterName>} with suggestions to rename certain
     * class type parameters. The mappings for the class type parameters will contain a mapping for
     * each of the {@code classTypeParameters}.
     */
    public static Map<TypeParameterName, TypeParameterName> findClassTypeParameterNameReplacements(List<TypeParameter> classTypeParameters) {
        List<TypeParameter> typeParametersThatCanCauseConflicts = new ArrayList<>();
        typeParametersThatCanCauseConflicts.addAll(PARAMETER_TYPE_CONSTRUCTOR_ARGUMENTS);
        typeParametersThatCanCauseConflicts.add(RETURN_TYPE_CONSTRUCTOR_ARGUMENT);

        Map<TypeParameterName, TypeParameterName> classTypeParameterReplacements = typeParametersHaveConflicts(classTypeParameters, typeParametersThatCanCauseConflicts)
                ? generateTypeParameterNameReplacements(classTypeParameters, Conflicts::alternativeClassTypeParameterName)
                : identityTypeParameterReplacements(classTypeParameters);

        return classTypeParameterReplacements;
    }

    private static boolean typeParametersHaveConflicts(Collection<TypeParameter> left, Collection<TypeParameter> right) {
        Set<TypeParameterName> leftNames = left.stream().map(TypeParameter::getName).collect(toSet());
        return right.stream().map(TypeParameter::getName).anyMatch(leftNames::contains);
    }

    private static Map<TypeParameterName, TypeParameterName> generateTypeParameterNameReplacements(List<TypeParameter> typeParameters, IntFunction<TypeParameterName> getAlternative) {
        Map<TypeParameterName, TypeParameterName> substitutions = new HashMap<>();
        int numberOfTypeParameters = typeParameters.size();
        for (int i = 0; i < numberOfTypeParameters; ++i) {
            TypeParameter typeParameter = typeParameters.get(i);
            TypeParameterName alternativeName = getAlternative.apply(i);

            TypeParameterName originalName = typeParameter.getName();
            substitutions.put(originalName, alternativeName);
        }
        return substitutions;
    }

    private static Map<TypeParameterName, TypeParameterName> identityTypeParameterReplacements(Collection<TypeParameter> typeParameters) {
        Map<TypeParameterName, TypeParameterName> substitutions = new HashMap<>();
        for (TypeParameter typeParameter : typeParameters) {
            TypeParameterName name = typeParameter.getName();
            substitutions.put(name, name);
        }
        return substitutions;
    }
}
