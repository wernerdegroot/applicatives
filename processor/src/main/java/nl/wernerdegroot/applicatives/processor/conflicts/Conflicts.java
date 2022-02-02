package nl.wernerdegroot.applicatives.processor.conflicts;

import nl.wernerdegroot.applicatives.processor.domain.TypeParameter;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static nl.wernerdegroot.applicatives.processor.Ordinals.ORDINALS;

/**
 * Constants/pure functions used while resolving conflicts.
 */
public final class Conflicts {

    private Conflicts() {
    }

    /**
     * Although a programmer may choose to generate <i>less</i> overloads,
     * which will result in less primary method type parameters that can
     * cause conflicts, the algorithms in this package will assume the worst
     * and always try to prevent conflicts will all 26 possible primary
     * method type parameters (whether they are used or not).
     */
    public static final int NUMBER_OF_PRIMARY_METHOD_TYPE_PARAMETERS = 26;

    public static final List<TypeParameterName> PRIMARY_METHOD_TYPE_PARAMETER_NAMES = IntStream
            .range(0, NUMBER_OF_PRIMARY_METHOD_TYPE_PARAMETERS)
            .mapToObj(Conflicts::primaryMethodTypeParameterName)
            .collect(toList());

    public static final List<TypeParameter> PRIMARY_METHOD_TYPE_PARAMETERS = PRIMARY_METHOD_TYPE_PARAMETER_NAMES
            .stream()
            .map(TypeParameter::of)
            .collect(toList());

    public static final List<String> PRIMARY_PARAMETER_NAMES = IntStream
            .range(0, NUMBER_OF_PRIMARY_METHOD_TYPE_PARAMETERS)
            .mapToObj(Conflicts::primaryParameterName)
            .collect(toList());

    public static final String PRIMARY_METHOD_TYPE_PARAMETER_PREFIX = "P";

    public static final TypeParameterName RESULT_TYPE_PARAMETER_NAME = TypeParameterName.of("R");

    public static final TypeParameter RESULT_TYPE_PARAMETER = TypeParameter.of(RESULT_TYPE_PARAMETER_NAME);

    public static final String SELF_PARAMETER_NAME = "self";

    public static final String COMBINATOR_PARAMETER_NAME = "fn";

    public static final String MAX_TUPLE_SIZE_PARAMETER_NAME = "maxSize";

    public static final String SECONDARY_METHOD_TYPE_PARAMETER_NAME_PREFIX = "M";

    public static final String CLASS_TYPE_PARAMETER_NAME_PREFIX = "C";

    public static final String SECONDARY_PARAMETER_NAME_PREFIX = "s";

    public static String primaryParameterName(int i) {
        return ORDINALS.get(i);
    }

    public static TypeParameterName primaryMethodTypeParameterName(int i) {
        String name = PRIMARY_METHOD_TYPE_PARAMETER_PREFIX + (i + 1);
        return TypeParameterName.of(name);
    }

    public static TypeParameterName alternativeSecondaryMethodTypeParameterName(int i) {
        String name = SECONDARY_METHOD_TYPE_PARAMETER_NAME_PREFIX + (i + 1);
        return TypeParameterName.of(name);
    }

    public static TypeParameterName alternativeClassTypeParameterName(int i) {
        String name = CLASS_TYPE_PARAMETER_NAME_PREFIX + (i + 1);
        return TypeParameterName.of(name);
    }

    public static String alternativeSecondaryMethodParameterName(int i) {
        return SECONDARY_PARAMETER_NAME_PREFIX + (i + 1);
    }
}
