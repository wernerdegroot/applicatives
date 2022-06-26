package nl.wernerdegroot.applicatives.json;

public enum Errors {
    UNEXPECTED_NULL("json.parse.error.unexpectedNull"),
    NOT_A_NUMBER("json.parse.error.notANumber"),
    NOT_A_STRING("json.parse.error.notAString"),
    NOT_AN_ARRAY("json.parse.error.notAnArray"),
    NOT_AN_OBJECT("json.parse.error.nonAnObject");

    private final String errorMessageKey;

    Errors(String errorMessageKey) {
        this.errorMessageKey = errorMessageKey;
    }

    public String getErrorMessageKey() {
        return errorMessageKey;
    }
}
