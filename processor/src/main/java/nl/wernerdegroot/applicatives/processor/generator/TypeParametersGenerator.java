package nl.wernerdegroot.applicatives.processor.generator;

import nl.wernerdegroot.applicatives.processor.domain.TypeParameter;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static nl.wernerdegroot.applicatives.processor.generator.Constants.*;

public class TypeParametersGenerator {

    private final List<TypeParameter> typeParameters = new ArrayList<>();

    public static String generatoreFrom(List<TypeParameter> typeParameters) {
        TypeParametersGenerator typeParametersGenerator = new TypeParametersGenerator();
        typeParametersGenerator.typeParameters.addAll(typeParameters);
        return typeParametersGenerator.generate();
    }

    public boolean isEmpty() {
        return typeParameters.isEmpty();
    }

    public String generate() {
        if (isEmpty()) {
            return "";
        } else {
            return typeParameters.stream().map(TypeParameterGenerator::typeParameter).map(TypeParameterGenerator::generate).collect(joining(SEPARATOR, OPEN_ANGULAR_BRACKET, CLOSE_ANGULAR_BRACKET));
        }
    }

    public interface HasTypeParametersGenerator<This> extends HasThis<This> {

        TypeParametersGenerator getTypeParametersGenerator();

        default This withTypeParameters(List<TypeParameter> typeParameters) {
            getTypeParametersGenerator().typeParameters.addAll(typeParameters);
            return getThis();
        }

        default This withTypeParameters(TypeParameter... typeParameters) {
            return withTypeParameters(asList(typeParameters));
        }

        default This withTypeParameters(TypeParameterName... typeParameters) {
            return withTypeParameters(Stream.of(typeParameters).map(TypeParameterName::asTypeParameter).collect(toList()));
        }
    }
}
