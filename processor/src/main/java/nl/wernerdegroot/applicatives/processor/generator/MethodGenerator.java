package nl.wernerdegroot.applicatives.processor.generator;

import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import nl.wernerdegroot.applicatives.processor.generator.BodyGenerator.HasBodyGenerator;
import nl.wernerdegroot.applicatives.processor.generator.ModifiersGenerator.HasModifiersGenerator;
import nl.wernerdegroot.applicatives.processor.generator.ParametersGenerator.HasParametersGenerator;
import nl.wernerdegroot.applicatives.processor.generator.TypeParametersGenerator.HasTypeParametersGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static nl.wernerdegroot.applicatives.processor.generator.Constants.*;
import static nl.wernerdegroot.applicatives.processor.generator.TypeGenerator.generateFrom;

public class MethodGenerator implements HasModifiersGenerator<MethodGenerator>, HasTypeParametersGenerator<MethodGenerator>, HasParametersGenerator<MethodGenerator>, HasBodyGenerator<MethodGenerator> {

    private ModifiersGenerator modifiersGenerator = new ModifiersGenerator();
    private TypeParametersGenerator typeParametersGenerator = new TypeParametersGenerator();
    private Optional<Type> optionalReturnType = Optional.empty();
    private String name;
    private ParametersGenerator parametersGenerator = new ParametersGenerator();
    private Optional<BodyGenerator> optionalBodyGenerator = Optional.empty();

    public static MethodGenerator method() {
        return new MethodGenerator();
    }

    @Override
    public ModifiersGenerator getModifiersGenerator() {
        return modifiersGenerator;
    }

    @Override
    public TypeParametersGenerator getTypeParametersGenerator() {
        return typeParametersGenerator;
    }

    @Override
    public ParametersGenerator getParametersGenerator() {
        return parametersGenerator;
    }

    @Override
    public BodyGenerator getBodyGenerator() {
        BodyGenerator bodyGenerator = optionalBodyGenerator.orElseGet(BodyGenerator::new);
        optionalBodyGenerator = Optional.of(bodyGenerator);
        return bodyGenerator;
    }

    @Override
    public MethodGenerator getThis() {
        return this;
    }

    public MethodGenerator withReturnType(Type returnType) {
        this.optionalReturnType = Optional.of(returnType);
        return this;
    }

    public MethodGenerator withName(String name) {
        this.name = name;
        return this;
    }

    public MethodGenerator withReturnStatement(List<String> lines) {
        List<String> toAdd = new ArrayList<>(lines);
        int firstElement = 0;
        int lastElement = toAdd.size() - 1;
        toAdd.set(firstElement, RETURN + SPACE + toAdd.get(firstElement));
        toAdd.set(lastElement, toAdd.get(lastElement) + SEMICOLON);
        return withBody(toAdd);
    }

    public MethodGenerator withReturnStatement(String... lines) {
        return withReturnStatement(asList(lines));
    }

    public List<String> lines() {
        List<String> components = new ArrayList<>();
        if (!modifiersGenerator.isEmpty()) {
            components.add(modifiersGenerator.generate());
        }
        if (!typeParametersGenerator.isEmpty()) {
            components.add(typeParametersGenerator.generate());
        }
        components.add(optionalReturnType.map(TypeGenerator::generateFrom).orElse(VOID));
        components.add(name + parametersGenerator.generate());
        String methodDeclaration = components.stream().collect(joining(SPACE));

        return optionalBodyGenerator
                .map(bodyGenerator -> {
                    List<String> result = new ArrayList<>();
                    result.add(methodDeclaration + SPACE + OPEN_BRACE);
                    result.addAll(bodyGenerator.indent().lines());
                    result.add(CLOSE_BRACE);
                    return result;
                })
                .orElse(asList(methodDeclaration + SEMICOLON));
    }
}
