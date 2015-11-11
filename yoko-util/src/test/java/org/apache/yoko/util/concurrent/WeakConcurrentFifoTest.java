package org.apache.yoko.util.concurrent;

import org.apache.yoko.util.KeyedFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class WeakConcurrentFifoTest extends ConcurrentFifoTest {
    // must not use a mock here because it will hold onto all its arguments strongly
    private final KeyedFactory<String, Runnable> factory =  new KeyedFactory<String, Runnable>() {
        public Runnable create(String key) {return cleanup;}
    };

    // must not use the @Mock annotation and Mockito's injection
    // because it intermittently fails to count invocations correctly
    private Runnable cleanup;
    private ReferenceQueue<?> referenceQueue = new ReferenceQueue<>();

    @Before
    @Override
    public void setupFifo() {
        cleanup = mock(Runnable.class);
        fifo = new WeakConcurrentFifo<>(factory);
    }

    @Test
    public void testWeakRefsGetCollectedOnRemove() throws Exception {
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
    public void testWeakRefsGetCollectedOnPut() throws Exception {
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
    public void testWeakRefsGetCollectedOnSize() throws Exception {
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
            refs[i] = new WeakReference(s, referenceQueue);
            fifo.put(s);
        }
        return refs;
    }

    public void gcUntilCleared(WeakReference<?>... refs) throws Exception {
        for (WeakReference<?> ref : refs) {
            gcUntilCollected(ref);
            referenceQueue.remove();
        }
    }

    private static void gcUntilCollected(WeakReference<?> ref) {
        while (ref.get() != null) {
            System.out.print("gc ");
            System.gc();
        }
        System.out.println();
    }
}
