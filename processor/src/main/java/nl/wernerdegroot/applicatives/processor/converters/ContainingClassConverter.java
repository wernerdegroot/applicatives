package nl.wernerdegroot.applicatives.processor.converters;

import nl.wernerdegroot.applicatives.processor.domain.ClassName;
import nl.wernerdegroot.applicatives.processor.domain.Modifier;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameter;
import nl.wernerdegroot.applicatives.processor.domain.containing.Containing;
import nl.wernerdegroot.applicatives.processor.domain.containing.ContainingClass;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class ContainingClassConverter {

    /**
     * Converts a class from the world of {@link javax.lang.model} to the
     * world of {@link nl.wernerdegroot.applicatives.processor.domain}.
     *
     * @param element A class, interface or record
     *
     * @return {@link ContainingClass ContainingClass}
     */
    public static ContainingClass toDomain(Element element) {
        Objects.requireNonNull(element);

        if (!(element instanceof TypeElement)) {
            throw new IllegalArgumentException("Not a class, interface or record");
        }

        TypeElement typeElement = (TypeElement) element;

        Containing parent = ContainingConverter.toDomain(element.getEnclosingElement());
        Set<Modifier> modifiers = typeElement.getModifiers().stream().map(ModifierConverter::toDomain).collect(toSet());
        ClassName className = ClassName.of(typeElement.getSimpleName().toString());
        List<TypeParameter> typeParameters = typeElement.getTypeParameters().stream().map(TypeParameterConverter::toDomain).collect(toList());

        return ContainingClass.of(parent, modifiers, className, typeParameters);
    }
}
