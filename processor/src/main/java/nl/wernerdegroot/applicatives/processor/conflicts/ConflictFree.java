package nl.wernerdegroot.applicatives.processor.conflicts;

import nl.wernerdegroot.applicatives.processor.domain.TypeParameter;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * The product of the conflict resolution algorithm. The fields in
 * this class are guaranteed to be free of conflicts and can be used
 * directly and without worry in code generation.
 */
public class ConflictFree {

    // For an explanation of these fields, check `README.md` in the `domain` package.

    private final List<TypeParameter> classTypeParameters;
    private final TypeConstructor accumulationTypeConstructor;
    private final TypeConstructor permissiveAccumulationTypeConstructor;
    private final TypeConstructor inputTypeConstructor;
    private final Optional<TypeConstructor> optionalResultTypeConstructor;

    public ConflictFree(List<TypeParameter> classTypeParameters, TypeConstructor accumulationTypeConstructor, TypeConstructor permissiveAccumulationTypeConstructor, TypeConstructor inputTypeConstructor, Optional<TypeConstructor> optionalResultTypeConstructor) {
        this.classTypeParameters = classTypeParameters;
        this.accumulationTypeConstructor = accumulationTypeConstructor;
        this.permissiveAccumulationTypeConstructor = permissiveAccumulationTypeConstructor;
        this.inputTypeConstructor = inputTypeConstructor;
        this.optionalResultTypeConstructor = optionalResultTypeConstructor;
    }

    public static ConflictFree of(List<TypeParameter> classTypeParameters, TypeConstructor accumulationTypeConstructor, TypeConstructor permissiveAccumulationTypeConstructor, TypeConstructor inputTypeConstructor, Optional<TypeConstructor> optionalResultTypeConstructor) {
        return new ConflictFree(classTypeParameters, accumulationTypeConstructor, permissiveAccumulationTypeConstructor, inputTypeConstructor, optionalResultTypeConstructor);
    }

    public List<TypeParameter> getClassTypeParameters() {
        return classTypeParameters;
    }

    public TypeConstructor getAccumulationTypeConstructor() {
        return accumulationTypeConstructor;
    }

    public TypeConstructor getPermissiveAccumulationTypeConstructor() {
        return permissiveAccumulationTypeConstructor;
    }

    public TypeConstructor getInputTypeConstructor() {
        return inputTypeConstructor;
    }

    public Optional<TypeConstructor> getOptionalResultTypeConstructor() {
        return optionalResultTypeConstructor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConflictFree that = (ConflictFree) o;
        return getClassTypeParameters().equals(that.getClassTypeParameters()) && getAccumulationTypeConstructor().equals(that.getAccumulationTypeConstructor()) && getPermissiveAccumulationTypeConstructor().equals(that.getPermissiveAccumulationTypeConstructor()) && getInputTypeConstructor().equals(that.getInputTypeConstructor()) && getOptionalResultTypeConstructor().equals(that.getOptionalResultTypeConstructor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClassTypeParameters(), getAccumulationTypeConstructor(), getPermissiveAccumulationTypeConstructor(), getInputTypeConstructor(), getOptionalResultTypeConstructor());
    }

    @Override
    public String toString() {
        return "ConflictFree{" +
                "classTypeParameters=" + classTypeParameters +
                ", accumulationTypeConstructor=" + accumulationTypeConstructor +
                ", permissiveAccumulationTypeConstructor=" + permissiveAccumulationTypeConstructor +
                ", inputTypeConstructor=" + inputTypeConstructor +
                ", optionalResultTypeConstructor=" + optionalResultTypeConstructor +
                '}';
    }
}
