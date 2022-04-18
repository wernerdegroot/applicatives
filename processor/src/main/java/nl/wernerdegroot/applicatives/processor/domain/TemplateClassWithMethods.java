package nl.wernerdegroot.applicatives.processor.domain;

import java.util.Objects;

public class TemplateClassWithMethods {

    private final TemplateClass templateClass;
    private final AccumulatorMethod accumulatorMethod;

    public TemplateClassWithMethods(TemplateClass templateClass, AccumulatorMethod accumulatorMethod) {
        this.templateClass = templateClass;
        this.accumulatorMethod = accumulatorMethod;
    }

    public static TemplateClassWithMethods of(TemplateClass templateClass, AccumulatorMethod accumulatorMethod) {
        return new TemplateClassWithMethods(templateClass, accumulatorMethod);
    }

    public TemplateClass getTemplateClass() {
        return templateClass;
    }

    public AccumulatorMethod getAccumulatorMethod() {
        return accumulatorMethod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TemplateClassWithMethods that = (TemplateClassWithMethods) o;
        return templateClass.equals(that.templateClass) && accumulatorMethod.equals(that.accumulatorMethod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(templateClass, accumulatorMethod);
    }

    @Override
    public String toString() {
        return "TemplateClassWithMethods{" +
                "templateClass=" + templateClass +
                ", accumulatorMethod=" + accumulatorMethod +
                '}';
    }
}
