package nl.wernerdegroot.applicatives.processor.domain.containing;

import nl.wernerdegroot.applicatives.processor.domain.ClassName;
import nl.wernerdegroot.applicatives.processor.domain.Modifier;
import nl.wernerdegroot.applicatives.processor.domain.PackageName;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameter;

import java.util.Set;
import java.util.function.Function;

import static java.util.Arrays.asList;

public interface Containing {

    <R> R match(Function<ContainingPackage, R> matchPackage, Function<ContainingClass, R> matchClass);

    PackageName getPackageName();

    boolean isPackage();

    boolean isClass();

    boolean isOuterClass();

    boolean isStaticInnerClass();

    default ContainingClass containingClass(Set<Modifier> modifiers, ClassName className, TypeParameter... typeParameters) {
        return ContainingClass.of(this, modifiers, className, asList(typeParameters));
    }
}
