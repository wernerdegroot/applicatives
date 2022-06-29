package nl.wernerdegroot.applicatives.json;

import javax.json.JsonArray;
import javax.json.JsonValue;
import java.util.ArrayList;
import java.util.List;

import static javax.json.JsonValue.ValueType.ARRAY;
import static nl.wernerdegroot.applicatives.json.Errors.NOT_AN_ARRAY;
import static nl.wernerdegroot.applicatives.json.Errors.UNEXPECTED_NULL;
import static nl.wernerdegroot.applicatives.json.ReadResult.SUCCESS;

public class JsonListReader<T> implements JsonReader<List<T>> {

    private final JsonReader<T> elementReader;

    public JsonListReader(JsonReader<T> elementReader) {
        this.elementReader = elementReader;
    }

    @Override
    public List<T> read(JsonValue toRead, ValidationContext ctx) {
        if (toRead == null) {
            return ctx.notifyFailure(UNEXPECTED_NULL.getErrorMessageKey());
        }

        if (toRead.getValueType() != ARRAY) {
            return ctx.notifyFailure(NOT_AN_ARRAY.getErrorMessageKey(), toRead.getValueType());
        }

        JsonArray jsonArray = (JsonArray) toRead;

        List<T> elements = new ArrayList<>();

        ctx.startReading();

        int index = 0;
        for (JsonValue element : jsonArray) {
            ctx.pushKey(Integer.toString(index));
            elements.add(elementReader.read(element, ctx));
            ctx.popKey();
            index++;
        }

        return ctx.finishReading() == SUCCESS ? elements : null;
    }
}
