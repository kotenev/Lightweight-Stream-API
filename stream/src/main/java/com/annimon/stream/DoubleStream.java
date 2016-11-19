package com.annimon.stream;

import com.annimon.stream.function.DoubleBinaryOperator;
import com.annimon.stream.function.DoubleConsumer;
import com.annimon.stream.function.DoubleFunction;
import com.annimon.stream.function.DoublePredicate;
import com.annimon.stream.function.DoubleSupplier;
import com.annimon.stream.function.DoubleToIntFunction;
import com.annimon.stream.function.DoubleUnaryOperator;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.ObjDoubleConsumer;
import com.annimon.stream.function.Supplier;
import com.annimon.stream.function.ToDoubleFunction;
import java.util.Arrays;
import java.util.Comparator;
import java.util.NoSuchElementException;

/**
 * A sequence of {@code double}-valued elements supporting aggregate operations.
 *
 * @since 1.1.4
 * @see Stream
 */
@SuppressWarnings("WeakerAccess")
public final class DoubleStream {

    /**
     * Single instance for empty stream. It is safe for multi-thread environment because it has no content.
     */
    private static final DoubleStream EMPTY = new DoubleStream(new PrimitiveIterator.OfDouble() {

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public double nextDouble() {
            return 0d;
        }
    });

    /**
     * Returns an empty stream.
     *
     * @return the empty stream
     */
    public static DoubleStream empty() {
        return EMPTY;
    }

    /**
     * Creates a {@code DoubleStream} from {@code PrimitiveIterator.OfDouble}.
     *
     * @param iterator  the iterator with elements to be passed to stream
     * @return the new {@code DoubleStream}
     * @throws NullPointerException if {@code iterator} is null
     */
    public static DoubleStream of(PrimitiveIterator.OfDouble iterator) {
        Objects.requireNonNull(iterator);
        return new DoubleStream(iterator);
    }

