package nl.wernerdegroot.applicatives.processor.generator;

import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import nl.wernerdegroot.applicatives.processor.domain.type.TypeArgument;

import java.util.List;

import static java.util.stream.Collectors.joining;
import static nl.wernerdegroot.applicatives.processor.generator.Constants.*;

public class TypeGenerator {

    private final Type type;

    public TypeGenerator(Type type) {
        this.type = type;
    }

    public static TypeGenerator type(Type type) {
        return new TypeGenerator(type);
    }

    public static String generateFrom(Type type) {
        return new TypeGenerator(type).generate();
    }

    public String generate() {
        return type.match(
                generic -> generic.getName().raw(),

                concrete -> {
                    List<TypeArgument> typeArguments = concrete.getTypeArguments();
                    if (typeArguments.isEmpty()) {
                        return concrete.getFullyQualifiedName().raw();
                    } else {
                        return concrete.getFullyQualifiedName().raw() + OPEN_ANGULAR_BRACKET + typeArguments.stream().map(TypeArgumentGenerator::generateFrom).collect(joining(SEPARATOR)) + CLOSE_ANGULAR_BRACKET;
                    }
                },

                array -> type(array.getType()).generate() + OPEN_SQUARE_BRACKET + CLOSE_SQUARE_BRACKET
        );
    }
}
