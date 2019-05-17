import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Monadic Try type, like the one in scala
 */
public abstract class Try<T> {

    protected Try(){/*avoid direct instantiation*/}

    public static <U> Try<U> of(Supplier<U> f) {
        try {
            return Success.of(f.get());
        } catch (Throwable t) {
            return Failure.of(t);
        }
    }

    public abstract <U> Try<U> map(Function<? super T, ? extends U> f);
    public abstract <U> Try<U> flatMap(Function<? super T, Try<U>> f);
    public abstract T recover(Function<? super Throwable, T> f);
    public abstract Try<T> recoverWith(Function<? super Throwable, Try<T>> f);
    public abstract T orElse(T value);
    public abstract Try<T> orElseTry(Supplier<T> f);
    public abstract boolean isSuccess();
    public abstract Optional<T> toOptional();

    public abstract T getUnchecked();


    public static class Success<U> extends Try<U> {
        private final U value;

        private Success(U value) {
            this.value = value;
        }

        public static <R> Success<R> of(R value) {
            return new Success<>(value);
        }


        @Override
        public <V> Try<V> map(Function<? super U, ? extends V> f) {
            try {
                return of(f.apply(value));
            }catch(Throwable t) {
                return Failure.of(t);
            }
        }

        @Override
        public <V> Try<V> flatMap(Function<? super U, Try<V>> f) {
            try {
                return f.apply(value);
            } catch (Throwable t){
                return Failure.of(t);
            }
        }

        @Override
        public U recover(Function<? super Throwable, U> f) {
            return value;
        }

        @Override
        public Try<U> recoverWith(Function<? super Throwable, Try<U>> f) {
            return this;
        }

        @Override
        public U orElse(U value) {
            return this.value;
        }

        @Override
        public Try<U> orElseTry(Supplier<U> f) {
            return this;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public Optional<U> toOptional() {
            return Optional.ofNullable(value);
        }

        @Override
        public U getUnchecked() {
            return value;
        }
    }

    public static class Failure<U> extends Try<U>{
        private final Throwable exception;

        private Failure(Throwable exception) {
            this.exception = exception;
        }

        public static <R> Failure<R> of(Throwable value) {
            return new Failure<>(value);
        }


        @Override
        public <V> Try<V> map(Function<? super U, ? extends V> f) {
            return of(exception);
        }

        @Override
        public <V> Try<V> flatMap(Function<? super U, Try<V>> f) {
            return of(exception);
        }

        @Override
        public U recover(Function<? super Throwable, U> f) {
            return f.apply(exception);
        }

        @Override
        public Try<U> recoverWith(Function<? super Throwable, Try<U>> f) {
            return f.apply(exception);
        }

        @Override
        public U orElse(U value) {
            return value;
        }

        @Override
        public Try<U> orElseTry(Supplier<U> f) {
            return Try.of(f);
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public Optional<U> toOptional() {
            return Optional.empty();
        }

        @Override
        public U getUnchecked() {
            throw new RuntimeException(exception);
        }

        public Throwable unwrap(){
            return exception;
        }
    }
}
