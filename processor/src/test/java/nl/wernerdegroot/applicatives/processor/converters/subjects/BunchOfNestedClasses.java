package nl.wernerdegroot.applicatives.processor.converters.subjects;

import nl.wernerdegroot.applicatives.processor.converters.TestAnnotation;

import java.io.Serializable;

public class BunchOfNestedClasses<A> {

    private static class StaticInnerClass<B, C extends Serializable> {

        @TestAnnotation
        class InnerClass {

        }
    }
}

