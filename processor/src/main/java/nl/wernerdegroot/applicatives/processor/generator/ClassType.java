package nl.wernerdegroot.applicatives.processor.generator;

public enum ClassType {
    CLASS(Constants.CLASS),
    INTERFACE(Constants.INTERFACE);

    private final String stringValue;

    ClassType(String stringValue) {
        this.stringValue = stringValue;
    }

    @Override
    public String toString() {
        return stringValue;
    }
}
