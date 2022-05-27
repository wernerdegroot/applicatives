package nl.wernerdegroot.applicatives.processor.domain;

public interface MayContainReferenceToTypeParameter {

    boolean referencesTypeParameter(TypeParameterName typeParameterName);
}
