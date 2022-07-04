package nl.wernerdegroot.applicatives.processor.converters;

import nl.wernerdegroot.applicatives.processor.domain.FullyQualifiedName;
import nl.wernerdegroot.applicatives.processor.domain.Method;

import javax.lang.model.element.TypeElement;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static javax.lang.model.element.ElementKind.METHOD;

public class EnclosedMethodsConverter {

    public static List<Method> toDomain(TypeElement typeElement, FullyQualifiedName... supportedAnnotations) {
        return typeElement
                .getEnclosedElements()
                .stream()
                .filter(enclosedElement -> enclosedElement.getKind() == METHOD)
                .map(MethodConverter::toDomain)
                .filter(method -> method.hasAnnotationOf(supportedAnnotations))
                .collect(toList());
    }
}
