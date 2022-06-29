package nl.wernerdegroot.applicatives.records;

import nl.wernerdegroot.applicatives.runtime.Function11;
import nl.wernerdegroot.applicatives.runtime.decompositions.Decomposable11;

public interface Record11<R extends Record & Record11<R, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh>, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh> extends Decomposable11<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh> {

    @Override
    default <T> T decomposeTo(Function11<? super First, ? super Second, ? super Third, ? super Fourth, ? super Fifth, ? super Sixth, ? super Seventh, ? super Eighth, ? super Ninth, ? super Tenth, ? super Eleventh, ? extends T> fn) {
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
                    (Eighth) components[7].getAccessor().invoke(this),
                    (Ninth) components[8].getAccessor().invoke(this),
                    (Tenth) components[9].getAccessor().invoke(this),
                    (Eleventh) components[10].getAccessor().invoke(this)
            );
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
