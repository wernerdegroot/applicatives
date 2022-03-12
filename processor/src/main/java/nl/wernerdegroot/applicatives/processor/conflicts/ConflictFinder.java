package nl.wernerdegroot.applicatives.processor.conflicts;

import nl.wernerdegroot.applicatives.processor.domain.Parameter;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameter;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;

import java.util.*;
import java.util.function.IntFunction;

import static java.util.stream.Collectors.toSet;
import static nl.wernerdegroot.applicatives.processor.conflicts.Conflicts.*;

/**
 * In the process of generating overloads, we will be introducing new parameters and type parameters.
 * These new parameters and type parameters might conflict with the parameters and type parameters
 * that the programmer supplied. To avoid these kinds of conflicts, the methods in the class might
 * suggest renaming some of the programmer's parameters and type parameters.
 */
public class ConflictFinder {

    /**
     * In the process of generating overloads, we will be introducing new parameters. This might
     * conflict with the parameter names the programmer has supplied. To avoid these kinds of
     * conflicts, this function might suggest renaming some of the programmer's parameters. These
     * suggestions are returned as {@code Map<String, String>}.
     *
     * @param secondaryParameters The secondary parameters that the programmer supplied, which
     *                            might conflict with the new parameters.
     * @return {@code Map<String, String>} with suggestions to rename certain parameters.
     * Each parameter name of the supplied {@code secondaryParameters} will have a mapping to a
     * new (possibly different) parameter name.
     */
    public static Map<String, String> findParameterNameReplacements(List<Parameter> secondaryParameters) {
        List<String> parameterNamesThatCanCauseConflicts = new ArrayList<>();
        parameterNamesThatCanCauseConflicts.addAll(PRIMARY_PARAMETER_NAMES);
        parameterNamesThatCanCauseConflicts.add(SELF_PARAMETER_NAME);
        parameterNamesThatCanCauseConflicts.add(COMBINATOR_PARAMETER_NAME);
        parameterNamesThatCanCauseConflicts.add(MAX_TUPLE_SIZE_PARAMETER_NAME);

        Map<String, String> parameterNameReplacements = parametersHaveConflicts(secondaryParameters, parameterNamesThatCanCauseConflicts)
                ? generateParameterNameReplacements(secondaryParameters, Conflicts::alternativeSecondaryMethodParameterName)
                : identityParameterReplacements(secondaryParameters);

        return parameterNameReplacements;
    }

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
        typeParametersThatCanCauseConflicts.addAll(PARTICIPANT_TYPE_PARAMETERS);
        typeParametersThatCanCauseConflicts.add(RESULT_TYPE_PARAMETER);

        Map<TypeParameterName, TypeParameterName> classTypeParameterReplacements = typeParametersHaveConflicts(classTypeParameters, typeParametersThatCanCauseConflicts)
                ? generateTypeParameterNameReplacements(classTypeParameters, Conflicts::alternativeClassTypeParameterName)
                : identityTypeParameterReplacements(classTypeParameters);

        return classTypeParameterReplacements;
    }

    private static boolean parametersHaveConflicts(Collection<Parameter> left, List<String> right) {
        Set<String> leftNames = left.stream().map(Parameter::getName).collect(toSet());
        return right.stream().anyMatch(leftNames::contains);
    }

    private static boolean typeParametersHaveConflicts(Collection<TypeParameter> left, Collection<TypeParameter> right) {
        Set<TypeParameterName> leftNames = left.stream().map(TypeParameter::getName).collect(toSet());
        return right.stream().map(TypeParameter::getName).anyMatch(leftNames::contains);
    }

    private static Map<String, String> generateParameterNameReplacements(List<Parameter> parameters, IntFunction<String> getAlternative) {
        Map<String, String> substitutions = new HashMap<>();
        int numberOfParameters = parameters.size();
        for (int i = 0; i < numberOfParameters; ++i) {
            String originalName = parameters.get(i).getName();
            String alternativeName = getAlternative.apply(i);
            substitutions.put(originalName, alternativeName);
        }
        return substitutions;
    }

    private static Map<String, String> identityParameterReplacements(Collection<Parameter> parameters) {
        Map<String, String> substitutions = new HashMap<>();
        for (Parameter parameter : parameters) {
            String name = parameter.getName();
            substitutions.put(name, name);
        }
        return substitutions;
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
