package nl.wernerdegroot.applicatives.json;

import nl.wernerdegroot.applicatives.runtime.Invariant;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface JsonFormats extends JsonFormatsOverloads {

    @Override
    @Invariant(liftMethodName = "inlift")
    default <A, B, Intermediate, C> JsonObjectFormat<C> formats(
            JsonObjectFormat<A> left,
            JsonObjectFormat<B> right,
            BiFunction<? super A, ? super B, ? extends C> combinator,
            Function<? super C, ? extends Intermediate> toIntermediate,
            Function<? super Intermediate, ? extends A> extractLeft,
            Function<? super Intermediate, ? extends B> extractRight) {

        return JsonObjectFormat.of(
                Json.instance().readers(left, right, combinator),
                Json.instance().writers(left, right, toIntermediate, extractLeft, extractRight)
        );
    }

}
