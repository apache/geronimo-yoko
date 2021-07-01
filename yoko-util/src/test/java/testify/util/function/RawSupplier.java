package testify.util.function;

import org.opentest4j.AssertionFailedError;

@FunctionalInterface
public interface RawSupplier<T> extends java.util.function.Supplier<T> {
    T getRaw() throws Exception;

    default T get() {
        try {
            return getRaw();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new AssertionFailedError("", e);
        }
    }
}
