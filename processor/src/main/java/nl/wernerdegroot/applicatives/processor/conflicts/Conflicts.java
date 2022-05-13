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
     * which will result in less input type constructor arguments that can
     * cause conflicts, the algorithms in this package will assume the worst
     * and always try to prevent conflicts will all 26 possible input type
     * constructor arguments (whether they are used or not).
     */
    public static final int NUMBER_OF_PARAMETER_TYPE_CONSTRUCTOR_ARGUMENTS = 26;

    public static final List<TypeParameterName> PARAMETER_TYPE_CONSTRUCTOR_ARGUMENT_NAMES = IntStream
            .range(0, NUMBER_OF_PARAMETER_TYPE_CONSTRUCTOR_ARGUMENTS)
            .mapToObj(Conflicts::parameterTypeConstructorArgumentName)
            .collect(toList());

    public static final List<TypeParameter> PARAMETER_TYPE_CONSTRUCTOR_ARGUMENTS = PARAMETER_TYPE_CONSTRUCTOR_ARGUMENT_NAMES
            .stream()
            .map(TypeParameter::of)
            .collect(toList());

    public static final List<String> INPUT_PARAMETER_NAMES = IntStream
            .range(0, NUMBER_OF_PARAMETER_TYPE_CONSTRUCTOR_ARGUMENTS)
            .mapToObj(Conflicts::inputParameterName)
            .collect(toList());

    public static final String INPUT_TYPE_CONSTRUCTOR_ARGUMENT_PREFIX = "P";

    public static final TypeParameterName RETURN_TYPE_CONSTRUCTOR_ARGUMENT_NAME = TypeParameterName.of("R");

    public static final TypeParameter RETURN_TYPE_CONSTRUCTOR_ARGUMENT = TypeParameter.of(RETURN_TYPE_CONSTRUCTOR_ARGUMENT_NAME);

    public static final String SELF_PARAMETER_NAME = "self";

    public static final String COMBINATOR_PARAMETER_NAME = "fn";

    public static final String MAX_TUPLE_SIZE_PARAMETER_NAME = "maxSize";

    public static final String CLASS_TYPE_PARAMETER_NAME_PREFIX = "C";

    public static String inputParameterName(int i) {
        return ORDINALS.get(i);
    }

    public static TypeParameterName parameterTypeConstructorArgumentName(int i) {
        String name = INPUT_TYPE_CONSTRUCTOR_ARGUMENT_PREFIX + (i + 1);
        return TypeParameterName.of(name);
    }

    public static TypeParameterName alternativeClassTypeParameterName(int i) {
        String name = CLASS_TYPE_PARAMETER_NAME_PREFIX + (i + 1);
        return TypeParameterName.of(name);
    }
}
