package nl.wernerdegroot.applicatives.processor.domain.type;

import nl.wernerdegroot.applicatives.processor.domain.*;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static nl.wernerdegroot.applicatives.processor.domain.Variance.*;

/**
 * Represents a type in the Java language. Supported types are:
 * <ul>
 *     <li>Concrete types, like {@code int}, {@link String String} and {@link List List&lt;String&gt;}</li>
 *     <li>Generic types, like {@code T}</li>
 *     <li>Array types, like {@code int[]}</li>
 *     <li>Wildcard types, like {@code ? extends Comparable<?>}</li>
 * </ul>
 * Each of these is represented as a subclass of {@link Type Type}
 */
public interface Type {

    ConcreteType INT = Type.concrete(FullyQualifiedName.of("int"));
    ConcreteType CHAR = Type.concrete(FullyQualifiedName.of("char"));
    ConcreteType INTEGER = Type.concrete(FullyQualifiedName.of("java.lang.Integer"));
    ConcreteType BOOLEAN = Type.concrete(FullyQualifiedName.of("java.lang.Boolean"));
    ConcreteType NUMBER = Type.concrete(FullyQualifiedName.of("java.lang.Number"));
    ConcreteType STRING = Type.concrete(FullyQualifiedName.of("java.lang.String"));
    ConcreteType CHAR_SEQUENCE = Type.concrete(FullyQualifiedName.of("java.lang.CharSequence"));
    ConcreteType BIG_DECIMAL = Type.concrete(FullyQualifiedName.of("java.math.BigDecimal"));
    ConcreteType OBJECT = Type.concrete(FullyQualifiedName.of("java.lang.Object"));
    ConcreteType EXCEPTION = Type.concrete(FullyQualifiedName.of("java.lang.Exception"));
    ConcreteType EXECUTOR = Type.concrete(FullyQualifiedName.of("java.util.concurrent.Executor"));
    ConcreteType SERIALIZABLE = Type.concrete(FullyQualifiedName.of("java.io.Serializable"));
    ConcreteType THREAD = Type.concrete(FullyQualifiedName.of("java.lang.Thread"));
    ConcreteType RUNNABLE = Type.concrete(FullyQualifiedName.of("java.lang.Runnable"));
    TypeBuilder OPTIONAL = new TypeBuilder(FullyQualifiedName.of("java.util.Optional"));
    TypeBuilder COLLECTION = new TypeBuilder(FullyQualifiedName.of("java.util.Collection"));
    TypeBuilder LIST = new TypeBuilder(FullyQualifiedName.of("java.util.List"));
    TypeBuilder SET = new TypeBuilder(FullyQualifiedName.of("java.util.Set"));
    TypeBuilder MAP = new TypeBuilder(FullyQualifiedName.of("java.util.Map"));
    TypeBuilder FUNCTION = new TypeBuilder(FullyQualifiedName.of("java.util.function.Function"));
    TypeBuilder BI_FUNCTION = new TypeBuilder(FullyQualifiedName.of("java.util.function.BiFunction"));
    TypeBuilder COMPLETABLE_FUTURE = new TypeBuilder(FullyQualifiedName.of("java.util.concurrent.CompletableFuture"));
    TypeBuilder COMPARABLE = new TypeBuilder(FullyQualifiedName.of("java.lang.Comparable"));

    /**
     * Method to provide some form of pattern matching (using the <a href="https://en.wikipedia.org/wiki/Visitor_pattern">visitor pattern</a>).
     */
    <R> R match(
            Function<GenericType, R> matchGeneric,
            Function<ConcreteType, R> matchConcrete,
            Function<ArrayType, R> matchArray
    );

    TypeConstructor asTypeConstructor();

    TypeConstructor asTypeConstructorWithPlaceholderFor(TypeParameterName needle);

    boolean contains(TypeParameterName needle);

    Type replaceAllTypeParameterNames(Map<TypeParameterName, TypeParameterName> replacement);

    default Type using(TypeConstructor typeConstructor) {
        return typeConstructor.apply(this);
    }

    default boolean contains(TypeParameterName... needles) {
        return Stream.of(needles).anyMatch(this::contains);
    }

    default boolean contains(TypeParameter... needles) {
        return Stream.of(needles).map(TypeParameter::getName).anyMatch(this::contains);
    }

    default ArrayType array() {
        return array(this);
    }

    default Parameter withName(String name) {
        return Parameter.of(this, name);
    }

    default TypeArgument invariant() {
        return TypeArgument.of(INVARIANT, this);
    }

    default TypeArgument covariant() {
        return TypeArgument.of(COVARIANT, this);
    }

    default TypeArgument contravariant() {
        return TypeArgument.of(CONTRAVARIANT, this);
    }

    static GenericType generic(TypeParameterName name) {
        return GenericType.of(name);
    }

    static ConcreteType concrete(FullyQualifiedName name, List<TypeArgument> typeArguments) {
        return ConcreteType.of(name, typeArguments);
    }

    static ConcreteType concrete(FullyQualifiedName name, TypeArgument... typeArguments) {
        return ConcreteType.of(name, asList(typeArguments));
    }

    static ConcreteType concrete(FullyQualifiedName name) {
        return ConcreteType.of(name, emptyList());
    }

    static ArrayType array(Type type) {
        return ArrayType.of(type);
    }

}
