package org.apache.yoko.util.concurrent;

import org.apache.yoko.util.KeyedFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.ref.WeakReference;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class WeakConcurrentFifoTest extends ConcurrentFifoTest {
    private KeyedFactory<String, Runnable> factory =  new KeyedFactory<String, Runnable>() {
        public Runnable create(String key) {
            return cleanup;
        }
    };

    @Mock
    private Runnable cleanup;

    @Override
    @Before
    public void setupFifo() {
        fifo = new WeakConcurrentFifo<>(factory);
    }

    @Test
    public void testWeakRefsGetCollectedOnRemove() {
        WeakReference[] refs;

        refs = enqueueStringsCollectably("foo", "foo", "bar", "bar", "bar", "bar");
        fifo.put("baz"); // strings in constant pool are never GC'd

        assertEquals(refs.length + 1, fifo.size());
        assertEquals("foo", fifo.remove());
        assertEquals(refs.length, fifo.size());

        gcUntilCleared(refs);

        verify(cleanup, times(0)).run();
        assertEquals("baz", fifo.remove());
        verify(cleanup, times(refs.length - 1)).run();
    }

    @Test
    public void testWeakRefsGetCollectedOnPut() {
        WeakReference[] refs;

        refs = enqueueStringsCollectably("foo", "foo", "bar", "bar", "bar", "bar");

        assertEquals(refs.length, fifo.size());
        assertEquals("foo", fifo.remove());
        assertEquals(refs.length - 1, fifo.size());

        gcUntilCleared(refs);

        verify(cleanup, times(0)).run();
        fifo.put("baz");
        verify(cleanup, times(refs.length - 1)).run();
        assertEquals("baz", fifo.remove());
    }

    @Test
    public void testWeakRefsGetCollectedOnSize() {
        WeakReference[] refs;

        refs = enqueueStringsCollectably("foo", "foo", "bar", "bar", "bar", "bar");

        assertEquals(refs.length, fifo.size());
        assertEquals("foo", fifo.remove());
        assertEquals(refs.length - 1, fifo.size());

        gcUntilCleared(refs);

        verify(cleanup, times(0)).run();
        assertEquals(0, fifo.size());
        verify(cleanup, times(refs.length - 1)).run();
    }

    private WeakReference[] enqueueStringsCollectably(String... strings) {
        WeakReference[] refs = new WeakReference[strings.length];
        for (int i = 0 ; i < strings.length; i++) {
            String s = new String(strings[i]);
            refs[i] = new WeakReference(s);
            fifo.put(s);
        }
        return refs;
    }

    public static void gcUntilCleared(WeakReference<?>... refs) {
        for (WeakReference<?> ref : refs) {
            while (ref.get() != null) {
                System.out.print("gc ");
                System.gc();
            }
            System.out.println();
        }
    }
}
