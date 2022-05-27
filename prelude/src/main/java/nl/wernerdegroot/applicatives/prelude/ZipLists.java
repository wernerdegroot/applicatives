package nl.wernerdegroot.applicatives.prelude;

import nl.wernerdegroot.applicatives.runtime.Accumulator;
import nl.wernerdegroot.applicatives.runtime.Covariant;
import nl.wernerdegroot.applicatives.runtime.Finalizer;
import nl.wernerdegroot.applicatives.runtime.Initializer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;

@Covariant.Builder(className = "ZipListsApplicative")
public class ZipLists implements ZipListsApplicative {

    private static final ZipLists INSTANCE = new ZipLists();

    public static ZipLists instance() {
        return INSTANCE;
    }

    @Override
    @Initializer
    public <A> ZipList<A> singleton(A value) {
        return ZipList.singleton(value);
    }

    @Override
    @Accumulator
    public <A, B, C> ZipList<C> combine(ZipList<? extends A> left, List<? extends B> right, BiFunction<? super A, ? super B, ? extends C> fn) {
            return ZipList.of(left, right, fn);
    }

    @Override
    @Finalizer
    public <A> List<A> finalize(ZipList<A> toFinalize) {
        ArrayList<A> finalized = new ArrayList<>(toFinalize.getSize());
        for (A value : toFinalize) {
            finalized.add(value);
        }
        return finalized;
    }
}
