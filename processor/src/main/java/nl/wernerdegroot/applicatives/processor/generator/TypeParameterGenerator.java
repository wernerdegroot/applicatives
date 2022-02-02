package nl.wernerdegroot.applicatives.processor.generator;

import nl.wernerdegroot.applicatives.processor.domain.TypeParameter;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;

import java.util.List;

import static java.util.stream.Collectors.joining;

public class TypeParameterGenerator {

    private final TypeParameter typeParameter;

    public TypeParameterGenerator(TypeParameter typeParameter) {
        this.typeParameter = typeParameter;
    }

    public static TypeParameterGenerator typeParameter(TypeParameter typeParameter) {
        return new TypeParameterGenerator(typeParameter);
    }

    public static String generateFrom(TypeParameter typeParameter) {
        return typeParameter(typeParameter).generate();
    }

    public String generate() {
        List<Type> upperBounds = typeParameter.getUpperBounds();
        if (upperBounds.isEmpty()) {
            return typeParameter.getName().raw();
        } else {
            return String.join(" ", typeParameter.getName().raw(), "extends", upperBounds.stream().map(TypeGenerator::type).map(TypeGenerator::generate).collect(joining(" & ")));
        }
    }
}

