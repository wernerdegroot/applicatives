package nl.wernerdegroot.applicatives.prelude;

import nl.wernerdegroot.applicatives.runtime.Contravariant;

import java.util.Comparator;
import java.util.function.Function;

public class Comparators implements ComparatorsOverloads {

    @Override
    @Contravariant
    public <A, B, Intermediate, C> Comparator<C> combine(Comparator<? super A> left, Comparator<? super B> right, Function<? super C, ? extends Intermediate> fn, Function<? super Intermediate, ? extends A> extractFirst, Function<? super Intermediate, ? extends B> extractSecond) {
        Comparator<Intermediate> comp = Comparator.<Intermediate, A>comparing(extractFirst, left).thenComparing(extractSecond, right);
        return Comparator.comparing(fn, comp);
    }
}
