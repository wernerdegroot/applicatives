package nl.wernerdegroot.applicatives.processor.generator;

import nl.wernerdegroot.applicatives.processor.domain.Parameter;

import static nl.wernerdegroot.applicatives.processor.generator.Constants.SPACE;

public class ParameterGenerator {

    private final Parameter parameter;

    public ParameterGenerator(Parameter parameter) {
        this.parameter = parameter;
    }

    public static ParameterGenerator parameter(Parameter parameter) {
        return new ParameterGenerator(parameter);
    }

    public static String generateFrom(Parameter parameter) {
        return parameter(parameter).generate();
    }

    public String generate() {
        String type = TypeGenerator.generateFrom(parameter.getType());
        String name = parameter.getName();
        return String.join(SPACE, type, name);
    }
}
