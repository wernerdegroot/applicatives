package nl.wernerdegroot.applicatives.records;

import nl.wernerdegroot.applicatives.runtime.Function3;
import nl.wernerdegroot.applicatives.runtime.decompositions.Decomposable3;

import java.util.function.BiFunction;

public interface Record3<R extends Record & Record3<R, First, Second, Third>, First, Second, Third> extends Decomposable3<First, Second, Third> {

    @Override
    default <T> T decomposeTo(Function3<? super First, ? super Second, ? super Third, ? extends T> fn) {
        var components = this.getClass().getRecordComponents();
        try {
            return fn.apply(
                    (First) components[0].getAccessor().invoke(this),
                    (Second) components[1].getAccessor().invoke(this),
                    (Third) components[2].getAccessor().invoke(this)
            );
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
