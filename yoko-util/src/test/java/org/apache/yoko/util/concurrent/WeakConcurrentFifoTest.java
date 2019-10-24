package org.apache.yoko.util.concurrent;

import org.junit.Before;

public class WeakConcurrentFifoTest extends ConcurrentFifoTest {
    @Before
    @Override
    public void setupFifo() {
        fifo = new WeakConcurrentFifo<>(key -> () -> {});
    }
}
