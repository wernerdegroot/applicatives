package nl.wernerdegroot.applicatives.processor.domain;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

public enum Modifier {
    PUBLIC("public"),
    PROTECTED("protected"),
    PRIVATE("private"),
    ABSTRACT("abstract"),
    DEFAULT("default"),
    STATIC("static"),
    SEALED("sealed"),
    NON_SEALED("non-sealed"),
    FINAL("final"),
    TRANSIENT("transient"),
    VOLATILE("volatile"),
    SYNCHRONIZED("synchronized"),
    NATIVE("native"),
    STRICTFP("strictfp");

    public static final Set<Modifier> ACCESS_MODIFIERS = EnumSet.of(PUBLIC, PRIVATE, PROTECTED);

    private final String stringValue;

    Modifier(String stringValue) {
        this.stringValue = stringValue;
    }

    public static Modifier fromString(String stringValue) {
        for (Modifier modifier : values()) {
            if (Objects.equals(modifier.stringValue, stringValue)) {
                return modifier;
            }
        }
        throw new IllegalArgumentException(String.format("No modifier found with value \"%s\"", stringValue));
    }

    @Override
    public String toString() {
        return stringValue;
    }
}
