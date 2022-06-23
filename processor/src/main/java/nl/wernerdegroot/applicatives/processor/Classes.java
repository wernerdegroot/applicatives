package nl.wernerdegroot.applicatives.processor;

import nl.wernerdegroot.applicatives.processor.domain.FullyQualifiedName;
import nl.wernerdegroot.applicatives.runtime.*;

import java.util.Objects;

public class Classes {

    public static final String COVARIANT_CLASS_NAME = "nl.wernerdegroot.applicatives.runtime.Covariant";
    public static final Class<Covariant> COVARIANT_CLASS = Covariant.class;

    static {
        if (!Objects.equals(COVARIANT_CLASS_NAME, COVARIANT_CLASS.getName())) {
            throw new RuntimeException(String.format("Can't find annotation class %s", COVARIANT_CLASS_NAME));
        }
    }

    public static final String COVARIANT_BUILDER_CANONICAL_NAME = "nl.wernerdegroot.applicatives.runtime.Covariant.Builder";
    public static final String COVARIANT_BUILDER_CLASS_NAME = "nl.wernerdegroot.applicatives.runtime.Covariant$Builder";
    public static final Class<Covariant.Builder> COVARIANT_BUILDER_CLASS = Covariant.Builder.class;

    static {
        if (!Objects.equals(COVARIANT_BUILDER_CLASS_NAME, COVARIANT_BUILDER_CLASS.getName())) {
            throw new RuntimeException(String.format("Can't find annotation class %s", COVARIANT_BUILDER_CANONICAL_NAME));
        }
        if (!Objects.equals(COVARIANT_BUILDER_CANONICAL_NAME, COVARIANT_BUILDER_CLASS.getCanonicalName())) {
            throw new RuntimeException(String.format("Can't find annotation class %s", COVARIANT_BUILDER_CANONICAL_NAME));
        }
    }

    public static final String CONTRAVARIANT_CLASS_NAME = "nl.wernerdegroot.applicatives.runtime.Contravariant";
    public static final Class<Contravariant> CONTRAVARIANT_CLASS = Contravariant.class;

    static {
        if (!Objects.equals(CONTRAVARIANT_CLASS_NAME, CONTRAVARIANT_CLASS.getName())) {
            throw new RuntimeException(String.format("Can't find annotation class %s", CONTRAVARIANT_CLASS_NAME));
        }
    }

    public static final String CONTRAVARIANT_BUILDER_CANONICAL_NAME = "nl.wernerdegroot.applicatives.runtime.Contravariant.Builder";
    public static final String CONTRAVARIANT_BUILDER_CLASS_NAME = "nl.wernerdegroot.applicatives.runtime.Contravariant$Builder";
    public static final Class<Contravariant.Builder> CONTRAVARIANT_BUILDER_CLASS = Contravariant.Builder.class;

    static {
        if (!Objects.equals(CONTRAVARIANT_BUILDER_CLASS_NAME, CONTRAVARIANT_BUILDER_CLASS.getName())) {
            throw new RuntimeException(String.format("Can't find annotation class %s", CONTRAVARIANT_BUILDER_CLASS_NAME));
        }
        if (!Objects.equals(CONTRAVARIANT_BUILDER_CANONICAL_NAME, CONTRAVARIANT_BUILDER_CLASS.getCanonicalName())) {
            throw new RuntimeException(String.format("Can't find annotation class %s", CONTRAVARIANT_BUILDER_CANONICAL_NAME));
        }
    }

    public static final FullyQualifiedName INITIALIZER_FULLY_QUALIFIED_NAME = FullyQualifiedName.of(Initializer.class.getCanonicalName());
    public static final FullyQualifiedName ACCUMULATOR_FULLY_QUALIFIED_NAME = FullyQualifiedName.of(Accumulator.class.getCanonicalName());
    public static final FullyQualifiedName FINALIZER_FULLY_QUALIFIED_NAME = FullyQualifiedName.of(Finalizer.class.getCanonicalName());
}
