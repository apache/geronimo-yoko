package testify.util.function;

import org.opentest4j.AssertionFailedError;

import java.util.Objects;

@FunctionalInterface
public interface RawPredicate<T> extends java.util.function.Predicate<T> {
    boolean testRaw(T t) throws Exception;

    default boolean test(T t) {
        try {
            return testRaw(t);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new AssertionFailedError("", e);
        }

    }

    default RawPredicate<T> negate() { return t -> !this.testRaw(t); }

    default RawPredicate<T> and(RawPredicate<? super T> that) {
        return t -> this.testRaw(t) && that.testRaw(t);
    }

    default RawPredicate<T> or(RawPredicate<? super T> that) {
        return t -> this.testRaw(t) || that.testRaw(t);
    }

    static <T> RawPredicate<T> isEqual(Object targetRef) {
        return t -> Objects.equals(targetRef, t);
    }
}
