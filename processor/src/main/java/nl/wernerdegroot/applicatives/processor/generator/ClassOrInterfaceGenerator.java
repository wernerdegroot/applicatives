package nl.wernerdegroot.applicatives.processor.generator;

import nl.wernerdegroot.applicatives.processor.generator.BodyGenerator.HasBodyGenerator;
import nl.wernerdegroot.applicatives.processor.generator.ModifiersGenerator.HasModifiersGenerator;
import nl.wernerdegroot.applicatives.processor.generator.TypeParametersGenerator.HasTypeParametersGenerator;

import java.util.ArrayList;
import java.util.List;

import static nl.wernerdegroot.applicatives.processor.generator.Constants.*;

public class ClassOrInterfaceGenerator implements HasModifiersGenerator<ClassOrInterfaceGenerator>, HasTypeParametersGenerator<ClassOrInterfaceGenerator>, HasBodyGenerator<ClassOrInterfaceGenerator> {

    private ModifiersGenerator modifiersGenerator = new ModifiersGenerator();
    private ClassType type;
    private String name;
    private TypeParametersGenerator typeParametersGenerator = new TypeParametersGenerator();
    private BodyGenerator bodyGenerator = new BodyGenerator();

    public static ClassOrInterfaceGenerator classOrInterface() {
        return new ClassOrInterfaceGenerator();
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
    public BodyGenerator getBodyGenerator() {
        return bodyGenerator;
    }

    @Override
    public ClassOrInterfaceGenerator getThis() {
        return this;
    }

    public ClassOrInterfaceGenerator asClass() {
        this.type = ClassType.CLASS;
        return this;
    }

    public ClassOrInterfaceGenerator asInterface() {
        this.type = ClassType.INTERFACE;
        return this;
    }

    public ClassOrInterfaceGenerator withName(String name) {
        this.name = name;
        return this;
    }

    public List<String> lines() {
        List<String> result = new ArrayList<>();
        result.add(
                String.join(
                        SPACE,
                        modifiersGenerator.isEmpty() ? type.toString() : modifiersGenerator.generate() + SPACE + type.toString(),
                        name + typeParametersGenerator.generate(),
                        OPEN_BRACE
                )
        );
        result.add(EMPTY_LINE);
        result.addAll(bodyGenerator.indent().lines());
        result.add(EMPTY_LINE);
        result.add(CLOSE_BRACE);
        return result;
    }
}
