package testify.util;

import java.util.concurrent.atomic.AtomicInteger;

public enum ObjectUtil {
    ;
    private static final ClassValue<AtomicInteger> INSTANCE_COUNT = new ClassValue<AtomicInteger>() {
        protected AtomicInteger computeValue(Class<?> type) { return new AtomicInteger(); }
    };

    public static String getNextObjectLabel(Class<?> type) {
        final AtomicInteger counter = INSTANCE_COUNT.get(type);
        return String.format("%s[%d]", type.getSimpleName(), counter.incrementAndGet());
    }
}