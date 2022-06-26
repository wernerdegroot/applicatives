package nl.wernerdegroot.applicatives.json;

import javax.json.JsonValue;
import java.util.List;
import java.util.function.Function;

public interface JsonWriter<T> {

    JsonValue write(T toWrite);

    default JsonWriter<List<T>> list() {
        return new JsonListWriter<>(this);
    }

    default <U> JsonWriter<U> contramap(Function<? super U, ? extends T> fn) {
        return toWrite -> write(fn.apply(toWrite));
    }

    default String writeString(T toWrite) {
        return write(toWrite).toString();
    }
}
