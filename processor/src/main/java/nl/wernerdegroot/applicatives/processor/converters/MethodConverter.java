package nl.wernerdegroot.applicatives.processor.converters;

import nl.wernerdegroot.applicatives.processor.domain.*;
import nl.wernerdegroot.applicatives.processor.domain.type.ConcreteType;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleTypeVisitor8;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class MethodConverter {

    /**
     * Converts a class from the world of {@link javax.lang.model} to the
     * world of {@link nl.wernerdegroot.applicatives.processor.domain}.
     *
     * @param element A method
     * @return {@link Method Method}
     */
    public static Method toDomain(Element element) {
        Objects.requireNonNull(element);

        if (element.getKind() != ElementKind.METHOD) {
            throw new IllegalArgumentException("Not a method");
        }

        ExecutableElement method = (ExecutableElement) element;

        Set<FullyQualifiedName> annotations = getAnnotations(method);

        Set<Modifier> modifiers = method.getModifiers().stream().map(ModifierConverter::toDomain).collect(toSet());

        List<TypeParameter> typeParameters = method.getTypeParameters().stream().map(TypeParameterConverter::toDomain).collect(toList());

        Optional<Type> returnType = returnTypeAsOptional(method.getReturnType()).map(TypeConverter::toDomain);

        String name = method.getSimpleName().toString();

        List<Parameter> parameters = method
                .getParameters()
                .stream()
                .map(parameter -> {
                    Type parameterType = TypeConverter.toDomain(parameter.asType());
                    String parameterName = parameter.getSimpleName().toString();
                    return Parameter.of(parameterType, parameterName);
                })
                .collect(toList());

        return Method.of(annotations, modifiers, typeParameters, returnType, name, parameters);
    }

    private static Set<FullyQualifiedName> getAnnotations(ExecutableElement method) {
        return method.getAnnotationMirrors()
                .stream()
                .map(AnnotationMirror::getAnnotationType)
                .map(TypeConverter::toDomain)
                .map(ConcreteType::getFullyQualifiedName)
                .collect(toSet());
    }

    private static Optional<TypeMirror> returnTypeAsOptional(TypeMirror typeMirror) {
        return typeMirror.accept(
                new SimpleTypeVisitor8<Optional<TypeMirror>, Void>() {

                    @Override
                    protected Optional<TypeMirror> defaultAction(TypeMirror notVoid, Void unused) {
                        return Optional.of(notVoid);
                    }

                    @Override
                    public Optional<TypeMirror> visitNoType(NoType noType, Void unused) {
                        return Optional.empty();
                    }
                },
                null
        );
    }
}
