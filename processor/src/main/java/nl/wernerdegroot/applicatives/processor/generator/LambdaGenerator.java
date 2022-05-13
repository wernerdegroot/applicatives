package nl.wernerdegroot.applicatives.processor.generator;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static nl.wernerdegroot.applicatives.processor.generator.Constants.*;

public class LambdaGenerator {

    private List<String> parameterNames;
    private String expression;

    public static LambdaGenerator lambda() {
        return new LambdaGenerator();
    }

    public LambdaGenerator withParameterNames(List<String> parameterNames) {
        this.parameterNames = parameterNames;
        return this;
    }

    public LambdaGenerator withParameterNames(String... parameterNames) {
        return withParameterNames(asList(parameterNames));
    }

    public LambdaGenerator withExpression(String expression) {
        this.expression = expression;
        return this;
    }

    public List<String> multiline() {
        return asList(
                generateArgumentList() + SPACE + ARROW,
                INDENT + INDENT + expression
        );
    }

    public String generate() {
        return generateArgumentList() + SPACE + ARROW + SPACE + expression;
    }

    private String generateArgumentList() {
        return parameterNames.stream().collect(joining(SEPARATOR, OPEN_PARENTHESIS, CLOSE_PARENTHESIS));
    }
}
