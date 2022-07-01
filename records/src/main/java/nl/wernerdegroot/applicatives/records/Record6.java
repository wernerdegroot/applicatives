package nl.wernerdegroot.applicatives.records;

import nl.wernerdegroot.applicatives.runtime.Function6;
import nl.wernerdegroot.applicatives.runtime.decompositions.Decomposable6;

public interface Record6<R extends Record & Record6<R, First, Second, Third, Fourth, Fifth, Sixth>, First, Second, Third, Fourth, Fifth, Sixth> extends Decomposable6<First, Second, Third, Fourth, Fifth, Sixth> {

    @Override
    default <T> T decomposeTo(Function6<? super First, ? super Second, ? super Third, ? super Fourth, ? super Fifth, ? super Sixth, ? extends T> fn) {
        var components = this.getClass().getRecordComponents();
        try {
            return fn.apply(
                    (First) components[0].getAccessor().invoke(this),
                    (Second) components[1].getAccessor().invoke(this),
                    (Third) components[2].getAccessor().invoke(this),
                    (Fourth) components[3].getAccessor().invoke(this),
                    (Fifth) components[4].getAccessor().invoke(this),
                    (Sixth) components[5].getAccessor().invoke(this)
            );
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
