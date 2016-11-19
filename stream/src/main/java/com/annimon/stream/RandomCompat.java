package com.annimon.stream;

import com.annimon.stream.function.DoubleSupplier;
import com.annimon.stream.function.IntSupplier;
import java.util.Random;

/**
 * Backported stream apis from {@link java.util.Random} class.
 */
@SuppressWarnings("WeakerAccess")
public final class RandomCompat {

    private final Random random;

    /**
     * Constructs object, inner {@code random} created with default constructor.
     */
    public RandomCompat() {
        this.random = new Random();
    }

    /**
     * Constructs object, inner {@code random} created with seed passed as param.
     *
     * @param seed  seed to initialize {@code random} object
     */
    public RandomCompat(long seed) {
        this.random = new Random(seed);
    }

    /**
     * Constructs object with the given {@code Random} instance.
     *
     * @param random  {@code Random} instance
     */
    public RandomCompat(Random random) {
        this.random = random;
    }

    /**
     * Returns underlying {@link java.util.Random} instance.
     *
     * @return {@link java.util.Random} object instance
     */
    public Random getRandom() {
        return random;
    }

    /**
     * Returns a stream producing the given {@code streamSize} number of
     * pseudorandom {@code int} values.
     *
     * <p>A pseudorandom {@code int} value is generated as if it's the result of
     * calling the method {@link Random#nextInt()}
     *
     * @param streamSize the number of values to generate
     * @return a stream of pseudorandom {@code int} values
     * @throws IllegalArgumentException if {@code streamSize} is
     *         less than zero
     */
    public IntStream ints(long streamSize) {
        if (streamSize < 0L) throw new IllegalArgumentException();
        if (streamSize == 0L) {
            return IntStream.empty();
        }
        return ints().limit(streamSize);
    }

    /**
     * Returns a stream producing the given {@code streamSize} number of
     * pseudorandom {@code double} values, each between zero (inclusive)
     * and one (exclusive).
     *
     * <p>A pseudorandom {@code int} value is generated as if it's the result of
     * calling the method {@link Random#nextDouble()}
     *
     * @param streamSize  the number of values to generate
     * @return a stream of pseudorandom {@code double} values
     * @throws IllegalArgumentException if {@code streamSize} is
     *         less than zero
     */
    public DoubleStream doubles(long streamSize) {
        if (streamSize < 0L) throw new IllegalArgumentException();
        if (streamSize == 0L) {
            return DoubleStream.empty();
        }
        return doubles().limit(streamSize);
    }


    /**
     * Returns an effectively unlimited stream of pseudorandom {@code int}
     * values.
     *
     * <p>A pseudorandom {@code int} value is generated as if it's the result of
     * calling the method {@link Random#nextInt()}.
     *
     * @return a stream of pseudorandom {@code int} values
     */
    public IntStream ints() {
        return IntStream.generate(new IntSupplier() {
            @Override
            public int getAsInt() {
                return random.nextInt();
            }
        });
    }

    /**
     * Returns an effectively unlimited stream of pseudorandom {@code double} values,
     * each between zero (inclusive) and one (exclusive).
     *
     * <p>A pseudorandom {@code double} value is generated as if it's the result of
     * calling the method {@link Random#nextDouble()}.
     *
     * @return a stream of pseudorandom {@code double} values
     */
    public DoubleStream doubles() {
        return DoubleStream.generate(new DoubleSupplier() {
            @Override
            public double getAsDouble() {
                return random.nextDouble();
            }
        });
    }

    /**
     * Returns a stream producing the given {@code streamSize} number
     * of pseudorandom {@code int} values, each conforming to the given
     * origin (inclusive) and bound (exclusive).
     *
     * @param streamSize the number of values to generate
     * @param randomNumberOrigin  the origin (inclusive) of each random value
     * @param randomNumberBound  the bound (exclusive) if each random value
     * @return a stream of pseudorandom {@code int} values,
     *         each with the given origin (inclusive) and bound (exclusive)
     * @throws IllegalArgumentException if {@code streamSize} is
     *         less than zero, or {@code randomNumberOrigin} is
     *         greater than or equal to {@code randomNumberBound}
     */
    public IntStream ints(long streamSize, final int randomNumberOrigin, final int randomNumberBound) {
        if (streamSize < 0L) throw new IllegalArgumentException();
        if (streamSize == 0L) {
            return IntStream.empty();
        }
        return ints(randomNumberOrigin, randomNumberBound).limit(streamSize);
    }

    /**
     * Returns a stream producing the given {@code streamSize} number
     * of pseudorandom {@code double} values, each conforming
     * to the given origin (inclusive) and bound (exclusive).
     *
     * @param streamSize the number of values to generate
     * @param randomNumberOrigin  the origin (inclusive) of each random value
     * @param randomNumberBound  the bound (exclusive) if each random value
     * @return a stream of pseudorandom {@code double} values,
     *         each with the given origin (inclusive) and bound (exclusive)
     * @throws IllegalArgumentException if {@code streamSize} is
     *         less than zero, or {@code randomNumberOrigin} is
     *         greater than or equal to {@code randomNumberBound}
     */
    public DoubleStream doubles(long streamSize,
            final double randomNumberOrigin, final double randomNumberBound) {
        if (streamSize < 0L) throw new IllegalArgumentException();
        if (streamSize == 0L) {
            return DoubleStream.empty();
        }
        return doubles(randomNumberOrigin, randomNumberBound).limit(streamSize);
    }

    /**
     * Returns an effectively unlimited stream of pseudorandom {@code int}
     * values, each conforming to the given origin (inclusive) and bound (exclusive)
     *
     * @param randomNumberOrigin  the origin (inclusive) of each random value
     * @param randomNumberBound  the bound (exclusive) of each random value
     * @return a stream of pseudorandom {@code int} values,
     *         each with the given origin (inclusive) and bound (exclusive)
     * @throws IllegalArgumentException if {@code randomNumberOrigin}
     *         is greater than or equal to {@code randomNumberBound}
     */
    public IntStream ints(final int randomNumberOrigin, final int randomNumberBound) {
        if (randomNumberOrigin >= randomNumberBound) {
            throw new IllegalArgumentException();
        }
        return IntStream.generate(new IntSupplier() {

            private final int bound = randomNumberBound - randomNumberOrigin;

            @Override
            public int getAsInt() {
                return randomNumberOrigin + random.nextInt(bound);
            }
        });
    }

    /**
     * Returns an effectively unlimited stream of pseudorandom {@code double}
     * values, each conforming to the given origin (inclusive) and bound (exclusive)
     *
     * @param randomNumberOrigin  the origin (inclusive) of each random value
     * @param randomNumberBound  the bound (exclusive) of each random value
     * @return a stream of pseudorandom {@code double} values,
     *         each with the given origin (inclusive) and bound (exclusive)
     * @throws IllegalArgumentException if {@code randomNumberOrigin}
     *         is greater than or equal to {@code randomNumberBound}
     */
    public DoubleStream doubles(final double randomNumberOrigin, final double randomNumberBound) {
        if (randomNumberOrigin >= randomNumberBound) {
            throw new IllegalArgumentException();
        }
        return DoubleStream.generate(new DoubleSupplier() {

            private final double bound = randomNumberBound - randomNumberOrigin;

            @Override
            public double getAsDouble() {
                double result = random.nextDouble() * bound + randomNumberOrigin;
                if (result >= randomNumberBound) {
                    result = Double.longBitsToDouble(Double.doubleToLongBits(randomNumberBound) - 1);
                }
                return result;
            }
        });
    }

}
