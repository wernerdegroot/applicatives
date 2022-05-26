package nl.wernerdegroot.applicatives.processor.validation;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class ConfigValidator {

    private static final Pattern VALID_IDENTIFIER = Pattern.compile("^([a-zA-Z_$][a-zA-Z\\d_$]*)$");

    public static Validated<Void> validate(String classNameToGenerate, String liftMethodName, int maxArity) {
        Set<String> errorMessages = new HashSet<>();
        errorMessages.addAll(isClassNameToGenerateValid(classNameToGenerate));
        errorMessages.addAll(isLiftMethodNameToGenerateValid(liftMethodName));
        errorMessages.addAll(isMaxArityValid(maxArity));

        if (errorMessages.isEmpty()) {
            return Validated.valid(null);
        } else {
            return Validated.invalid(errorMessages);
        }
    }

    private static Set<String> isClassNameToGenerateValid(String classNameToGenerate) {
        Set<String> errorMessages = new HashSet<>();

        boolean isValid = VALID_IDENTIFIER.matcher(classNameToGenerate).matches();
        if (!isValid) {
            errorMessages.add(String.format("Class name '%s' is not valid", classNameToGenerate));
        }

        return errorMessages;
    }

    private static Set<String> isLiftMethodNameToGenerateValid(String liftMethodName) {
        Set<String> errorMessages = new HashSet<>();

        boolean isValid = VALID_IDENTIFIER.matcher(liftMethodName).matches();
        if (!isValid) {
            errorMessages.add(String.format("Lift method name '%s' is not valid", liftMethodName));
        }

        return errorMessages;
    }

    private static Set<String> isMaxArityValid(int maxArity) {
        Set<String> errorMessages = new HashSet<>();

        boolean isValid = 2 <= maxArity && maxArity <= 26;
        if (!isValid) {
            errorMessages.add(String.format("Maximum arity should be between 2 and 26 (but was %d)", maxArity));
        }

        return errorMessages;
    }
}
