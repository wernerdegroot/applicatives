package nl.wernerdegroot.applicatives.processor;

import nl.wernerdegroot.applicatives.processor.domain.FullyQualifiedName;
import nl.wernerdegroot.applicatives.runtime.*;

import java.util.Objects;

public class Classes {

    public static final String COVARIANT_CLASS_NAME = "nl.wernerdegroot.applicatives.runtime.Covariant";
    public static final Class<?> COVARIANT_CLASS;
    public static final FullyQualifiedName COVARIANT_FULLY_QUALIFIED_NAME = FullyQualifiedName.of(Covariant.class.getCanonicalName());

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

    public static final FullyQualifiedName INITIALIZER_FULLY_QUALIFIED_NAME = FullyQualifiedName.of(Initializer.class.getCanonicalName());
    public static final FullyQualifiedName ACCUMULATOR_FULLY_QUALIFIED_NAME = FullyQualifiedName.of(Accumulator.class.getCanonicalName());
    public static final FullyQualifiedName FINALIZER_FULLY_QUALIFIED_NAME = FullyQualifiedName.of(Finalizer.class.getCanonicalName());

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

    public static final String CONTRAVARIANT_CLASS_NAME = "nl.wernerdegroot.applicatives.runtime.Contravariant";
    public static final Class<?> CONTRAVARIANT_CLASS;
    public static final FullyQualifiedName CONTRAVARIANT_FULLY_QUALIFIED_NAME = FullyQualifiedName.of(Contravariant.class.getCanonicalName());

    static {
        try {
            CONTRAVARIANT_CLASS = Class.forName(CONTRAVARIANT_CLASS_NAME);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(String.format("Can't find annotation class %s", CONTRAVARIANT_CLASS_NAME), e);
        }
    }

    public static final String CONTRAVARIANT_BUILDER_CANONICAL_NAME = "nl.wernerdegroot.applicatives.runtime.Contravariant.Builder";
    public static final String CONTRAVARIANT_BUILDER_CLASS_NAME = "nl.wernerdegroot.applicatives.runtime.Contravariant$Builder";
    public static final Class<?> CONTRAVARIANT_BUILDER_CLASS;

    static {
        try {
            CONTRAVARIANT_BUILDER_CLASS = Class.forName(CONTRAVARIANT_BUILDER_CLASS_NAME);
            if (!Objects.equals(CONTRAVARIANT_BUILDER_CANONICAL_NAME, CONTRAVARIANT_BUILDER_CLASS.getCanonicalName())) {
                throw new RuntimeException(String.format("Can't find annotation class %s", CONTRAVARIANT_BUILDER_CANONICAL_NAME));
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(String.format("Can't find annotation class %s", CONTRAVARIANT_BUILDER_CLASS_NAME), e);
        }
    }
}
