package nl.wernerdegroot.applicatives.processor.validation;

import nl.wernerdegroot.applicatives.processor.domain.TypeParameter;
import nl.wernerdegroot.applicatives.processor.domain.containing.ContainingClass;

import java.util.List;

public class TemplateClassValidator {

    public static Validated<TemplateClass> validate(ContainingClass containingClass) {

        // We check whether we're dealing with an outer class or a static inner class.
        // Why is this required? Consider the following scenario:
        //
        //   class Outer<A extends B, B> {
        //     class Inner<B> {
        //       ...
        //     }
        //   }
        //
        // It is impossible to collapse these three type parameters of these two classes into a
        // single list of type parameters without carefully renaming some of them to avoid shadowing.
        // Instead of going through all that effort for this extreme edge-case, I'm just going avoid
        // it completely. If we'd like to be more sophisticated we could try:
        //
        //  * To support only a single class with type parameters in the hierarchy
        //  * Only support multiple classes with type parameters if their names don't conflict
        //  * Only support conflicts if the type parameter that is shadowed can be removed
        //    completely (isn't used as upper bound for any of the other type parameters)
        if (!containingClass.isOuterClass() && !containingClass.isStaticInnerClass()) {
            return Validated.invalid("Only outer classes and static inner classes are currently supported");
        }
        List<TypeParameter> typeParameters = containingClass.getTypeParameters();

        return Validated.valid(TemplateClass.of(typeParameters));
    }
}
