package nl.wernerdegroot.applicatives.json;

import java.util.function.Function;

public interface Validated<T> {

    static <T> Valid<T> valid(T value) {
        return new Valid<>(value);
    }

    static <T> Invalid<T> invalid(String errorMessageKey, Object... arguments) {
        return new Invalid<>(errorMessageKey, arguments);
    }

    <U> U fold(Function<? super Invalid<T>, ? extends U> handleInvalid, Function<? super Valid<T>, ? extends U> handleValid);

    Json.Result<T> asResult(Path path);

    class Valid<T> implements Validated<T> {
        private final T value;

        public Valid(T value) {
            this.value = value;
        }

        @Override
        public <U> U fold(Function<? super Invalid<T>, ? extends U> handleInvalid, Function<? super Valid<T>, ? extends U> handleValid) {
            return handleValid.apply(this);
        }

        @Override
        public Json.Result<T> asResult(Path path) {
            return Json.success(value);
        }
    }

    class Invalid<T> implements Validated<T> {
        private final String errorMessageKey;
        private final Object[] arguments;

        public Invalid(String errorMessageKey, Object[] arguments) {
            this.errorMessageKey = errorMessageKey;
            this.arguments = arguments;
        }

        @Override
        public <U> U fold(Function<? super Invalid<T>, ? extends U> handleInvalid, Function<? super Valid<T>, ? extends U> handleValid) {
            return handleInvalid.apply(this);
        }

        @Override
        public Json.Result<T> asResult(Path path) {
            return Json.failed(path, errorMessageKey, arguments);
        }
    }
}
