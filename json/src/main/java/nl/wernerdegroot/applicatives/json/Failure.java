package nl.wernerdegroot.applicatives.json;

public class Failure {
    private final Path path;
    private final String errorMessageKey;
    private final Object[] arguments;

    public Failure(Path path, String errorMessageKey, Object[] arguments) {
        this.path = path;
        this.errorMessageKey = errorMessageKey;
        this.arguments = arguments;
    }

    public Failure atPathComponent(String component) {
        return new Failure(
                path.append(component),
                errorMessageKey,
                arguments
        );
    }
}
