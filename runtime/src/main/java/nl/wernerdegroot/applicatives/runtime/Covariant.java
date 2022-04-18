package nl.wernerdegroot.applicatives.runtime;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface Covariant {
    String className();
    String liftMethodName() default "lift";
    int maxArity() default 26;

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.SOURCE)
    @interface Builder {
        String className();
        String liftMethodName() default "lift";
        int maxArity() default 26;
    }
}
