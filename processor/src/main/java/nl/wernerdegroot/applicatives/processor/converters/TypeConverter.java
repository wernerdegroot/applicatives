package nl.wernerdegroot.applicatives.processor.converters;

import nl.wernerdegroot.applicatives.processor.domain.FullyQualifiedName;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.*;
import javax.lang.model.util.AbstractTypeVisitor8;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;
import static nl.wernerdegroot.applicatives.processor.domain.BoundType.EXTENDS;
import static nl.wernerdegroot.applicatives.processor.domain.BoundType.SUPER;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.OBJECT;

public class TypeConverter {

    /**
     * Converts a class from the world of {@link javax.lang.model} to the
     * world of {@link nl.wernerdegroot.applicatives.processor.domain}.
     * <p>
     * Not every type is supported. This method will throw an exception when
     * passed one of the following types:
     * <ul>
     *     <li>Intersection type</li>
     *     <li>Union type</li>
     *     <li>Null type</li>
     *     <li>A non-static inner class</li>
     *     <li>Error type</li>
     *     <li>Executable type</li>
     *     <li>No type</li>
     * </ul>
     * Ignoring the non-static inner classes, should not occur in method definitions.
     * If you do happen to get an exception about one of these types, please raise an
     * issue with a small code example.
     *
     * @param type A type
     *
     * @return {@link Type Type}
     */
    public static Type toDomain(TypeMirror type) {
        Objects.requireNonNull(type);

        return type.accept(
                new AbstractTypeVisitor8<Type, Void>() {
                    @Override
                    public Type visitIntersection(IntersectionType t, Void unused) {
                        throw new IllegalArgumentException(String.format("Unexpected intersection type %s", t));
                    }

                    @Override
                    public Type visitUnion(UnionType unionType, Void unused) {
                        throw new IllegalArgumentException(String.format("Unexpected union type %s", unionType));
                    }

                    @Override
                    public Type visitPrimitive(PrimitiveType primitiveType, Void unused) {
                        String primitiveName = primitiveType.getKind().name().toLowerCase();
                        return Type.concrete(FullyQualifiedName.of(primitiveName));
                    }

                    @Override
                    public Type visitNull(NullType nullType, Void unused) {
                        throw new IllegalArgumentException(String.format("Unexpected null type %s", nullType));
                    }

                    @Override
                    public Type visitArray(ArrayType arrayType, Void unused) {
                        return Type.array(toDomain(arrayType.getComponentType()));
                    }

                    @Override
                    public Type visitDeclared(DeclaredType declaredType, Void unused) {
                        if (declaredType.getEnclosingType().getKind() != TypeKind.NONE) {
                            throw new IllegalArgumentException("Use of (non-static) inner classes is currently not supported.");
                        }

                        Element element = declaredType.asElement();
                        TypeElement typeElement = (TypeElement) element;
                        FullyQualifiedName fullyQualifiedName = FullyQualifiedName.of(typeElement.getQualifiedName().toString());
                        List<Type> typeArguments = declaredType.getTypeArguments()
                                .stream()
                                .map(TypeConverter::toDomain)
                                .collect(toList());
                        return Type.concrete(fullyQualifiedName, typeArguments);
                    }

                    @Override
                    public Type visitError(ErrorType errorType, Void unused) {
                        throw new IllegalArgumentException(String.format("Unexpected error type %s", errorType));
                    }

                    @Override
                    public Type visitTypeVariable(TypeVariable typeVariable, Void unused) {
                        // We don't follow the lower- and upper bounds of the type variable (in this case, a type argument)
                        // here. All we need to know is _which_ type variable this is.
                        return Type.generic(TypeParameterName.of(typeVariable.asElement().getSimpleName().toString()));
                    }

                    @Override
                    public Type visitWildcard(WildcardType wildcardType, Void unused) {
                        if (wildcardType.getExtendsBound() != null && wildcardType.getSuperBound() != null) {
                            throw new IllegalArgumentException(String.format("Unexpected wildcard type %s with both lower bound %s and upper bound %s", wildcardType, wildcardType.getSuperBound(), wildcardType.getExtendsBound()));
                        } else if (wildcardType.getExtendsBound() != null) {
                            return Type.wildcard(EXTENDS, toDomain(wildcardType.getExtendsBound()));
                        } else if (wildcardType.getSuperBound() != null) {
                            return Type.wildcard(SUPER, toDomain(wildcardType.getSuperBound()));
                        } else {
                            return Type.wildcard(EXTENDS, OBJECT);
                        }
                    }

                    @Override
                    public Type visitExecutable(ExecutableType executableType, Void unused) {
                        throw new IllegalArgumentException(String.format("Unexpected executable type %s", executableType));
                    }

                    @Override
                    public Type visitNoType(NoType noType, Void unused) {
                        throw new IllegalArgumentException(String.format("Missing type %s", noType));
                    }
                },
                null
        );
    }
}
