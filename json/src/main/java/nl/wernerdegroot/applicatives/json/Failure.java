package nl.wernerdegroot.applicatives.json;

import java.util.Arrays;
import java.util.Objects;

public class Failure {
    private final String path;
    private final String errorMessageKey;
    private final Object[] arguments;

    public Failure(String path, String errorMessageKey, Object[] arguments) {
        this.path = path;
        this.errorMessageKey = errorMessageKey;
        this.arguments = arguments;
    }

    public static Failure of(String path, String errorMessageKey, Object... arguments) {
        return new Failure(path, errorMessageKey, arguments);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Failure failure = (Failure) o;
        return Objects.equals(path, failure.path) && Objects.equals(errorMessageKey, failure.errorMessageKey) && Arrays.equals(arguments, failure.arguments);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(path, errorMessageKey);
        result = 31 * result + Arrays.hashCode(arguments);
        return result;
    }

    @Override
    public String toString() {
        return "Failure{" +
                "path='" + path + '\'' +
                ", errorMessageKey='" + errorMessageKey + '\'' +
                ", arguments=" + Arrays.toString(arguments) +
                '}';
    }
}
