package nl.wernerdegroot.applicatives.json;

import nl.wernerdegroot.applicatives.runtime.Covariant;

import java.util.function.BiFunction;

import static nl.wernerdegroot.applicatives.json.ReadResult.SUCCESS;

public interface JsonReaders extends JsonReadersOverloads {

    @Override
    @Covariant(className = "JsonReadersOverloads")
    default <A, B, C> JsonReader<C> reader(
            JsonReader<? extends A> left,
            JsonReader<? extends B> right,
            BiFunction<? super A, ? super B, ? extends C> combinator) {

        return (toRead, ctx) -> {
            ctx.startReading();
            A fromLeft = left.read(toRead, ctx);
            B fromRight = right.read(toRead, ctx);
            return ctx.finishReading() == SUCCESS ? combinator.apply(fromLeft, fromRight) : null;
        };
    }
}
