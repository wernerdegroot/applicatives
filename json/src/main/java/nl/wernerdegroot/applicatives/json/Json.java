package nl.wernerdegroot.applicatives.json;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

public class Json implements JsonReaders, JsonWriters, JsonFormats {

    private static final Json INSTANCE = new Json();

    public static JsonStringFormat STRING = new JsonStringFormat();
    public static JsonIntFormat INT = new JsonIntFormat();
    public static JsonLongFormat LONG = new JsonLongFormat();
    public static JsonDoubleFormat DOUBLE = new JsonDoubleFormat();
    public static JsonBigDecimalFormat BIG_DECIMAL = new JsonBigDecimalFormat();
    public static JsonBigIntegerFormat BIG_INTEGER = new JsonBigIntegerFormat();

    public static Json instance() {
        return INSTANCE;
    }

    public static <T> Success<T> success(T value) {
        return new Success<>(value);
    }

    public static <T> Failed<T> failed(List<Failure> failures) {
        return new Failed<>(failures);
    }

    public interface Result<T> {

        boolean isSuccess();

        T get();

        List<Failure> getFailures();
    }

    public static class Success<T> implements Result<T> {
        private final T value;

        public Success(T value) {
            this.value = value;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public List<Failure> getFailures() {
            throw new NoSuchElementException();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Success<?> success = (Success<?>) o;
            return Objects.equals(value, success.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return "Success{" +
                    "value=" + value +
                    '}';
        }
    }

    public static class Failed<T> implements Result<T> {
        private final List<Failure> failures;

        public Failed(List<Failure> failures) {
            this.failures = failures;
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public T get() {
            throw new NoSuchElementException();
        }

        @Override
        public List<Failure> getFailures() {
            return failures;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Failed<?> failed = (Failed<?>) o;
            return Objects.equals(getFailures(), failed.getFailures());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getFailures());
        }

        @Override
        public String toString() {
            return "Failed{" +
                    "failures=" + failures +
                    '}';
        }
    }

}
