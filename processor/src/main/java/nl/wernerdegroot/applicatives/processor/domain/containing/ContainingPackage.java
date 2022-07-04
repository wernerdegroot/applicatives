package nl.wernerdegroot.applicatives.processor.domain.containing;

import nl.wernerdegroot.applicatives.processor.domain.PackageName;

import java.util.Objects;
import java.util.function.Function;

public final class ContainingPackage implements Containing {

    private final PackageName packageName;

    public ContainingPackage(PackageName packageName) {
        this.packageName = packageName;
    }

    public static ContainingPackage of(PackageName packageName) {
        return new ContainingPackage(packageName);
    }

    @Override
    public <R> R match(Function<ContainingPackage, R> matchPackage, Function<ContainingClass, R> matchClass) {
        return matchPackage.apply(this);
    }

    @Override
    public PackageName getPackageName() {
        return packageName;
    }

    @Override
    public boolean isPackage() {
        return true;
    }

    @Override
    public boolean isClass() {
        return false;
    }

    @Override
    public boolean isOuterClass() {
        return false;
    }

    @Override
    public boolean isStaticInnerClass() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContainingPackage that = (ContainingPackage) o;
        return Objects.equals(getPackageName(), that.getPackageName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPackageName());
    }

    @Override
    public String toString() {
        return "ContainingPackage{" +
                "packageName=" + packageName +
                '}';
    }
}
