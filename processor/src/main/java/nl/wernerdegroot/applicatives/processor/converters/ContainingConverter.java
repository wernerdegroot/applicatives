package nl.wernerdegroot.applicatives.processor.converters;

import nl.wernerdegroot.applicatives.processor.domain.PackageName;
import nl.wernerdegroot.applicatives.processor.domain.containing.Containing;
import nl.wernerdegroot.applicatives.processor.domain.containing.ContainingPackage;

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import java.util.Objects;

public class ContainingConverter {

    /**
     * Converts a class from the world of {@link javax.lang.model} to the
     * world of {@link nl.wernerdegroot.applicatives.processor.domain}.
     *
     * @param element A package, class, interface or record
     * @return {@link Containing Containing}
     */
    public static Containing toDomain(Element element) {
        Objects.requireNonNull(element);

        if (element instanceof PackageElement) {
            PackageElement packageElement = (PackageElement) element;
            PackageName packageName = PackageName.of(packageElement.getQualifiedName().toString());
            return ContainingPackage.of(packageName);
        } else if (element instanceof TypeElement) {
            return ContainingClassConverter.toDomain(element);
        } else {
            throw new IllegalArgumentException("Not a package, class, interface or record");
        }
    }
}