    /**
     * Creates a {@code DoubleStream} from the specified values.
     *
     * @param values  the elements of the new stream
     * @return the new stream
     * @throws NullPointerException if {@code values} is null
     */
    public static DoubleStream of(final double... values) {
        Objects.requireNonNull(values);
        return new DoubleStream(new PrimitiveIterator.OfDouble() {

            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < values.length;
            }

            @Override
            public double nextDouble() {
                return values[index++];
            }
        });
    }

    /**
     * Returns stream which contains single element passed as param
     *
     * @param t  element of the stream
     * @return the new stream
     */
    public static DoubleStream of(final double t) {
        return new DoubleStream(new PrimitiveIterator.OfDouble() {

            private int index = 0;

            @Override
            public boolean hasNext() {
                return index == 0;
            }

            @Override
            public double nextDouble() {
                index++;
                return t;
            }
        });
    }

    /**
     * Creates a {@code DoubleStream} by elements that generated by {@code DoubleSupplier}.
     *
     * @param s  the {@code DoubleSupplier} for generated elements
     * @return a new infinite sequential {@code DoubleStream}
     * @throws NullPointerException if {@code s} is null
     */
    public static DoubleStream generate(final DoubleSupplier s) {
        Objects.requireNonNull(s);
        return new DoubleStream(new PrimitiveIterator.OfDouble() {

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public double nextDouble() {
                return s.getAsDouble();
            }
        });
    }

    /**
     * Creates a {@code DoubleStream} by iterative application {@code DoubleUnaryOperator} function
     * to an initial element {@code seed}. Produces {@code DoubleStream} consisting of
     * {@code seed}, {@code f(seed)}, {@code f(f(seed))}, etc.
     *
     * <p> The first element (position {@code 0}) in the {@code DoubleStream} will be
     * the provided {@code seed}. For {@code n > 0}, the element at position
     * {@code n}, will be the result of applying the function {@code f} to the
     * element at position {@code n - 1}.
     *
     * <p>Example:
     * <pre>
     * seed: 1
     * f: (a) -&gt; a + 5
     * result: [1, 6, 11, 16, ...]
     * </pre>
     *
     * @param seed the initial element
     * @param f  a function to be applied to the previous element to produce a new element
     * @return a new sequential {@code DoubleStream}
     * @throws NullPointerException if {@code f} is null
     */
    public static DoubleStream iterate(final double seed, final DoubleUnaryOperator f) {
        Objects.requireNonNull(f);
        return new DoubleStream(new PrimitiveIterator.OfDouble() {

            private double current = seed;

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public double nextDouble() {
                final double old = current;
                current = f.applyAsDouble(current);
                return old;
            }
        });
    }

    /**
     * Concatenates two streams.
     *
     * <p>Example:
     * <pre>
     * stream a: [1, 2, 3, 4]
     * stream b: [5, 6]
     * result:   [1, 2, 3, 4, 5, 6]
     * </pre>
     *
     * @param a  the first stream
     * @param b  the second stream
     * @return the new concatenated stream
     * @throws NullPointerException if {@code a} or {@code b} is null
     */
    public static DoubleStream concat(final DoubleStream a, final DoubleStream b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        final PrimitiveIterator.OfDouble it1 = a.iterator;
        final PrimitiveIterator.OfDouble it2 = b.iterator;
        return new DoubleStream(new PrimitiveIterator.OfDouble() {

            private boolean firstStreamIsCurrent = true;

            @Override
            public boolean hasNext() {
                if (firstStreamIsCurrent) {
                    if (it1.hasNext())
                        return true;

                    firstStreamIsCurrent = false;
                }
                return it2.hasNext();
            }

            @Override
            public double nextDouble() {
                return firstStreamIsCurrent ? it1.nextDouble() : it2.nextDouble();
            }
        });
    }


    private final PrimitiveIterator.OfDouble iterator;

    private DoubleStream(PrimitiveIterator.OfDouble iterator) {
        this.iterator = iterator;
    }

    /**
     * Returns internal {@code DoubleStream} iterator.
     *
     * @return internal {@code DoubleStream} iterator.
     */
    public PrimitiveIterator.OfDouble iterator() {
        return iterator;
    }

    /**
     * Applies custom operator on stream.
     *
     * Transforming function can return {@code DoubleStream} for intermediate operations,
     * or any value for terminal operation.
     *
     * <p>Operator examples:
     * <pre><code>
     *     // Intermediate operator
     *     public class Zip implements Function&lt;DoubleStream, DoubleStream&gt; {
     *
     *         private final DoubleStream secondStream;
     *         private final DoubleBinaryOperator combiner;
     *
     *         public Zip(DoubleStream secondStream, DoubleBinaryOperator combiner) {
     *             this.secondStream = secondStream;
     *             this.combiner = combiner;
     *         }
     *
     *         &#64;Override
     *         public DoubleStream apply(DoubleStream firstStream) {
     *             final PrimitiveIterator.OfDouble it1 = firstStream.iterator();
     *             final PrimitiveIterator.OfDouble it2 = secondStream.iterator();
     *             return DoubleStream.of(new PrimitiveIterator.OfDouble() {
     *                 &#64;Override
     *                 public boolean hasNext() {
     *                     return it1.hasNext() &amp;&amp; it2.hasNext();
     *                 }
     *
     *                 &#64;Override
     *                 public double nextDouble() {
     *                     return combiner.applyAsDouble(it1.nextDouble(), it2.nextDouble());
     *                 }
     *             });
     *         }
     *     }
     *
     *     // Intermediate operator based on existing stream operators
     *     public class SkipAndLimit implements UnaryOperator&lt;DoubleStream&gt; {
     *
     *         private final int skip, limit;
     *
     *         public SkipAndLimit(int skip, int limit) {
     *             this.skip = skip;
     *             this.limit = limit;
     *         }
     *
     *         &#64;Override
     *         public DoubleStream apply(DoubleStream stream) {
     *             return stream.skip(skip).limit(limit);
     *         }
     *     }
     *
     *     // Terminal operator
     *     public class DoubleSummaryStatistics implements Function&lt;DoubleStream, double[]&gt; {
     *         &#64;Override
     *         public double[] apply(DoubleStream stream) {
     *             long count = 0;
     *             double sum = 0;
     *             final PrimitiveIterator.OfDouble it = stream.iterator();
     *             while (it.hasNext()) {
     *                 count++;
     *                 sum += it.nextDouble();
     *             }
     *             double average = (count == 0) ? 0 : (sum / (double) count);
     *             return new double[] {count, sum, average};
     *         }
     *     }
     * </code></pre>
     *
     * @param <R> the type of the result
     * @param function  a transforming function
     * @return a result of the transforming function
     * @see Stream#custom(com.annimon.stream.function.Function)
     * @throws NullPointerException if {@code function} is null
     */
    public <R> R custom(final Function<DoubleStream, R> function) {
        Objects.requireNonNull(function);
        return function.apply(this);
    }

    /**
     * Returns a {@code Stream} consisting of the elements of this stream,
     * each boxed to an {@code Double}.
     *
     * <p>This is an lazy intermediate operation.
     *
     * @return a {@code Stream} consistent of the elements of this stream,
     *         each boxed to an {@code Double}
     */
    public Stream<Double> boxed() {
        return Stream.of(iterator);
    }

    /**
     * Returns {@code DoubleStream} with elements that satisfy the given predicate.
     *
     * <p> This is an intermediate operation.
     *
     * <p>Example:
     * <pre>
     * predicate: (a) -&gt; a &gt; 2
     * stream: [1, 2, 3, 4, -8, 0, 11]
     * result: [3, 4, 11]
     * </pre>
     *
     * @param predicate  the predicate used to filter elements
     * @return the new stream
     */
    public DoubleStream filter(final DoublePredicate predicate) {
        return new DoubleStream(new PrimitiveIterator.OfDouble() {

            private double next;

            @Override
            public boolean hasNext() {
                while (iterator.hasNext()) {
                    next = iterator.next();
                    if (predicate.test(next)) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public double nextDouble() {
                return next;
            }
        });
    }

    /**
     * Returns {@code DoubleStream} with elements that does not satisfy the given predicate.
     *
     * <p> This is an intermediate operation.
     *
     * @param predicate  the predicate used to filter elements
     * @return the new stream
     */
    public DoubleStream filterNot(final DoublePredicate predicate) {
        return filter(DoublePredicate.Util.negate(predicate));
    }

    /**
     * Returns an {@code DoubleStream} consisting of the results of applying the given
     * function to the elements of this stream.
     *
     * <p> This is an intermediate operation.
     *
     * <p>Example:
     * <pre>
     * mapper: (a) -&gt; a + 5
     * stream: [1, 2, 3, 4]
     * result: [6, 7, 8, 9]
     * </pre>
     *
     * @param mapper  the mapper function used to apply to each element
     * @return the new stream
     * @see Stream#map(com.annimon.stream.function.Function)
     */
    public DoubleStream map(final DoubleUnaryOperator mapper) {
        return new DoubleStream(new PrimitiveIterator.OfDouble() {

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public double nextDouble() {
                return mapper.applyAsDouble(iterator.nextDouble());
            }
        });
    }

    /**
     * Returns a {@code Stream} consisting of the results of applying the given
     * function to the elements of this stream.
     *
     * <p> This is an intermediate operation.
     *
     * @param <R> the type result
     * @param mapper  the mapper function used to apply to each element
     * @return the new {@code Stream}
     */
    public <R> Stream<R> mapToObj(final DoubleFunction<? extends R> mapper) {
        return Stream.of(new LsaIterator<R>() {

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public R nextIteration() {
                return mapper.apply(iterator.nextDouble());
            }
        });
    }

    /**
     * Returns an {@code IntStream} consisting of the results of applying the given
     * function to the elements of this stream.
     *
     * <p> This is an intermediate operation.
     *
     * @param mapper  the mapper function used to apply to each element
     * @return the new {@code IntStream}
     */
    public IntStream mapToInt(final DoubleToIntFunction mapper) {
        return IntStream.of(new PrimitiveIterator.OfInt() {

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public int nextInt() {
                return mapper.applyAsInt(iterator.nextDouble());
            }
        });
    }

    /**
     * Returns a stream consisting of the results of replacing each element of
     * this stream with the contents of a mapped stream produced by applying
     * the provided mapping function to each element.
     *
     * <p>This is an intermediate operation.
     *
     * <p>Example:
     * <pre>
     * mapper: (a) -&gt; [a, a + 5]
     * stream: [1, 2, 3, 4]
     * result: [1, 6, 2, 7, 3, 8, 4, 9]
     * </pre>
     *
     * @param mapper  the mapper function used to apply to each element
     * @return the new stream
     * @see Stream#flatMap(com.annimon.stream.function.Function)
     */
    public DoubleStream flatMap(final DoubleFunction<? extends DoubleStream> mapper) {
        return new DoubleStream(new PrimitiveIterator.OfDouble() {

            private PrimitiveIterator.OfDouble inner;

            @Override
            public boolean hasNext() {
                if (inner != null && inner.hasNext()) {
                    return true;
                }
                while (iterator.hasNext()) {
                    final double arg = iterator.next();
                    final DoubleStream result = mapper.apply(arg);
                    if (result == null) {
                        continue;
                    }
                    if (result.iterator.hasNext()) {
                        inner = result.iterator;
                        return true;
                    }
                }
                return false;
            }

            @Override
            public double nextDouble() {
                return inner.nextDouble();
            }
        });
    }

    /**
     * Returns a stream consisting of the distinct elements of this stream.
     *
     * <p>This is a stateful intermediate operation.
     *
     * <p>Example:
     * <pre>
     * stream: [1, 4, 2, 3, 3, 4, 1]
     * result: [1, 4, 2, 3]
     * </pre>
     *
     * @return the new stream
     */
    public DoubleStream distinct() {
        return boxed().distinct().mapToDouble(UNBOX_FUNCTION);
    }

    /**
     * Returns a stream consisting of the elements of this stream in sorted order.
     *
     * <p>This is a stateful intermediate operation.
     *
     * <p>Example:
     * <pre>
     * stream: [3, 4, 1, 2]
     * result: [1, 2, 3, 4]
     * </pre>
     *
     * @return the new stream
     */
    public DoubleStream sorted() {
        return new DoubleStream(new PrimitiveExtIterator.OfDouble() {

            private int index = 0;
            private double[] array;

            @Override
            protected void nextIteration() {
                if (!isInit) {
                    array = toArray();
                    Arrays.sort(array);
                }
                hasNext = index < array.length;
                if (hasNext) {
                    next = array[index++];
                }
            }
        });
    }

    /**
     * Returns a stream consisting of the elements of this stream
     * in sorted order as determinated by provided {@code Comparator}.
     *
     * <p>This is a stateful intermediate operation.
     *
     * <p>Example:
     * <pre>
     * comparator: (a, b) -&gt; -a.compareTo(b)
     * stream: [1, 2, 3, 4]
     * result: [4, 3, 2, 1]
     * </pre>
     *
     * @param comparator  the {@code Comparator} to compare elements
     * @return the new {@code DoubleStream}
     */
    public DoubleStream sorted(Comparator<Double> comparator) {
        return boxed().sorted(comparator).mapToDouble(UNBOX_FUNCTION);
    }

    /**
     * Samples the {@code DoubleStream} by emitting every n-th element.
     *
     * <p>This is an intermediate operation.
     *
     * <p>Example:
     * <pre>
     * stepWidth: 3
     * stream: [1, 2, 3, 4, 5, 6, 7, 8]
     * result: [1, 4, 7]
     * </pre>
     *
     * @param stepWidth  step width
     * @return the new {@code DoubleStream}
     * @throws IllegalArgumentException if {@code stepWidth} is zero or negative
     * @see Stream#sample(int)
     */
    public DoubleStream sample(final int stepWidth) {
        if (stepWidth <= 0) throw new IllegalArgumentException("stepWidth cannot be zero or negative");
        if (stepWidth == 1) return this;
        return new DoubleStream(new PrimitiveIterator.OfDouble() {

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public double nextDouble() {
                final double result = iterator.nextDouble();
                int skip = 1;
                while (skip < stepWidth && iterator.hasNext()) {
                    iterator.nextDouble();
                    skip++;
                }
                return result;
            }
        });
    }

    /**
     * Performs provided action on each element.
     *
     * <p>This is an intermediate operation.
     *
     * @param action the action to be performed on each element
     * @return the new stream
     */
    public DoubleStream peek(final DoubleConsumer action) {
        return new DoubleStream(new PrimitiveIterator.OfDouble() {

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public double nextDouble() {
                double value = iterator.nextDouble();
                action.accept(value);
                return value;
            }
        });
    }

    /**
     * Takes elements while the predicate is true.
     *
     * <p>This is an intermediate operation.
     *
     * <p>Example:
     * <pre>
     * predicate: (a) -&gt; a &lt; 3
     * stream: [1, 2, 3, 4, 1, 2, 3, 4]
     * result: [1, 2]
     * </pre>
     *
     * @param predicate  the predicate used to take elements
     * @return the new {@code DoubleStream}
     */
    public DoubleStream takeWhile(final DoublePredicate predicate) {
        return new DoubleStream(new PrimitiveExtIterator.OfDouble() {

            @Override
            protected void nextIteration() {
                hasNext = iterator.hasNext()
                        && predicate.test(next = iterator.next());
            }
        });
    }

    /**
     * Drops elements while the predicate is true and returns the rest.
     *
     * <p>This is an intermediate operation.
     *
     * <p>Example:
     * <pre>
     * predicate: (a) -&gt; a &lt; 3
     * stream: [1, 2, 3, 4, 1, 2, 3, 4]
     * result: [3, 4, 1, 2, 3, 4]
     * </pre>
     *
     * @param predicate  the predicate used to drop elements
     * @return the new {@code DoubleStream}
     */
    public DoubleStream dropWhile(final DoublePredicate predicate) {
        return new DoubleStream(new PrimitiveExtIterator.OfDouble() {

            @Override
            protected void nextIteration() {
                if (!isInit) {
                    // Skip first time
                    while (hasNext = iterator.hasNext()) {
                        next = iterator.next();
                        if (!predicate.test(next)) {
                            return;
                        }
                    }
                }

                hasNext = hasNext && iterator.hasNext();
                if (!hasNext) return;

                next = iterator.next();
            }
        });
    }

    /**
     * Returns a stream consisting of the elements of this stream, truncated
     * to be no longer than {@code maxSize} in length.
     *
     * <p> This is a short-circuiting stateful intermediate operation.
     *
     * <p>Example:
     * <pre>
     * maxSize: 3
     * stream: [1, 2, 3, 4, 5]
     * result: [1, 2, 3]
     *
     * maxSize: 10
     * stream: [1, 2]
     * result: [1, 2]
     * </pre>
     *
     * @param maxSize  the number of elements the stream should be limited to
     * @return the new stream
     * @throws IllegalArgumentException if {@code maxSize} is negative
     */
    public DoubleStream limit(final long maxSize) {
        if (maxSize < 0) throw new IllegalArgumentException("maxSize cannot be negative");
        if (maxSize == 0) return DoubleStream.empty();
        return new DoubleStream(new PrimitiveIterator.OfDouble() {

            private long index = 0;

            @Override
            public boolean hasNext() {
                return (index < maxSize) && iterator.hasNext();
            }

            @Override
            public double nextDouble() {
                index++;
                return iterator.nextDouble();
            }
        });
    }

    /**
     * Skips first {@code n} elements and returns {@code Stream} with remaining elements.
     * If this stream contains fewer than {@code n} elements, then an
     * empty stream will be returned.
     *
     * <p>This is a stateful intermediate operation.
     *
     * <p>Example:
     * <pre>
     * n: 3
     * stream: [1, 2, 3, 4, 5]
     * result: [4, 5]
     *
     * n: 10
     * stream: [1, 2]
     * result: []
     * </pre>
     *
     * @param n  the number of elements to skip
     * @return the new stream
     * @throws IllegalArgumentException if {@code n} is negative
     */
    public DoubleStream skip(final long n) {
        if (n < 0) throw new IllegalArgumentException("n cannot be negative");
        if (n == 0) return this;
        return new DoubleStream(new PrimitiveIterator.OfDouble() {

            private long skippedCount = 0;

            @Override
            public boolean hasNext() {
                while (iterator.hasNext()) {
                    if (skippedCount == n) break;
                    iterator.nextDouble();
                    skippedCount++;
                }
                return iterator.hasNext();
            }

            @Override
            public double nextDouble() {
                return iterator.nextDouble();
            }
        });
    }

    /**
     * Performs an action for each element of this stream.
     *
     * <p>This is a terminal operation.
     *
     * @param action  the action to be performed on each element
     */
    public void forEach(DoubleConsumer action) {
        while (iterator.hasNext()) {
            action.accept(iterator.nextDouble());
        }
    }

    /**
     * Performs a reduction on the elements of this stream, using the provided
     * identity value and an associative accumulation function, and returns the
     * reduced value.
     *
     * <p>The {@code identity} value must be an identity for the accumulator
     * function. This means that for all {@code x},
     * {@code accumulator.apply(identity, x)} is equal to {@code x}.
     * The {@code accumulator} function must be an associative function.
     *
     * <p>This is a terminal operation.
     *
     * <p>Example:
     * <pre>
     * identity: 0
     * accumulator: (a, b) -&gt; a + b
     * stream: [1, 2, 3, 4, 5]
     * result: 15
     * </pre>
     *
     * @param identity  the identity value for the accumulating function
     * @param accumulator  the accumulation function
     * @return the result of the reduction
     * @see #sum()
     * @see #min()
     * @see #max()
     */
    public double reduce(double identity, DoubleBinaryOperator accumulator) {
        double result = identity;
        while (iterator.hasNext()) {
            final double value = iterator.nextDouble();
            result = accumulator.applyAsDouble(result, value);
        }
        return result;
    }

    /**
     * Performs a reduction on the elements of this stream, using an
     * associative accumulation function, and returns an {@code OptionalDouble}
     * describing the reduced value, if any.
     *
     * <p>The {@code accumulator} function must be an associative function.
     *
     * <p>This is a terminal operation.
     *
     * @param accumulator  the accumulation function
     * @return the result of the reduction
     * @see #reduce(com.annimon.stream.function.DoubleBinaryOperator)
     */
    public OptionalDouble reduce(DoubleBinaryOperator accumulator) {
        boolean foundAny = false;
        double result = 0;
        while (iterator.hasNext()) {
            final double value = iterator.nextDouble();
            if (!foundAny) {
                foundAny = true;
                result = value;
            } else {
                result = accumulator.applyAsDouble(result, value);
            }
        }
        return foundAny ? OptionalDouble.of(result) : OptionalDouble.empty();
    }

    /**
     * Returns an array containing the elements of this stream.
     *
     * <p>This is a terminal operation.
     *
     * @return an array containing the elements of this stream
     */
    public double[] toArray() {
        SpinedBuffer.OfDouble b = new SpinedBuffer.OfDouble();
        forEach(b);
        return b.asPrimitiveArray();
    }

    /**
     * Collects elements to {@code supplier} provided container by applying the given accumulation function.
     *
     * <p>This is a terminal operation.
     *
     * @param <R> the type of the result
     * @param supplier  the supplier function that provides container
     * @param accumulator  the accumulation function
     * @return the result of collect elements
     * @see Stream#collect(com.annimon.stream.function.Supplier, com.annimon.stream.function.BiConsumer)
     */
    public <R> R collect(Supplier<R> supplier, ObjDoubleConsumer<R> accumulator) {
        final R result = supplier.get();
        while (iterator.hasNext()) {
            final double value = iterator.nextDouble();
            accumulator.accept(result, value);
        }
        return result;
    }

    /**
     * Returns the sum of elements in this stream.
     *
     * @return the sum of elements in this stream
     */
    public double sum() {
        double sum = 0;
        while (iterator.hasNext()) {
            sum += iterator.nextDouble();
        }
        return sum;
    }

    /**
     * Returns an {@code OptionalDouble} describing the minimum element of this
     * stream, or an empty optional if this stream is empty.
     *
     * <p>This is a terminal operation.
     *
     * @return the minimum element
     */
    public OptionalDouble min() {
        return reduce(new DoubleBinaryOperator() {
            @Override
            public double applyAsDouble(double left, double right) {
                return Math.min(left, right);
            }
        });
    }

    /**
     * Returns an {@code OptionalDouble} describing the maximum element of this
     * stream, or an empty optional if this stream is empty.
     *
     * <p>This is a terminal operation.
     *
     * @return the maximum element
     */
    public OptionalDouble max() {
        return reduce(new DoubleBinaryOperator() {
            @Override
            public double applyAsDouble(double left, double right) {
                return Math.max(left, right);
            }
        });
    }

    /**
     * Returns the count of elements in this stream.
     *
     * <p>This is a terminal operation.
     *
     * @return the count of elements in this stream
     */
    public long count() {
        long count = 0;
        while (iterator.hasNext()) {
            iterator.nextDouble();
            count++;
        }
        return count;
    }

    /**
     * Returns the average of elements in this stream.
     *
     * <p>This is a terminal operation.
     *
     * @return the average of elements in this stream
     */
    public OptionalDouble average() {
        long count = 0;
        double sum = 0d;
        while (iterator.hasNext()) {
            sum += iterator.nextDouble();
            count++;
        }
        if (count == 0) return OptionalDouble.empty();
        return OptionalDouble.of(sum / (double) count);
    }

    /**
     * Tests whether all elements match the given predicate.
     * May not evaluate the predicate on all elements if not necessary
     * for determining the result. If the stream is empty then
     * {@code false} is returned and the predicate is not evaluated.
     *
     * <p>This is a short-circuiting terminal operation.
     *
     * <p>Example:
     * <pre>
     * predicate: (a) -&gt; a == 5
     * stream: [1, 2, 3, 4, 5]
     * result: true
     *
     * predicate: (a) -&gt; a == 5
     * stream: [5, 5, 5]
     * result: true
     * </pre>
     *
     * @param predicate  the predicate used to match elements
     * @return {@code true} if any elements of the stream match the provided
     *         predicate, otherwise {@code false}
     */
    public boolean anyMatch(DoublePredicate predicate) {
        while (iterator.hasNext()) {
            if (predicate.test(iterator.nextDouble()))
                return true;
        }
        return false;
    }

    /**
     * Tests whether all elements match the given predicate.
     * May not evaluate the predicate on all elements if not necessary for
     * determining the result. If the stream is empty then {@code true} is
     * returned and the predicate is not evaluated.
     *
     * <p>This is a short-circuiting terminal operation.
     *
     * <p>Example:
     * <pre>
     * predicate: (a) -&gt; a == 5
     * stream: [1, 2, 3, 4, 5]
     * result: false
     *
     * predicate: (a) -&gt; a == 5
     * stream: [5, 5, 5]
     * result: true
     * </pre>
     *
     * @param predicate  the predicate used to match elements
     * @return {@code true} if either all elements of the stream match the
     *         provided predicate or the stream is empty, otherwise {@code false}
     */
    public boolean allMatch(DoublePredicate predicate) {
        while (iterator.hasNext()) {
            if (!predicate.test(iterator.nextDouble()))
                return false;
        }
        return true;
    }

    /**
     * Tests whether no elements match the given predicate.
     * May not evaluate the predicate on all elements if not necessary for
     * determining the result. If the stream is empty then {@code true} is
     * returned and the predicate is not evaluated.
     *
     * <p>This is a short-circuiting terminal operation.
     *
     * <p>Example:
     * <pre>
     * predicate: (a) -&gt; a == 5
     * stream: [1, 2, 3, 4, 5]
     * result: false
     *
     * predicate: (a) -&gt; a == 5
     * stream: [1, 2, 3]
     * result: true
     * </pre>
     *
     * @param predicate  the predicate used to match elements
     * @return {@code true} if either no elements of the stream match the
     *         provided predicate or the stream is empty, otherwise {@code false}
     */
    public boolean noneMatch(DoublePredicate predicate) {
        if (!iterator.hasNext()) return true;
        while (iterator.hasNext()) {
            if (predicate.test(iterator.nextDouble()))
                return false;
        }
        return true;
    }

    /**
     * Returns the first element wrapped by {@code OptionalDouble} class.
     * If stream is empty, returns {@code OptionalDouble.empty()}.
     *
     * <p>This is a short-circuiting terminal operation.
     *
     * @return an {@code OptionalDouble} with first element
     *         or {@code OptionalDouble.empty()} if stream is empty
     */
    public OptionalDouble findFirst() {
        if (iterator.hasNext()) {
            return OptionalDouble.of(iterator.nextDouble());
        }
        return OptionalDouble.empty();
    }

    /**
     * Returns the single element of stream.
     * If stream is empty, throws {@code NoSuchElementException}.
     * If stream contains more than one element, throws {@code IllegalStateException}.
     *
     * <p>This is a short-circuiting terminal operation.
     *
     * <p>Example:
     * <pre>
     * stream: []
     * result: NoSuchElementException
     *
     * stream: [1]
     * result: 1
     *
     * stream: [1, 2, 3]
     * result: IllegalStateException
     * </pre>
     *
     * @return single element of stream
     * @throws NoSuchElementException if stream is empty
     * @throws IllegalStateException if stream contains more than one element
     */
    public double single() {
        if (!iterator.hasNext()) {
            throw new NoSuchElementException("DoubleStream contains no element");
        }

        final double singleCandidate = iterator.next();
        if (iterator.hasNext()) {
            throw new IllegalStateException("DoubleStream contains more than one element");
        }
        return singleCandidate;
    }

    /**
     * Returns the single element wrapped by {@code OptionalDouble} class.
     * If stream is empty, returns {@code OptionalDouble.empty()}.
     * If stream contains more than one element, throws {@code IllegalStateException}.
     *
     * <p>This is a short-circuiting terminal operation.
     *
     * <p>Example:
     * <pre>
     * stream: []
     * result: OptionalDouble.empty()
     *
     * stream: [1]
     * result: OptionalDouble.of(1)
     *
     * stream: [1, 2, 3]
     * result: IllegalStateException
     * </pre>
     *
     * @return an {@code OptionalDouble} with single element
     *         or {@code OptionalDouble.empty()} if stream is empty
     * @throws IllegalStateException if stream contains more than one element
     */
    public OptionalDouble findSingle() {
        if (!iterator.hasNext()) {
            return OptionalDouble.empty();
        }

        final double singleCandidate = iterator.next();
        if (iterator.hasNext()) {
            throw new IllegalStateException("DoubleStream contains more than one element");
        }
        return OptionalDouble.of(singleCandidate);
    }


    private static final ToDoubleFunction<Double> UNBOX_FUNCTION = new ToDoubleFunction<Double>() {
        @Override
        public double applyAsDouble(Double t) {
            return t;
        }
    };
}
