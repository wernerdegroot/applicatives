package nl.wernerdegroot.applicatives.processor.generator;

import nl.wernerdegroot.applicatives.processor.domain.containing.Containing;
import nl.wernerdegroot.applicatives.processor.domain.containing.ContainingClass;

import java.util.Optional;

public class ContainingClassGenerator {

    private final ContainingClass containingClass;

    public ContainingClassGenerator(ContainingClass containingClass) {
        this.containingClass = containingClass;
    }

    public static String generateFrom(ContainingClass containingClass) {
        return new ContainingClassGenerator(containingClass).generate();
    }

    public String generate() {
        return containingToString(containingClass);
    }

    private String containingToString(Containing containing) {
        return containing.match(
                p -> p.getPackageName().raw(),
                c -> {
                    String parentAsString = containingToString(c.getParent());
                    String classNameAsString = c.getClassName().raw();
                    String typeParametersAsString = Optional.of(c.getTypeParameters())
                            .filter(typeParameters -> !typeParameters.isEmpty())
                            .map(TypeParametersGenerator::generatoreFrom)
                            .orElse("");
                    return parentAsString + "." + classNameAsString + typeParametersAsString;
                }
        );
    }
}
