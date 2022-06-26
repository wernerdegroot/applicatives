package nl.wernerdegroot.applicatives.json;

import javax.json.JsonValue;
import java.util.List;

public interface JsonFormat<T> extends JsonReader<T>, JsonWriter<T> {

    static <T> JsonFormat<T> of(JsonReader<T> reader, JsonWriter<T> writer) {
        return new JsonFormat<T>() {
            @Override
            public Json.Result<T> read(Path path, JsonValue toRead) {
                return reader.read(path, toRead);
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
