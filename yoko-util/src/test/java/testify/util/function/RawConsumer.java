package testify.util.function;

import org.opentest4j.AssertionFailedError;

@FunctionalInterface
public interface RawConsumer<T> extends java.util.function.Consumer<T> {
    void acceptRaw(T t) throws Exception;

    default void accept(T t) {
        try {
            acceptRaw(t);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new AssertionFailedError("", e);
        }
    }

    default RawConsumer<T> andThen(RawConsumer<? super T> after) {
        return t -> {
            acceptRaw(t);
            after.acceptRaw(t);
        };
    }
}
