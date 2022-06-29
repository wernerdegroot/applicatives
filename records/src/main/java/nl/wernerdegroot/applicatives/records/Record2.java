package nl.wernerdegroot.applicatives.records;

import nl.wernerdegroot.applicatives.runtime.decompositions.Decomposable2;

import java.util.function.BiFunction;

public interface Record2<R extends Record & Record2<R, First, Second>, First, Second> extends Decomposable2<First, Second> {

    @Override
    default <T> T decomposeTo(BiFunction<? super First, ? super Second, T> fn) {
        var components = this.getClass().getRecordComponents();
        try {
            return fn.apply(
                    (First) components[0].getAccessor().invoke(this),
                    (Second) components[1].getAccessor().invoke(this)
            );
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
