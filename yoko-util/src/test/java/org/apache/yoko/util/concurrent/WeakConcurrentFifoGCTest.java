package org.apache.yoko.util.concurrent;

import org.apache.yoko.util.KeyedFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.Invocation;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class WeakConcurrentFifoGCTest {
    // must not use a mock here because it will hold onto all its arguments strongly
    private final KeyedFactory<String, Runnable> factory =  new KeyedFactory<String, Runnable>() {
        public Runnable create(String key) {return cleanup;}
    };

    private static final String[] STRINGS_TO_COPY = "aaa bbb ccc ddd eee fff".split(" ");
    private static final String LONG_LIVED_STRING = "ZZZ";
    // must not use the @Mock annotation and Mockito's injection
    // because it intermittently fails to count invocations correctly
    private Runnable cleanup;
    private List<String> releasableElements;
    private List<String> expectedElements;
    private WeakConcurrentFifo<String> fifo;


    @Before
    public void setup() {
        cleanup = mock(Runnable.class);
        fifo = new WeakConcurrentFifo<>(factory);
        expectedElements = new ArrayList<>();
        releasableElements = new ArrayList<>();
        for (String s : STRINGS_TO_COPY) {
            expectedElements.add(s);
            s = new String(s); // ensure each string is a distinct GC'able object
            fifo.put(s);
            releasableElements.add(s);
        }
        expectedElements.add(LONG_LIVED_STRING);
        fifo.put(LONG_LIVED_STRING);
    }

    @Test
    public void testWeakRefsGetCollectedOnRemove() throws Exception {
        assertEquals(expectedElements.size(), fifo.size());
        assertEquals(expectedElements.remove(0), fifo.remove());
        assertEquals(expectedElements.size(), fifo.size());

        releasableElements.clear();

        int collectedElements = 0;
        System.out.println("Testing WeakConcurrentFifo.remove() after GC");
        do {
            driveGC();
            verify(cleanup, times(collectedElements)).run();

            // Some of the elements may now have been GC'd
            // Find the first remaining elephant
            String elem = fifo.remove();
            System.out.println("\tremoved " + elem);


            assertThat(expectedElements, hasItem(elem));

            while(!expectedElements.remove(0).equals(elem)) {
                collectedElements ++;
            }
        } while (!expectedElements.isEmpty());

        verify(cleanup, times(collectedElements)).run();
        assertThat(fifo.remove(), is(nullValue()));
        assertThat(collectedElements, is(not(0)));
    }

    @Test
    public void testWeakRefsGetCollectedOnPeek() throws Exception {
        assertEquals(expectedElements.size(), fifo.size());

        expectedElements.removeAll(releasableElements);
        releasableElements.clear();

        int collectedElements = 0;
        System.out.println("Testing WeakConcurrentFifo.peek() after GC");
        do {
            driveGC();
            // there should be no new invocations of cleanup.run()
            verify(cleanup, times(collectedElements)).run();
            // fifo.peek() should drive cleanup
            fifo.peek();
            // count how many elements were cleaned up by all the peek() calls so far
            Collection<Invocation> invocations = Mockito.mockingDetails(cleanup).getInvocations();
            collectedElements = invocations.size();
        } while (collectedElements < STRINGS_TO_COPY.length);

        verify(cleanup, times(collectedElements)).run();
        assertThat(fifo.remove(), is(LONG_LIVED_STRING));
    }

    @Test
    public void testWeakRefsGetCollectedOnPut() throws Exception {
        assertEquals(expectedElements.size(), fifo.size());

        expectedElements.removeAll(releasableElements);
        releasableElements.clear();

        int collectedElements = 0;
        System.out.println("Testing WeakConcurrentFifo.put() after GC");
        do {
            driveGC();
            // there should be no new invocations of cleanup.run()
            verify(cleanup, times(collectedElements)).run();
            // fifo.put() should drive cleanup
            fifo.put(LONG_LIVED_STRING);
            // count how many elements were cleaned up by all the put() calls so far
            Collection<Invocation> invocations = Mockito.mockingDetails(cleanup).getInvocations();
            collectedElements = invocations.size();
        } while (collectedElements < STRINGS_TO_COPY.length);

        verify(cleanup, times(collectedElements)).run();
        assertThat(fifo.remove(), is(LONG_LIVED_STRING));
    }

    @Test
    public void testWeakRefsGetCollectedOnSize() throws Exception {
        assertEquals(expectedElements.size(), fifo.size());

        expectedElements.removeAll(releasableElements);
        releasableElements.clear();

        int collectedElements = 0;
        System.out.println("Testing WeakConcurrentFifo.size() after GC");
        do {
            driveGC();
            // there should be no new invocations of cleanup.run()
            verify(cleanup, times(collectedElements)).run();
            // fifo.size() should drive cleanup
            fifo.size();
            // count how many elements were cleaned up by all the size() calls so far
            Collection<Invocation> invocations = Mockito.mockingDetails(cleanup).getInvocations();
            collectedElements = invocations.size();
        } while (collectedElements < STRINGS_TO_COPY.length);

        verify(cleanup, times(collectedElements)).run();
        assertThat(fifo.remove(), is(LONG_LIVED_STRING));
    }

    private void driveGC() {
        WeakReference<?> ref = new WeakReference<>(new Object());
        do {
            System.out.print("gc ");
            System.gc();
        } while (ref.get() != null);
        System.out.println();
    }

}
