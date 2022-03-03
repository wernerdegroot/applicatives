package nl.wernerdegroot.applicatives.processor.generator;

import nl.wernerdegroot.applicatives.processor.domain.type.TypeArgument;
import nl.wernerdegroot.applicatives.processor.generator.ObjectPathOrTypeGenerator.HasObjectPathOrTypeGenerator;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static nl.wernerdegroot.applicatives.processor.generator.Constants.*;

public class MethodCallGenerator implements HasObjectPathOrTypeGenerator<MethodCallGenerator> {

    private ObjectPathOrTypeGenerator objectPathOrTypeGenerator = new ObjectPathOrTypeGenerator();
    private List<TypeArgument> typeArguments = new ArrayList<>();
    private String methodName;
    private List<String> arguments = new ArrayList<>();

    public static MethodCallGenerator methodCall() {
        return new MethodCallGenerator();
    }

    @Override
    public ObjectPathOrTypeGenerator getObjectPathOrTypeGenerator() {
        return objectPathOrTypeGenerator;
    }

    @Override
    public MethodCallGenerator getThis() {
        return this;
    }

    public MethodCallGenerator withTypeArguments(List<TypeArgument> typeArguments) {
        this.typeArguments.addAll(typeArguments);
        return this;
    }

    public MethodCallGenerator withTypeArguments(TypeArgument... typeArguments) {
        return withTypeArguments(asList(typeArguments));
    }

    public MethodCallGenerator withMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public MethodCallGenerator withArguments(List<String> arguments) {
        this.arguments.addAll(arguments);
        return this;
    }

    public MethodCallGenerator withArguments(String... arguments) {
        return withArguments(asList(arguments));
    }

    public String generate() {
        return objectPathOrTypeGenerator.generate() + PERIOD + generateOptionalTypeArguments() + methodName + arguments.stream().collect(joining(SEPARATOR, OPEN_PARENTHESIS, CLOSE_PARENTHESIS));
    }

    private String generateOptionalTypeArguments() {
        if (typeArguments.isEmpty()) {
            return "";
        } else {
            return typeArguments.stream().map(TypeArgumentGenerator::generateFrom).collect(joining(SEPARATOR, OPEN_ANGULAR_BRACKET, CLOSE_ANGULAR_BRACKET));
        }
    }
}
