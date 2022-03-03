package nl.wernerdegroot.applicatives.processor.generator;

import nl.wernerdegroot.applicatives.processor.domain.type.TypeArgument;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import static nl.wernerdegroot.applicatives.processor.generator.Constants.*;

public class TypeArgumentGenerator {

    private final TypeArgument typeArgument;

    public TypeArgumentGenerator(TypeArgument typeArgument) {
        this.typeArgument = typeArgument;
    }

    public static String generateFrom(TypeArgument typeArgument) {
        return new TypeArgumentGenerator(typeArgument).generate();
    }

    public String generate() {
        String generatedType = TypeGenerator.generateFrom(typeArgument.getType());
        switch (typeArgument.getVariance()) {
            case INVARIANT:
                return generatedType;
            case COVARIANT:
                return String.join(SPACE, QUESTION_MARK, EXTENDS, generatedType);
            case CONTRAVARIANT:
                return String.join(SPACE, QUESTION_MARK, SUPER, generatedType);
            default:
                throw new NotImplementedException();
        }
    }
}
