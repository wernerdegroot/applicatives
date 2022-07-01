package nl.wernerdegroot.applicatives.processor.generator;

import nl.wernerdegroot.applicatives.processor.domain.FullyQualifiedName;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameter;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import nl.wernerdegroot.applicatives.processor.domain.type.ConcreteType;
import nl.wernerdegroot.applicatives.processor.domain.type.GenericType;
import nl.wernerdegroot.applicatives.processor.generator.ObjectPathGenerator.HasObjectPathGenerator;

import java.util.Optional;

public class ObjectPathOrTypeGenerator {

    private Optional<FullyQualifiedName> optionalConcreteType = Optional.empty();
    private Optional<TypeParameterName> optionalGenericType = Optional.empty();
    private Optional<ObjectPathGenerator> optionalObjectPathGenerator = Optional.empty();

    public String generate() {
        Optional<String> optionalObjectPathAsString = optionalObjectPathGenerator.map(ObjectPathGenerator::generate);
        Optional<String> optionalConcreteTypeAsString = optionalConcreteType.map(FullyQualifiedName::raw);
        Optional<String> optionalGenericTypeAsString = optionalGenericType.map(TypeParameterName::raw);
        return optionalObjectPathAsString
                .map(Optional::of).orElse(optionalConcreteTypeAsString)
                .map(Optional::of).orElse(optionalGenericTypeAsString)
                .orElseThrow(NullPointerException::new);
    }

    public interface HasObjectPathOrTypeGenerator<This> extends HasObjectPathGenerator<This> {

        ObjectPathOrTypeGenerator getObjectPathOrTypeGenerator();

        @Override
        default ObjectPathGenerator getObjectPathGenerator() {
            ObjectPathGenerator objectPathGenerator = getObjectPathOrTypeGenerator().optionalObjectPathGenerator.orElseGet(ObjectPathGenerator::new);
            getObjectPathOrTypeGenerator().optionalObjectPathGenerator = Optional.of(objectPathGenerator);
            return objectPathGenerator;
        }

        default This withType(FullyQualifiedName type) {
            getObjectPathOrTypeGenerator().optionalConcreteType = Optional.of(type);
            return getThis();
        }

        default This withType(TypeParameterName type) {
            getObjectPathOrTypeGenerator().optionalGenericType = Optional.of(type);
            return getThis();
        }

        default This withType(ConcreteType type) {
            return withType(type.getFullyQualifiedName());
        }

        default This withType(GenericType type) {
            return withType(type.getName());
        }

        default This withType(TypeParameter type) {
            return withType(type.getName());
        }
    }
}
