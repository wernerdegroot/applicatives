package nl.wernerdegroot.applicatives.json;

import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import java.util.List;

public interface JsonObjectFormat<T> extends JsonReader<T>, JsonObjectWriter<T> {

    static <T> JsonObjectFormat<T> of(JsonReader<T> reader, JsonObjectWriter<T> writer) {
        return new JsonObjectFormat<T>() {
            @Override
            public T read(JsonValue toRead, ValidationContext ctx) {
                return reader.read(toRead, ctx);
            }

            @Override
            public void write(JsonObjectBuilder builder, T toWrite) {
                writer.write(builder, toWrite);
            }
        };
    }

    @Override
    default JsonFormat<List<T>> list() {
        return JsonFormat.of(JsonReader.super.list(), JsonObjectWriter.super.list());
    }
}
