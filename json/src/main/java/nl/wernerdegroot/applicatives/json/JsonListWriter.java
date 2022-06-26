package nl.wernerdegroot.applicatives.json;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonValue;
import java.util.List;

public class JsonListWriter<T> implements JsonWriter<List<T>> {

    private final JsonWriter<T> elementWriter;

    public JsonListWriter(JsonWriter<T> elementWriter) {
        this.elementWriter = elementWriter;
    }

    @Override
    public JsonValue write(List<T> toWrite) {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (T element : toWrite) {
            builder.add(elementWriter.write(element));
        }
        return builder.build();
    }
}
