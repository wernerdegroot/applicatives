package nl.wernerdegroot.applicatives.json;

import nl.wernerdegroot.applicatives.runtime.Contravariant;
import nl.wernerdegroot.applicatives.runtime.Covariant;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Collections.singletonList;

public class Json implements JsonReaderOverloads, JsonWriterOverloads {

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

    @Override
    @Contravariant(className = "*WriterOverloads")
    public <A, B, Intermediate, C> JsonObjectWriter<C> writers(
            JsonObjectWriter<? super A> left,
            JsonObjectWriter<? super B> right,
            Function<? super C, ? extends Intermediate> toIntermediate,
            Function<? super Intermediate, ? extends A> extractLeft,
            Function<? super Intermediate, ? extends B> extractRight) {

        return (builder, toWrite) -> {
            Intermediate intermediate = toIntermediate.apply(toWrite);
            left.write(builder, extractLeft.apply(intermediate));
            right.write(builder, extractRight.apply(intermediate));
        };
    }

    @Override
    @Covariant(className = "*ReaderOverloads")
    public <A, B, C> JsonObjectReader<C> readers(
            JsonObjectReader<? extends A> left,
            JsonObjectReader<? extends B> right,
            BiFunction<? super A, ? super B, ? extends C> combinator) {

        return (path, toRead) -> {
            Result<? extends A> fromLeft = left.read(path, toRead);
            Result<? extends B> fromRight = right.read(path, toRead);
            if (fromLeft.isSuccess() && fromRight.isSuccess()) {
                return success(combinator.apply(fromLeft.get(), fromRight.get()));
            } else {
                List<Failure> failures = new ArrayList<>();
                if (!fromLeft.isSuccess()) {
                    failures.addAll(fromLeft.getFailures());
                }
                if (!fromRight.isSuccess()) {
                    failures.addAll(fromRight.getFailures());
                }
                return failed(failures);
            }
        };
    }

    public static <T> Success<T> success(T value) {
        return new Success<>(value);
    }

    public static <T> Failed<T> failed(Path path, String errorMessageKey, Object... arguments) {
        return new Failed<>(singletonList(new Failure(path, errorMessageKey, arguments)));
    }

    public static <T> Failed<T> failed(List<Failure> failures) {
        return new Failed<>(failures);
    }

    public interface Result<T> {
        <U> Result<U> validate(Path path, Function<? super T, ? extends Validated<U>> validation);

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
        public <U> Result<U> validate(Path path, Function<? super T, ? extends Validated<U>> validation) {
            return validation.apply(value).asResult(path);
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
    }

    public static class Failed<T> implements Result<T> {
        private final List<Failure> failures;

        public Failed(List<Failure> failures) {
            this.failures = failures;
        }

        @Override
        public <U> Result<U> validate(Path path, Function<? super T, ? extends Validated<U>> validation) {
            return new Failed<>(failures);
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
    }

}
