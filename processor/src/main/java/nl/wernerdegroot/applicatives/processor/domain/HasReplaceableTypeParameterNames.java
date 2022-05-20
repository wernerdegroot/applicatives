package nl.wernerdegroot.applicatives.processor.domain;

import java.util.Map;

public interface HasReplaceableTypeParameterNames<T> {

    T replaceTypeParameterNames(Map<TypeParameterName, TypeParameterName> replacements);
}
