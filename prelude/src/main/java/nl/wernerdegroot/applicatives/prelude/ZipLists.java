package nl.wernerdegroot.applicatives.prelude;

import nl.wernerdegroot.applicatives.runtime.Covariant;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;

public class ZipLists implements ZipListsApplicative {

    @Override
    @Covariant(className = "ZipListsApplicative")
    public <A, B, C> List<C> combine(List<? extends A> left, List<? extends B> right, BiFunction<? super A, ? super B, ? extends C> fn) {
        List<C> result = new ArrayList<>(Math.max(left.size(), right.size()));
        Iterator<? extends A> leftIterator = left.iterator();
        Iterator<? extends B> rightIterator = right.iterator();
        while (leftIterator.hasNext() && rightIterator.hasNext()) {
            A elementFromLeft = leftIterator.next();
            B elementFromRight = rightIterator.next();
            result.add(fn.apply(elementFromLeft, elementFromRight));
        }
        return result;
    }
}
