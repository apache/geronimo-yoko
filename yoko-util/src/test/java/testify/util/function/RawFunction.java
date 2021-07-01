package testify.util.function;

import org.opentest4j.AssertionFailedError;

@FunctionalInterface
public interface RawFunction<T, R> extends java.util.function.Function<T, R> {
    R applyRaw(T t) throws Exception;

    default R apply(T t) {
        try {
            return applyRaw(t);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new AssertionFailedError("", e);
        }

    }

    default RawSupplier<R> curry(T t) {
        return () -> applyRaw(t);
    }
}
