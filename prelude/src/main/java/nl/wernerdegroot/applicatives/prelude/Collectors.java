package nl.wernerdegroot.applicatives.prelude;

import nl.wernerdegroot.applicatives.runtime.Covariant;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.stream.Collector;

public class Collectors<T> implements CollectorsOverloads<T> {

    private static final Collectors<Object> INSTANCE = new Collectors<>();

    @SuppressWarnings("unchecked")
    public static <T> Collectors<T> instance() {
        return (Collectors<T>) INSTANCE;
    }

    @Override
    @Covariant(className = "CollectorsOverloads")
    public <A, B, C> Collector<T, ?, C> combine(Collector<T, ?, A> left, Collector<T, ?, B> right, BiFunction<? super A, ? super B, ? extends C> fn) {
        return typeSafeCombine(left, right, fn);
    }

    private static <Element, LeftAccumulated, RightAccumulated, LeftResult, RightResult, CombinedResult> Collector<Element, ?, CombinedResult> typeSafeCombine(Collector<Element, LeftAccumulated, LeftResult> leftCollector, Collector<Element, RightAccumulated, RightResult> rightCollector, BiFunction<? super LeftResult, ? super RightResult, ? extends CombinedResult> fn) {

        Set<Collector.Characteristics> leftCharacteristics = leftCollector.characteristics();
        Set<Collector.Characteristics> rightCharacteristics = rightCollector.characteristics();
        Set<Collector.Characteristics> characteristics = EnumSet.noneOf(Collector.Characteristics.class);
        characteristics.addAll(leftCharacteristics);
        characteristics.retainAll(rightCharacteristics);
        characteristics.remove(Collector.Characteristics.IDENTITY_FINISH);

        BiConsumer<LeftAccumulated, Element> leftAccumulator = leftCollector.accumulator();
        BiConsumer<RightAccumulated, Element> rightAccumulator = rightCollector.accumulator();
        BinaryOperator<LeftAccumulated> leftCombiner = leftCollector.combiner();
        BinaryOperator<RightAccumulated> rightCombiner = rightCollector.combiner();

        class Accumulator {

            private LeftAccumulated left = leftCollector.supplier().get();
            private RightAccumulated right = rightCollector.supplier().get();

            public void accumulate(Element element) {
                leftAccumulator.accept(left, element);
                rightAccumulator.accept(right, element);
            }

            public Accumulator combine(Accumulator that) {
                this.left = leftCombiner.apply(this.left, that.left);
                this.right = rightCombiner.apply(this.right, that.right);
                return this;
            }

            public CombinedResult finish() {
                return fn.apply(
                   leftCollector.finisher().apply(left),
                   rightCollector.finisher().apply(right)
                );
            }
        }

        return Collector.<Element, Accumulator, CombinedResult>of(
                Accumulator::new,
                Accumulator::accumulate,
                Accumulator::combine,
                Accumulator::finish,
                characteristics.toArray(new Collector.Characteristics[0])
        );
    }

}
