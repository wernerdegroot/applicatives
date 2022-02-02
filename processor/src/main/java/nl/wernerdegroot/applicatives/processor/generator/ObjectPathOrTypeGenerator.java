package nl.wernerdegroot.applicatives.processor.generator;

import nl.wernerdegroot.applicatives.processor.domain.FullyQualifiedName;
import nl.wernerdegroot.applicatives.processor.domain.type.ConcreteType;
import nl.wernerdegroot.applicatives.processor.generator.ObjectPathGenerator.HasObjectPathGenerator;

import java.util.Optional;

public class ObjectPathOrTypeGenerator {

    private Optional<FullyQualifiedName> optionalType = Optional.empty();
    private Optional<ObjectPathGenerator> optionalObjectPathGenerator = Optional.empty();

    public String generate() {
        Optional<String> optionalObjectPathAsString = optionalObjectPathGenerator.map(ObjectPathGenerator::generate);
        Optional<String> optionalTypeAsString = optionalType.map(FullyQualifiedName::raw);
        return optionalObjectPathAsString.map(Optional::of).orElse(optionalTypeAsString).orElseThrow(NullPointerException::new);
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
            getObjectPathOrTypeGenerator().optionalType = Optional.of(type);
            return getThis();
        }

        default This withType(ConcreteType type) {
            return withType(type.getFullyQualifiedName());
        }
    }
}
