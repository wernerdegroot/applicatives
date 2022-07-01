package nl.wernerdegroot.applicatives.records;

import nl.wernerdegroot.applicatives.runtime.Function7;
import nl.wernerdegroot.applicatives.runtime.decompositions.Decomposable7;

public interface Record7<R extends Record & Record7<R, First, Second, Third, Fourth, Fifth, Sixth, Seventh>, First, Second, Third, Fourth, Fifth, Sixth, Seventh> extends Decomposable7<First, Second, Third, Fourth, Fifth, Sixth, Seventh> {

    @Override
    default <T> T decomposeTo(Function7<? super First, ? super Second, ? super Third, ? super Fourth, ? super Fifth, ? super Sixth, ? super Seventh, ? extends T> fn) {
        var components = this.getClass().getRecordComponents();
        try {
            return fn.apply(
                    (First) components[0].getAccessor().invoke(this),
                    (Second) components[1].getAccessor().invoke(this),
                    (Third) components[2].getAccessor().invoke(this),
                    (Fourth) components[3].getAccessor().invoke(this),
                    (Fifth) components[4].getAccessor().invoke(this),
                    (Sixth) components[5].getAccessor().invoke(this),
                    (Seventh) components[6].getAccessor().invoke(this)
            );
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
