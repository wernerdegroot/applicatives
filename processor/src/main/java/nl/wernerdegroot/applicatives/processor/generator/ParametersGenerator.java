package nl.wernerdegroot.applicatives.processor.generator;

import nl.wernerdegroot.applicatives.processor.domain.Parameter;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static nl.wernerdegroot.applicatives.processor.generator.Constants.*;

public class ParametersGenerator {

    private List<Parameter> parameters = new ArrayList<>();

    public static Standalone parameters() {
        return new Standalone();
    }

    public String generate() {
        return parameters
                .stream()
                .map(ParameterGenerator::generateFrom)
                .collect(joining(SEPARATOR, OPEN_PARENTHESIS, CLOSE_PARENTHESIS));
    }

    public static class Standalone implements HasParametersGenerator<Standalone> {

        private final ParametersGenerator parametersGenerator = new ParametersGenerator();

        @Override
        public Standalone getThis() {
            return this;
        }

        @Override
        public ParametersGenerator getParametersGenerator() {
            return parametersGenerator;
        }

        public List<Parameter> unwrap() {
            return parametersGenerator.parameters;
        }
    }

    public interface HasParametersGenerator<This> extends HasThis<This> {

        ParametersGenerator getParametersGenerator();

        default This withParameters(Iterable<Parameter> parameters) {
            parameters.forEach(getParametersGenerator().parameters::add);
            return getThis();
        }

        default This withParameters(Parameter... parameters) {
            return withParameters(asList(parameters));
        }

        default This withParameter(Type type, String name) {
            return withParameters(Parameter.of(type, name));
        }

        default TypesAndNamesBuilder<This> withParameterTypes(List<Type> types) {
            return new TypesAndNamesBuilder<>(this, types);
        }

        default TypesAndNamesBuilder<This> withParameterTypes(Type... types) {
            return withParameterTypes(asList(types));
        }

        class TypesAndNamesBuilder<This> {

            private final HasParametersGenerator<This> hasParametersGenerator;
            private final List<Type> types;

            public TypesAndNamesBuilder(HasParametersGenerator<This> hasParametersGenerator, List<Type> types) {
                this.hasParametersGenerator = hasParametersGenerator;
                this.types = types;
            }

            public This andParameterNames(List<String> names) {
                return hasParametersGenerator.withParameters(
                        IntStream.range(0, Math.min(types.size(), names.size()))
                                .mapToObj(i -> Parameter.of(types.get(i), names.get(i)))
                                .collect(toList())
                );
            }

            public This andParameterNames(String... names) {
                return andParameterNames(asList(names));
            }
        }
    }
}
