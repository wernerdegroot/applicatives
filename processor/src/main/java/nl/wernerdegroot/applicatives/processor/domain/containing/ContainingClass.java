package nl.wernerdegroot.applicatives.processor.domain.containing;

import nl.wernerdegroot.applicatives.processor.domain.*;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static nl.wernerdegroot.applicatives.processor.domain.Modifier.PUBLIC;
import static nl.wernerdegroot.applicatives.processor.domain.Modifier.STATIC;

public final class ContainingClass implements Containing {

    private final Containing parent;
    private final Set<Modifier> modifiers;
    private final ClassName className;
    private final List<TypeParameter> typeParameters;

    public ContainingClass(Containing parent, Set<Modifier> modifiers, ClassName className, List<TypeParameter> typeParameters) {
        this.parent = parent;
        this.modifiers = modifiers;
        this.className = className;
        this.typeParameters = typeParameters;
    }

    public static ContainingClass of(Containing parent, Set<Modifier> modifiers, ClassName className, List<TypeParameter> typeParameters) {
        return new ContainingClass(parent, modifiers, className, typeParameters);
    }

    public static ContainingClass of(PackageName packageName, ClassName className, List<TypeParameter> typeParameters) {
        return ContainingClass.of(ContainingPackage.of(packageName), singleton(PUBLIC), className, typeParameters);
    }

    public static ContainingClass of(PackageName packageName, ClassName className) {
        return ContainingClass.of(packageName, className, emptyList());
    }

    public FullyQualifiedName getFullyQualifiedName() {
        return getParent().match(
                containingPackage -> containingPackage.getPackageName().withClassName(getClassName()),
                containingClass -> containingClass.getFullyQualifiedName().withClassName(getClassName())
        );
    }

    @Override
    public <R> R match(Function<ContainingPackage, R> matchPackage, Function<ContainingClass, R> matchClass) {
        return matchClass.apply(this);
    }

    @Override
    public PackageName getPackageName() {
        return parent.getPackageName();
    }

    @Override
    public boolean isPackage() {
        return false;
    }

    @Override
    public boolean isClass() {
        return true;
    }

    @Override
    public boolean isOuterClass() {
        return getParent().isPackage();
    }

    @Override
    public boolean isStaticInnerClass() {
        return getParent().isClass() && getModifiers().contains(STATIC);
    }

    public Containing getParent() {
        return parent;
    }

    public Set<Modifier> getModifiers() {
        return modifiers;
    }

    public ClassName getClassName() {
        return className;
    }

    public List<TypeParameter> getTypeParameters() {
        return typeParameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContainingClass that = (ContainingClass) o;
        return Objects.equals(getParent(), that.getParent()) && Objects.equals(getModifiers(), that.getModifiers()) && Objects.equals(getClassName(), that.getClassName()) && Objects.equals(getTypeParameters(), that.getTypeParameters());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getParent(), getModifiers(), getClassName(), getTypeParameters());
    }

    @Override
    public String toString() {
        return "ContainingClass{" +
                "parent=" + parent +
                ", modifiers=" + modifiers +
                ", className=" + className +
                ", typeParameters=" + typeParameters +
                '}';
    }
}
