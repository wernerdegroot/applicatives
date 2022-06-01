package nl.wernerdegroot.applicatives.prelude;

import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;

// Inspired by https://dzone.com/articles/how-to-replay-java-streams
public class ReplayStream<T> {

    private final Spliterator<T> spliterator;
    private final long originalSizeEstimate;
    private boolean hasExhaustedSpliterator = false;
    private final List<T> buffer = new ArrayList<>();

    public ReplayStream(Stream<T> source) {
        this.spliterator = source.spliterator();
        this.originalSizeEstimate = spliterator.estimateSize();
    }

    public static <T> ReplayStream<T> of(Stream<T> source) {
        return new ReplayStream<>(source);
    }

    public Spliterator<T> spliterator() {
        return new Spliterators.AbstractSpliterator<T>(originalSizeEstimate, spliterator.characteristics()) {

            private int index = 0;

            @Override
            public synchronized boolean tryAdvance(Consumer<? super T> consumer) {
                if (index < buffer.size()) {
                    consumer.accept(buffer.get(index));
                    index++;
                    return true;
                }

                if (!hasExhaustedSpliterator) {
                    boolean hasConsumed = spliterator.tryAdvance(element -> {
                        buffer.add(element);
                        consumer.accept(element);
                    });
                    hasExhaustedSpliterator = !hasConsumed;

                    if (hasConsumed) {
                        index++;
                    }

                    return hasConsumed;
                }

                return false;
            }
        };
    }
}
