package nl.wernerdegroot.applicatives.processor.converters;

import nl.wernerdegroot.applicatives.processor.domain.TypeParameter;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;

import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.IntersectionType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleTypeVisitor8;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class TypeParameterConverter {

    /**
     * Converts a class from the world of {@link javax.lang.model} to the
     * world of {@link nl.wernerdegroot.applicatives.processor.domain}.
     *
     * @param typeParameterElement A type parameter
     *
     * @return {@link TypeParameter TypeParameter}
     */
    public static TypeParameter toDomain(TypeParameterElement typeParameterElement) {
        TypeParameterName name = TypeParameterName.of(typeParameterElement.getSimpleName().toString());
        List<Type> bounds = typeParameterElement
                .getBounds()
                .stream()
                // It  may be that a certain JDK models the bounds as a single `TypeMirror`
                // which happens to be an intersection type. To be able to deal with that
                // too, I'll flatten the possible intersection to a list of type `TypeMirror`
                // first:
                .flatMap(TypeParameterConverter::flattenIntersection)
                .map(TypeConverter::toDomain)
                .collect(toList());
        return TypeParameter.of(name, bounds);
    }

    private static Stream<? extends TypeMirror> flattenIntersection(TypeMirror typeMirror) {
        return typeMirror.accept(
                new SimpleTypeVisitor8<Stream<? extends TypeMirror>, Void>() {

                    @Override
                    protected Stream<? extends TypeMirror> defaultAction(TypeMirror notIntersection, Void unused) {
                        return Stream.of(notIntersection);
                    }

                    @Override
                    public Stream<? extends TypeMirror> visitIntersection(IntersectionType intersection, Void unused) {
                        return intersection.getBounds().stream();
                    }
                },
                null
        );
    }
}
