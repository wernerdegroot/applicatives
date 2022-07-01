package nl.wernerdegroot.applicatives.records;

import nl.wernerdegroot.applicatives.runtime.Function5;
import nl.wernerdegroot.applicatives.runtime.decompositions.Decomposable5;

public interface Record5<R extends Record & Record5<R, First, Second, Third, Fourth, Fifth>, First, Second, Third, Fourth, Fifth> extends Decomposable5<First, Second, Third, Fourth, Fifth> {

    @Override
    default <T> T decomposeTo(Function5<? super First, ? super Second, ? super Third, ? super Fourth, ? super Fifth, ? extends T> fn) {
        var components = this.getClass().getRecordComponents();
        try {
            return fn.apply(
                    (First) components[0].getAccessor().invoke(this),
                    (Second) components[1].getAccessor().invoke(this),
                    (Third) components[2].getAccessor().invoke(this),
                    (Fourth) components[3].getAccessor().invoke(this),
                    (Fifth) components[4].getAccessor().invoke(this)
            );
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
