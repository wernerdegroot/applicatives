package nl.wernerdegroot.applicatives.processor.converters.subjects;

import nl.wernerdegroot.applicatives.processor.converters.TestAnnotation;
import nl.wernerdegroot.applicatives.runtime.Covariant;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class ComplexMethod<A> {

    public static class StaticInnerClass<B, C extends Runnable> {
    }

    @TestAnnotation
    @Deprecated
    @SuppressWarnings("As many as you can!")
    public strictfp synchronized <D, E extends Number & Comparable<D>> void someMethod(
            int primitive,
            Boolean object,
            char[][] twoDimensionalPrimitiveArray,
            List<?> listOfWildcardsWithoutUpperOrLowerBound,
            Set<? extends Serializable> setOfWildcardsWithUpperBound,
            Collection<? super Number> collectionOfWildcardsWithLowerBound,
            StaticInnerClass<E, Thread> nestedClasses,
            String... varArgs) {
    }
}
