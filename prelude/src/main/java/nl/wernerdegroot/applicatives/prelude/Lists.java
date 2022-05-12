package nl.wernerdegroot.applicatives.prelude;

import nl.wernerdegroot.applicatives.runtime.Accumulator;
import nl.wernerdegroot.applicatives.runtime.Covariant;
import nl.wernerdegroot.applicatives.runtime.Initializer;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

/**
 * When combining <strong>more</strong> than two lists, it is strongly advised
 * to use {@link nl.wernerdegroot.applicatives.prelude.Streams Streams} instead
 * to avoid many intermediate lists that need to be created and then garbage
 * collected.
 */
@Covariant.Builder(className = "ListsApplicative")
public class Lists implements ListsApplicative {

    /*
        public static <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, P23, P24, P25, P26> java.util.ArrayList<? extends nl.wernerdegroot.applicatives.runtime.FastTuple<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, P23, P24, P25, P26>> tuple(nl.wernerdegroot.applicatives.prelude.ListsApplicative self, int maxSize) {
            return self.singleton(new FastTuple<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, P23, P24, P25, P26>(maxSize));
        }

        public static <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, P23, P24, P25, P26> java.util.ArrayList<? extends nl.wernerdegroot.applicatives.runtime.FastTuple<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, P23, P24, P25, P26>> tuple(nl.wernerdegroot.applicatives.prelude.ListsApplicative self, java.util.ArrayList<? extends P1> first, int maxSize) {
            return self.combine(nl.wernerdegroot.applicatives.prelude.ListsApplicative.Tuples.<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, P23, P24, P25, P26>tuple(self, maxSize), first, nl.wernerdegroot.applicatives.runtime.FastTuple::withFirst);
        }

        public static <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, P23, P24, P25, P26> java.util.ArrayList<? extends nl.wernerdegroot.applicatives.runtime.FastTuple<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, P23, P24, P25, P26>> tuple(nl.wernerdegroot.applicatives.prelude.ListsApplicative self, java.util.ArrayList<? extends P1> first, java.util.List<? extends P2> second, int maxSize) {
            return self.combine(nl.wernerdegroot.applicatives.prelude.ListsApplicative.Tuples.<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, P23, P24, P25, P26>tuple(self, first, maxSize), second, nl.wernerdegroot.applicatives.runtime.FastTuple::withSecond);
        }

        public static <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, P23, P24, P25, P26> java.util.ArrayList<nl.wernerdegroot.applicatives.runtime.FastTuple<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, P23, P24, P25, P26>> tuple(nl.wernerdegroot.applicatives.prelude.ListsApplicative self, java.util.ArrayList<? extends P1> first, java.util.List<? extends P2> second, java.util.List<? extends P3> third, int maxSize) {
            return self.combine(nl.wernerdegroot.applicatives.prelude.ListsApplicative.Tuples.<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, P23, P24, P25, P26>tuple(self, first, second, maxSize), third, nl.wernerdegroot.applicatives.runtime.FastTuple::withThird);
        }
     */

    @Override
    @Initializer
    public <A> ArrayList<A> singleton(A value) {
        ArrayList<A> result = new ArrayList<>(1);
        result.add(value);
        return result;
    }

    // The fact that we are returning an `ArrayList` (implementation detail)
    // is a temporary situation while we allow the left type constructor and
    // the right type constructor to diverge (work in progress).
    @Override
    @Accumulator
    public <A, B, C> ArrayList<C> combine(ArrayList<? extends A> left, List<? extends B> right, BiFunction<? super A, ? super B, ? extends C> fn) {
        ArrayList<C> result = new ArrayList<>(left.size() * right.size());
        for (A elementFromLeft : left) {
            for (B elementFromRight : right) {
                result.add(fn.apply(elementFromLeft, elementFromRight));
            }
        }
        return result;
    }
}
