package nl.wernerdegroot.applicatives.records;

import nl.wernerdegroot.applicatives.runtime.Function8;
import nl.wernerdegroot.applicatives.runtime.decompositions.Decomposable8;

public interface Record8<R extends Record & Record8<R, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth>, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth> extends Decomposable8<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth> {

    @Override
    default <T> T decomposeTo(Function8<? super First, ? super Second, ? super Third, ? super Fourth, ? super Fifth, ? super Sixth, ? super Seventh, ? super Eighth, ? extends T> fn) {
        var components = this.getClass().getRecordComponents();
        try {
            return fn.apply(
                    (First) components[0].getAccessor().invoke(this),
                    (Second) components[1].getAccessor().invoke(this),
                    (Third) components[2].getAccessor().invoke(this),
                    (Fourth) components[3].getAccessor().invoke(this),
                    (Fifth) components[4].getAccessor().invoke(this),
                    (Sixth) components[5].getAccessor().invoke(this),
                    (Seventh) components[6].getAccessor().invoke(this),
                    (Eighth) components[7].getAccessor().invoke(this)
            );
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
