package nl.wernerdegroot.applicatives.processor.converters;

import nl.wernerdegroot.applicatives.processor.domain.Modifier;

import java.util.Objects;

public class ModifierConverter {

    /**
     * Converts a class from the world of {@link javax.lang.model} to the
     * world of {@link nl.wernerdegroot.applicatives.processor.domain}.
     *
     * @param modifier A modifier
     *
     * @return {@link Modifier Modifier}
     */
    public static Modifier toDomain(javax.lang.model.element.Modifier modifier) {
        Objects.requireNonNull(modifier);

        return Modifier.fromString(modifier.toString());
    }
}
