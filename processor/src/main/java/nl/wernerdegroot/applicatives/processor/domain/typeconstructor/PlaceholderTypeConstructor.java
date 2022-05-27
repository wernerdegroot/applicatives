package nl.wernerdegroot.applicatives.processor.domain.typeconstructor;

import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;

import java.util.Map;

public class PlaceholderTypeConstructor implements TypeConstructor {

    @Override
    public PlaceholderTypeConstructor replaceTypeParameterNames(Map<TypeParameterName, TypeParameterName> replacement) {
        return this;
    }

    @Override
    public boolean referencesTypeParameter(TypeParameterName typeParameterName) {
        return false;
    }

    @Override
    public boolean canAccept(TypeConstructor that) {
        return this.equals(that);
    }

    @Override
    public Type apply(Type toApplyTo) {
        return toApplyTo;
    }

    @Override
    public boolean equals(Object that) {
        return that instanceof PlaceholderTypeConstructor;
    }

    @Override
    public int hashCode() {
        return 7;
    }

    @Override
    public String toString() {
        return "PlaceholderTypeConstructor{}";
    }
}
