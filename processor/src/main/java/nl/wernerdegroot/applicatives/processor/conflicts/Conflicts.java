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
    public static final int MAX_NUMBER_OF_PARTICIPANTS = 26;

    public static final List<TypeParameterName> PARTICIPANT_TYPE_PARAMETER_NAMES = IntStream
            .range(0, MAX_NUMBER_OF_PARTICIPANTS)
            .mapToObj(Conflicts::getParticipantTypeParameterName)
            .collect(toList());

    public static final List<TypeParameter> PARTICIPANT_TYPE_PARAMETERS = PARTICIPANT_TYPE_PARAMETER_NAMES
            .stream()
            .map(TypeParameter::of)
            .collect(toList());

    public static final List<String> INPUT_PARAMETER_NAMES = IntStream
            .range(0, MAX_NUMBER_OF_PARTICIPANTS)
            .mapToObj(Conflicts::inputParameterName)
            .collect(toList());

    public static final String PARTICIPANT_TYPE_PARAMETER_NAME_PREFIX = "P";

    public static final TypeParameterName COMPOSITE_TYPE_PARAMETER_NAME = TypeParameterName.of("R");

    public static final TypeParameterName INTERMEDIATE_TYPE_PARAMETER_NAME = TypeParameterName.of("Intermediate");

    public static final TypeParameter COMPOSITE_TYPE_PARAMETER = TypeParameter.of(COMPOSITE_TYPE_PARAMETER_NAME);

    public static final TypeParameter INTERMEDIATE_TYPE_PARAMETER = TypeParameter.of(INTERMEDIATE_TYPE_PARAMETER_NAME);

    public static final String VALUE_PARAMETER_NAME = "value";

    public static final String DECOMPOSITION_PARAMETER_NAME = "decomposition";

    public static final String SELF_PARAMETER_NAME = "self";

    public static final String COMBINATOR_PARAMETER_NAME = "fn";

    public static final String TO_INTERMEDIATE_PARAMETER_NAME = "toIntermediate";

    public static final String EXTRACT_LEFT_PARAMETER_NAME = "extractLeft";

    public static final String EXTRACT_RIGHT_PARAMETER_NAME = "extractRight";

    public static final String MAX_TUPLE_SIZE_PARAMETER_NAME = "maxSize";

    public static final String TUPLE_PARAMETER_NAME = "tuple";

    public static final String ELEMENT_PARAMETER_NAME = "element";

    public static final String CLASS_TYPE_PARAMETER_NAME_PREFIX = "C";

    public static String inputParameterName(int i) {
        return ORDINALS.get(i);
    }

    public static TypeParameterName getParticipantTypeParameterName(int i) {
        String name = PARTICIPANT_TYPE_PARAMETER_NAME_PREFIX + (i + 1);
        return TypeParameterName.of(name);
    }

    public static TypeParameterName getAlternativeClassTypeParameterName(int i) {
        String name = CLASS_TYPE_PARAMETER_NAME_PREFIX + (i + 1);
        return TypeParameterName.of(name);
    }
}
