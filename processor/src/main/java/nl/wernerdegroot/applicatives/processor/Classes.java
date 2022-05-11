package nl.wernerdegroot.applicatives.processor;

import nl.wernerdegroot.applicatives.processor.domain.FullyQualifiedName;
import nl.wernerdegroot.applicatives.runtime.Accumulator;
import nl.wernerdegroot.applicatives.runtime.Covariant;
import nl.wernerdegroot.applicatives.runtime.Initializer;

import java.util.Objects;

public class Classes {

    public static final String COVARIANT_CLASS_NAME = "nl.wernerdegroot.applicatives.runtime.Covariant";
    public static final Class<?> COVARIANT_CLASS;
    public static final FullyQualifiedName COVARIANT = FullyQualifiedName.of(Covariant.class.getCanonicalName());

    static {
        try {
            COVARIANT_CLASS = Class.forName(COVARIANT_CLASS_NAME);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(String.format("Can't find annotation class %s", COVARIANT_CLASS_NAME), e);
        }
    }

    public static final String COVARIANT_BUILDER_CANONICAL_NAME = "nl.wernerdegroot.applicatives.runtime.Covariant.Builder";
    public static final String COVARIANT_BUILDER_CLASS_NAME = "nl.wernerdegroot.applicatives.runtime.Covariant$Builder";
    public static final Class<?> COVARIANT_BUILDER_CLASS;

    public static final FullyQualifiedName INITIALIZER = FullyQualifiedName.of(Initializer.class.getCanonicalName());
    public static final FullyQualifiedName ACCUMULATOR = FullyQualifiedName.of(Accumulator.class.getCanonicalName());

    static {
        try {
            COVARIANT_BUILDER_CLASS = Class.forName(COVARIANT_BUILDER_CLASS_NAME);
            if (!Objects.equals(COVARIANT_BUILDER_CANONICAL_NAME, COVARIANT_BUILDER_CLASS.getCanonicalName())) {
                throw new RuntimeException(String.format("Can't find annotation class %s", COVARIANT_BUILDER_CANONICAL_NAME));
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(String.format("Can't find annotation class %s", COVARIANT_BUILDER_CLASS_NAME), e);
        }
    }
}
