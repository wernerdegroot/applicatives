package nl.wernerdegroot.applicatives.records;

import nl.wernerdegroot.applicatives.runtime.Function4;
import nl.wernerdegroot.applicatives.runtime.decompositions.Decomposable4;

public interface Record4<R extends Record & Record4<R, First, Second, Third, Fourth>, First, Second, Third, Fourth> extends Decomposable4<First, Second, Third, Fourth> {

    @Override
    default <T> T decomposeTo(Function4<? super First, ? super Second, ? super Third, ? super Fourth, ? extends T> fn) {
        var components = this.getClass().getRecordComponents();
        try {
            return fn.apply(
                    (First) components[0].getAccessor().invoke(this),
                    (Second) components[1].getAccessor().invoke(this),
                    (Third) components[2].getAccessor().invoke(this),
                    (Fourth) components[3].getAccessor().invoke(this)
            );
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
