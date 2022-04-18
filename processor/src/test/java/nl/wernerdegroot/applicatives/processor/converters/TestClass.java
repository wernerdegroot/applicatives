package nl.wernerdegroot.applicatives.processor.converters;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class TestClass<C> {

    // This static inner class doesn't know about the type parameters of `TestClass`, because it is static.
    public static class StaticInnerClass<B, C extends Runnable> {

        // This non-static inner class (and everything in it) has access to the type parameters of `StaticInnerClass`.
        public class InnerClass<A, C extends Serializable> {

            // This method has access to the type parameters of `StaticInnerClass` and `InnerClass` both.
            @TestAnnotation
            @Deprecated
            @SuppressWarnings(value = "Stuff to suppress")
            public strictfp synchronized <A, B extends Number & Comparable<A>> void someMethod(
                    int primitive,
                    Boolean object,
                    char[][] twoDimensionalPrimitiveArray,
                    List<?> listOfWildcardsWithoutUpperOrLowerBound,
                    Set<? extends Serializable> setOfWildcardsWithUpperBound,
                    Collection<? super Number> collectionOfWildcardsWithLowerBound,
                    StaticInnerClass<B, Thread> nestedClasses) {
            }
        }
    }
}
