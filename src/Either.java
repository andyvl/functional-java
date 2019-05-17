package eu.europa.ec.taxud.ics2.sti.persistence.test.common;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents a value of one of two possible types (a disjoint union).
 * An instance of {@code Either }is an instance of either {@link Left} or {@link Right}.
 *
 * A common use of {@code Either} is as an alternative to {@link java.util.Optional} for dealing
 * with possibly missing values or operations that may fail; failure is recorded in {@code Left}
 * @param <E> - The type of left value.
 * @param <A> - The type of right value.
 */
public abstract class Either<E, A> {

    public static <R,T> Either<R,T> of(Supplier<T> s) {
        try {
            return right(s.get());
        } catch (Exception e) {
            return left((R) e);
        }
    }

    public static <R,T> Either<R,T> of(Runnable s) {
        try {
            s.run();
            return right(null);
        } catch (Exception e) {
            return left((R) e);
        }
    }

    public static <E, A> Either<E, A> left(E value) {
        return new Left<>(value);
    }

    static <E, A> Either<E, A> right(A value) {
        return new Right<>(value);
    }

    public abstract E left();

    public abstract A right();

    public abstract boolean isLeft();

    public abstract boolean isRight();

    public abstract <B> Either<E,B> map(Function<A,B> f);
    public abstract <B> Either<E,B> flatMap(Function<A,Either<E,B>> f);

    public <B, C> Either<E, C> map2(Either<E, B> b, Function<A, Function<B, C>> f) {
        return flatMap(a -> b.map(b1 -> f.apply(a).apply(b1)));
    }

    public static class Left<E,A> extends Either<E,A> {

        private final E value;

        private Left(E value) {
            this.value = value;
        }


        @Override
        public E left() {
            return value;
        }

        @Override
        public A right() {
            throw new  IllegalStateException("getRight called on Left");
        }

        @Override
        public boolean isLeft() {
            return true;
        }

        @Override
        public boolean isRight() {
            return false;
        }

        @Override
        public <B> Either<E, B> map(Function<A, B> f) {
            return new Left<>(value);
        }

        @Override
        public <B> Either<E, B> flatMap(Function<A, Either<E, B>> f) {
            return new Left<>(value);
        }
    }

    public static class Right<E, A> extends Either<E,A> {
        private final A value;

        private Right(A value) {
            this.value = value;
        }

        @Override
        public E left() {
            throw new  IllegalStateException("getLeft called on Right");
        }

        @Override
        public A right() {
            return value;
        }

        @Override
        public boolean isLeft() {
            return false;
        }

        @Override
        public boolean isRight() {
            return true;
        }

        @Override
        public <B> Either<E, B> map(Function<A, B> f) {
            return new Right<>(f.apply(value));
        }

        @Override
        public <B> Either<E, B> flatMap(Function<A, Either<E, B>> f) {
            return f.apply(value);
        }

        public Either<E, A> orElse(Supplier<Either<E, A>> a) {
            return this;
        }

        @Override
        public String toString() {
            return "Right{" + "value=" + value + '}';
        }
    }
}
