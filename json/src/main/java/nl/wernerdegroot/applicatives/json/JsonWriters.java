package nl.wernerdegroot.applicatives.json;

import nl.wernerdegroot.applicatives.runtime.Contravariant;

import java.util.function.Function;

public interface JsonWriters extends JsonWritersOverloads {

    @Override
    @Contravariant(liftMethodName = "contralift")
    default <A, B, Intermediate, C> JsonObjectWriter<C> writer(
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
}
