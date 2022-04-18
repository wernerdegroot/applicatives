package nl.wernerdegroot.applicatives.processor.domain;

import java.util.List;
import java.util.Objects;

public class TemplateClass {
    private final List<TypeParameter> typeParameters;

    public TemplateClass(List<TypeParameter> typeParameters) {
        this.typeParameters = typeParameters;
    }

    public static TemplateClass of(List<TypeParameter> typeParameters) {
        return new TemplateClass(typeParameters);
    }

    public List<TypeParameter> getTypeParameters() {
        return typeParameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TemplateClass that = (TemplateClass) o;
        return getTypeParameters().equals(that.getTypeParameters());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTypeParameters());
    }

    @Override
    public String toString() {
        return "TemplateClass{" +
                "typeParameters=" + typeParameters +
                '}';
    }
}
