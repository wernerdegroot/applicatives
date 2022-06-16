package nl.wernerdegroot.applicatives.processor.validation;

import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.*;

public enum Variance {
    COVARIANT {
        @Override
        public List<Type> getExpectedCombinators(TypeParameterName leftInputTypeParameterName, TypeParameterName rightInputTypeParameterName, TypeParameterName returnTypeParameterName) {
            return singletonList(
                    BI_FUNCTION.with(leftInputTypeParameterName.asType().contravariant(), rightInputTypeParameterName.asType().contravariant(), returnTypeParameterName.asType().covariant())
            );
        }
    },
    CONTRAVARIANT {
        @Override
        public List<Type> getExpectedCombinators(TypeParameterName leftInputTypeParameterName, TypeParameterName rightInputTypeParameterName, TypeParameterName returnTypeParameterName) {
            return asList(
                    FUNCTION.with(returnTypeParameterName.asType().contravariant(), TUPLE2.with(leftInputTypeParameterName.asType().covariant(), rightInputTypeParameterName.asType().covariant()).covariant())
            );
        }
    };

    public abstract List<Type> getExpectedCombinators(TypeParameterName leftInputTypeParameterName, TypeParameterName rightInputTypeParameterName, TypeParameterName returnTypeParameterName);
}
