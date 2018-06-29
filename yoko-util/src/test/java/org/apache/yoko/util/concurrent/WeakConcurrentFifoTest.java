package org.apache.yoko.util.concurrent;

import org.apache.yoko.util.KeyedFactory;
import org.junit.Before;

public class WeakConcurrentFifoTest extends ConcurrentFifoTest {
    @Before
    @Override
    public void setupFifo() {
        fifo = new WeakConcurrentFifo<>(new KeyedFactory<String, Runnable>() {
            public Runnable create(String key) {
                return new Runnable() {
                    public void run() {}
                };
            }
        });
    }
}
