package nl.wernerdegroot.applicatives.processor.domain;

import nl.wernerdegroot.applicatives.processor.domain.containing.ContainingPackage;

import java.util.Objects;

public class PackageName {
    private final String packageName;

    public PackageName(String packageName) {
        this.packageName = packageName;
    }

    public static PackageName of(String packageName) {
        return new PackageName(packageName);
    }

    public FullyQualifiedName withClassName(ClassName className) {
        return FullyQualifiedName.of(packageName + "." + className.raw());
    }

    public ContainingPackage asPackage() {
        return ContainingPackage.of(this);
    }

    public String raw() {
        return packageName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PackageName that = (PackageName) o;
        return raw().equals(that.raw());
    }

    @Override
    public int hashCode() {
        return Objects.hash(raw());
    }

    @Override
    public String toString() {
        return "PackageName{" +
                "packageName='" + packageName + '\'' +
                '}';
    }
}
