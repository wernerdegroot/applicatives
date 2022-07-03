package nl.wernerdegroot.applicatives.processor.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ConfigValidator {

    private static final Pattern VALID_IDENTIFIER = Pattern.compile("^([a-zA-Z_$][a-zA-Z\\d_$]*)$");

    public static Validated<String, Void> validate(String classNameToGenerate, String combineMethodName, String liftMethodName, int maxArity) {
        List<String> errorMessages = new ArrayList<>();

        errorMessages.addAll(isClassNameToGenerateValid(classNameToGenerate));
        errorMessages.addAll(isMethodNameValid(combineMethodName));
        errorMessages.addAll(isMethodNameValid(liftMethodName));
        errorMessages.addAll(isMaxArityValid(maxArity));

        if (errorMessages.isEmpty()) {
            return Validated.valid(null);
        } else {
            return Validated.invalid(errorMessages);
        }
    }

    private static List<String> isClassNameToGenerateValid(String classNameToGenerate) {
        List<String> errorMessages = new ArrayList<>();

        boolean isValid = VALID_IDENTIFIER.matcher(classNameToGenerate).matches();
        if (!isValid) {
            errorMessages.add(String.format("Class name '%s' is not valid", classNameToGenerate));
        }

        return errorMessages;
    }

    private static List<String> isMethodNameValid(String methodName) {
        List<String> errorMessages = new ArrayList<>();

        boolean isValid = VALID_IDENTIFIER.matcher(methodName).matches();
        if (!isValid) {
            errorMessages.add(String.format("Method name '%s' is not valid", methodName));
        }

        return errorMessages;
    }

    private static List<String> isMaxArityValid(int maxArity) {
        List<String> errorMessages = new ArrayList<>();

        boolean isValid = 2 <= maxArity && maxArity <= 26;
        if (!isValid) {
            errorMessages.add(String.format("Maximum arity should be between 2 and 26 (but was %d)", maxArity));
        }

        return errorMessages;
    }
}
