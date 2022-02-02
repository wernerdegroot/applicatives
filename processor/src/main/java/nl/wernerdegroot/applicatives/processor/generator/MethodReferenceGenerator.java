package nl.wernerdegroot.applicatives.processor.generator;

import nl.wernerdegroot.applicatives.processor.generator.ObjectPathOrTypeGenerator.HasObjectPathOrTypeGenerator;

import static nl.wernerdegroot.applicatives.processor.generator.Constants.DOUBLE_COLON;

public class MethodReferenceGenerator implements HasObjectPathOrTypeGenerator<MethodReferenceGenerator> {

    private ObjectPathOrTypeGenerator objectPathOrTypeGenerator = new ObjectPathOrTypeGenerator();
    private String methodName;

    public static MethodReferenceGenerator methodReference() {
        return new MethodReferenceGenerator();
    }

    @Override
    public ObjectPathOrTypeGenerator getObjectPathOrTypeGenerator() {
        return objectPathOrTypeGenerator;
    }

    @Override
    public MethodReferenceGenerator getThis() {
        return this;
    }

    public MethodReferenceGenerator withMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public String generate() {
        return objectPathOrTypeGenerator.generate() + DOUBLE_COLON + methodName;
    }
}
