package nl.wernerdegroot.applicatives.json;

import javax.json.JsonValue;
import java.util.List;

public interface JsonFormat<T> extends JsonReader<T>, JsonWriter<T> {

    static <T> JsonFormat<T> of(JsonReader<T> reader, JsonWriter<T> writer) {
        return new JsonFormat<T>() {
            @Override
            public T read(JsonValue toRead, ValidationContext ctx) {
                return reader.read(toRead, ctx);
            }

            @Override
            public JsonValue write(T toWrite) {
                return writer.write(toWrite);
            }
        };
    }

    @Override
    default JsonFormat<List<T>> list() {
        return of(JsonReader.super.list(), JsonWriter.super.list());
    }
}
