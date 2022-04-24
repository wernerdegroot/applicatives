package nl.wernerdegroot.applicatives.processor.domain;

import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;

import java.util.Objects;

public class CovariantInitializer {

    private final String name;
    private final TypeConstructor inputTypeConstructor;

    public CovariantInitializer(String name, TypeConstructor inputTypeConstructor) {
        this.name = name;
        this.inputTypeConstructor = inputTypeConstructor;
    }

    public static CovariantInitializer of(String name, TypeConstructor inputTypeConstructor) {
        return new CovariantInitializer(name, inputTypeConstructor);
    }

    public String getName() {
        return name;
    }

    public TypeConstructor getInputTypeConstructor() {
        return inputTypeConstructor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CovariantInitializer that = (CovariantInitializer) o;
        return getName().equals(that.getName()) && getInputTypeConstructor().equals(that.getInputTypeConstructor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getInputTypeConstructor());
    }

    @Override
    public String toString() {
        return "CovariantInitializer{" +
                "name='" + name + '\'' +
                ", inputTypeConstructor=" + inputTypeConstructor +
                '}';
    }
}
