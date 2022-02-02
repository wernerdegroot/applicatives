package nl.wernerdegroot.applicatives.processor.conflicts;

import nl.wernerdegroot.applicatives.processor.domain.Parameter;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameter;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;

import java.util.*;
import java.util.function.IntFunction;

import static java.util.stream.Collectors.toList;
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
     * suggestions are returned as {@link ParameterNameReplacements ParameterNameReplacements}.
     *
     * @param secondaryParameters The secondary parameters that the programmer supplied, which
     * might conflict with the new parameters.
     *
     * @return {@code ParameterNameReplacements} with suggestions to rename certain parameters.
     * Each parameter name of the supplied {@code secondaryParameters} will have a mapping to a
     * new (possibly different) parameter name.
     */
    public static ParameterNameReplacements findParameterNameReplacements(List<Parameter> secondaryParameters) {
        // == Step 1 ==
        // We'll introduce a couple of new parameters. Of course this can cause conflicts
        // with the existing parameters, so we'll proceed carefully in this method.

        List<String> parameterNamesThatCanCauseConflicts = new ArrayList<>();
        parameterNamesThatCanCauseConflicts.addAll(PRIMARY_PARAMETER_NAMES);
        parameterNamesThatCanCauseConflicts.add(SELF_PARAMETER_NAME);
        parameterNamesThatCanCauseConflicts.add(COMBINATOR_PARAMETER_NAME);
        parameterNamesThatCanCauseConflicts.add(MAX_TUPLE_SIZE_PARAMETER_NAME);

        // == Step 2 ==
        // Determine if we have to replace the original parameters because they will
        // cause conflicts with the parameters introduced in Step 1.

        Map<String, String> parameterNameReplacements = parametersHaveConflicts(secondaryParameters, parameterNamesThatCanCauseConflicts)
                ? generateParameterNameReplacements(secondaryParameters, Conflicts::alternativeSecondaryMethodParameterName)
                : identityParameterReplacements(secondaryParameters);

        // == Step 3 ==
        // Return.

        return ParameterNameReplacements.of(parameterNameReplacements);
    }

    /**
     * In the process of generating overloads, we will be introducing new type parameters. This might
     * conflict with the type parameter names the programmer has supplied. To avoid these kinds of
     * conflicts, this function might suggest renaming some of the programmer's type parameters.
     * These suggestions are returned as {@link TypeParameterNameReplacements TypeParameterNameReplacements}.
     * <p>
     * You might wonder why we need to supply both the {@code secondaryMethodTypeParameters} as well
     * as the {@code classTypeParameters}. Wouldn't creating two separate methods for both make more
     * sense? Unfortunately, this won't work as both {@code secondaryMethodTypeParameters} and
     * {@code classTypeParameters} (or even their replacements may conflict with each other too.
     *
     * @param secondaryMethodTypeParameters The secondary method type parameters that the programmer
     * supplied, which might conflict with the new type parameters.
     *
     * @param classTypeParameters The class type parameters that the programmer supplied, which might
     * conflict with the new type parameters.
     *
     * @return {@link TypeParameterNameReplacements TypeParameterNameReplacements}
     * with suggestions to rename certain type parameters. The mappings for the class type parameters
     * will contain a mapping for each of the {@code classTypeParameters}. The mappings for the secondary
     * method type parameters will contain both a mapping for each of the {@code secondaryMethodTypeParameters}
     * as well as each of the {@code classTypeParameters} because both will be visible at the method-level,
     * and both may need to be replaced.
     */
    public static TypeParameterNameReplacements findTypeParameterNameReplacements(List<TypeParameter> secondaryMethodTypeParameters, List<TypeParameter> classTypeParameters) {
        return findTypeParameterNameReplacements(secondaryMethodTypeParameters, classTypeParameters, false, false);
    }

    private static TypeParameterNameReplacements findTypeParameterNameReplacements(
            List<TypeParameter> secondaryMethodTypeParameters,
            List<TypeParameter> classTypeParameters,
            boolean forceReplaceSecondaryMethodTypeParameters,
            boolean forceReplaceClassTypeParameters) {

        // == Step 1 ==
        // We'll introduce a couple of new type parameters. Of course this can cause conflicts
        // with the existing type parameters, so we'll proceed carefully in this method.

        List<TypeParameter> primaryMethodTypeParameters = PRIMARY_METHOD_TYPE_PARAMETERS;

        TypeParameter resultTypeParameter = RESULT_TYPE_PARAMETER;

        // == Step 2 ==
        // Let's see if those new type parameters will cause conflicts with the existing type parameters.
        // These existing type parameters will be at both the class-level and the method-level.

        List<TypeParameter> typeParametersThatCanCauseConflicts = new ArrayList<>();
        typeParametersThatCanCauseConflicts.addAll(primaryMethodTypeParameters);
        typeParametersThatCanCauseConflicts.add(resultTypeParameter);

        boolean classTypeParametersHaveConflicts = typeParametersHaveConflictsWithOtherTypeParameters(classTypeParameters, typeParametersThatCanCauseConflicts);
        boolean secondaryMethodTypeParametersHaveConflicts = typeParametersHaveConflictsWithOtherTypeParameters(secondaryMethodTypeParameters, typeParametersThatCanCauseConflicts);

        // == Step 3 ==
        // If the introduction of new type parameters in Step 1 caused conflicts with the
        // class-level type parameters, we'll have to replace those with type parameters
        // that are guaranteed not to have conflicts with these new type parameters. We
        // will also replace the class-level type parameters if we are asked to do so with
        // the parameter `forceReplaceClassTypeParameters`.

        Map<TypeParameterName, TypeParameterName> classTypeParameterReplacements = classTypeParametersHaveConflicts || forceReplaceClassTypeParameters
                ? generateTypeParameterNameReplacements(classTypeParameters, Conflicts::alternativeClassTypeParameterName)
                : identityTypeParameterReplacements(classTypeParameters);

        // == Step 4 ==
        // Even though the replacements in `classTypeParameterReplacements` guarantee that
        // the new primary method type parameters and the class type parameters are not in
        // conflict, these replacements could have introduced its own conflicts with the
        // existing secondary method type parameters. If this is the case, make sure we replace
        // those secondary method type parameters too.

        if (!forceReplaceSecondaryMethodTypeParameters && typeParametersHaveConflictsWithTypeParameterNames(secondaryMethodTypeParameters, classTypeParameterReplacements.values())) {
            return findTypeParameterNameReplacements(secondaryMethodTypeParameters, classTypeParameters, true, forceReplaceClassTypeParameters);
        }

        // == Step 5 ==
        // If the introduction of new type parameters in Step 1 caused conflicts with the
        // secondary method type parameters, we'll have to replace those with type parameters
        // that are guaranteed not to have conflicts with these new type parameters. We
        // will also replace the secondary method type parameters if we are asked to do so with
        // the parameter `forceReplaceSecondaryMethodTypeParameters`.

        Map<TypeParameterName, TypeParameterName> secondaryMethodTypeParameterReplacements = secondaryMethodTypeParametersHaveConflicts || forceReplaceSecondaryMethodTypeParameters
                ? generateTypeParameterNameReplacements(secondaryMethodTypeParameters, Conflicts::alternativeSecondaryMethodTypeParameterName)
                : identityTypeParameterReplacements(secondaryMethodTypeParameters);

        // == Step 6 ==
        // Even though the replacements in `secondaryMethodTypeParameterReplacements` guarantee
        // that the new primary method type parameters and the secondary method type parameters
        // are not in conflict, these replacements could have introduced its own conflicts with
        // the existing class-level type parameters. If this is the case, make sure we replace
        // those class-level type parameters too.

        if (!forceReplaceClassTypeParameters && typeParametersHaveConflictsWithTypeParameterNames(classTypeParameters, secondaryMethodTypeParameterReplacements.values())) {
            return findTypeParameterNameReplacements(secondaryMethodTypeParameters, classTypeParameters, forceReplaceSecondaryMethodTypeParameters, true);
        }

        // == Step 7 ==
        // Note that both the method-level replacements and the class-level replacements apply.
        // Make sure we merge those two (where the method-level replacements take precedence)
        // to get the full set of replacements at the method-level.

        classTypeParameterReplacements.forEach(secondaryMethodTypeParameterReplacements::putIfAbsent);

        // == Step 9 ==
        // Return.

        return TypeParameterNameReplacements.of(classTypeParameterReplacements, secondaryMethodTypeParameterReplacements);
    }

    private static boolean parametersHaveConflicts(Collection<Parameter> left, List<String> right) {
        // For efficiency, use a `Set`:
        Set<String> leftNames = left.stream().map(Parameter::getName).collect(toSet());
        return right.stream().anyMatch(leftNames::contains);
    }

    private static boolean typeParametersHaveConflictsWithOtherTypeParameters(Collection<TypeParameter> left, Collection<TypeParameter> right) {
        return typeParametersHaveConflictsWithTypeParameterNames(left, right.stream().map(TypeParameter::getName).collect(toList()));
    }

    private static boolean typeParametersHaveConflictsWithTypeParameterNames(Collection<TypeParameter> left, Collection<TypeParameterName> right) {
        // For efficiency, use a `Set`:
        Set<TypeParameterName> leftNames = left.stream().map(TypeParameter::getName).collect(toSet());
        return right.stream().anyMatch(leftNames::contains);
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
        int numberOfInertClassTypeParameters = typeParameters.size();
        for (int i = 0; i < numberOfInertClassTypeParameters; ++i) {
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
