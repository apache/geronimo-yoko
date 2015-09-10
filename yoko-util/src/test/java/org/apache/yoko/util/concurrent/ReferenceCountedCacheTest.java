package org.apache.yoko.util.concurrent;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import org.apache.yoko.util.Cache;
import org.apache.yoko.util.KeyedFactory;
import org.apache.yoko.util.Reference;
import org.junit.After;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;
import java.util.concurrent.*;

@RunWith(MockitoJUnitRunner.class)
public class ReferenceCountedCacheTest {
    private static final ConcurrentLinkedQueue<Integer> createdInts = new ConcurrentLinkedQueue<>();
    private static final ConcurrentLinkedQueue<Integer> deletedInts = new ConcurrentLinkedQueue<>();
    private static class StringToInteger implements KeyedFactory<String, Integer>, Cache.Cleaner<Integer> {
        @Override
        public Integer create(String key) {
            Integer result = new Integer(key);
            createdInts.add(result);
            return result;
        }

        @Override
        public void clean(Integer value) {
            deletedInts.add(value);
        }
    }
    private static class BadFactory implements KeyedFactory<String, Integer> {
        @Override
        public Integer create(String key) {
            throw new UnsupportedOperationException();
        }
    }

    @Spy
    StringToInteger factory;
    @Spy
    BadFactory badFactory;
    ReferenceCountedCache<String, Integer> cache;
    volatile CyclicBarrier startBarrier, endBarrier;
    volatile boolean retrieving = true;

    @After
    public void setup() {
        createdInts.clear();
        deletedInts.clear();
        cache = null;
        retrieving = true;
        startBarrier = endBarrier = null;
    }

    @Test
    public void testGetAndCreate() {
        cache = new ReferenceCountedCache<>(factory, 0, 5);
        assertEquals(null, cache.get("1"));
        try (Reference<Integer> ref = cache.getOrCreate("1", factory);)
        {assertEquals(Integer.valueOf(1), ref.get());}
        try (Reference<Integer> ref = cache.get("1");)
        {assertEquals(Integer.valueOf(1), ref.get());}
        cache.clean();
        assertEquals(null, cache.get("1"));
    }

    @Test
    public void testFailedCreateDoesNotPolluteCache() {
        cache = new ReferenceCountedCache<>(factory, 0, 5);
        assertEquals(null, cache.get("1"));
        try (Reference<Integer> ref = cache.getOrCreate("1", badFactory);) {
            fail("getOrCreate() should throw an exception");
        } catch (UnsupportedOperationException expected) {}

        assertEquals(null, cache.get("1"));

        try (Reference<Integer> ref = cache.getOrCreate("1", factory);)
        {assertEquals(Integer.valueOf(1), ref.get());}
        try (Reference<Integer> ref = cache.get("1");)
        {assertEquals(Integer.valueOf(1), ref.get());}
        cache.clean();
        assertEquals(null, cache.get("1"));
    }

    @Test
    public void testCreateAndClean() {
        cache = new ReferenceCountedCache<>(factory, 3, 5);
        cache.getOrCreate("0", factory).close();
        cache.getOrCreate("1", factory).close();
        assertEquals(2, cache.snapshot().size());
        cache.getOrCreate("2", factory).close();
        assertEquals(3, cache.snapshot().size());
        cache.getOrCreate("3", factory).close();
        assertEquals(4, cache.snapshot().size());
        long cleaned = cache.clean();
        assertEquals(4, cleaned);
        assertEquals(0, cache.snapshot().size());
    }

    @Test
    public void testCreateEntries() {
        cache = new ReferenceCountedCache<>(factory, 0, 0);
        // new entries should result in factory invocations
        cache.getOrCreate("0", factory);
        verify(factory).create("0");
        cache.getOrCreate("1", factory);
        verify(factory).create("1");
        // existing entries should not invoke the factory further
        cache.getOrCreate("0", factory);
        cache.getOrCreate("1", factory);
        verify(factory, times(2)).create(anyString());
        System.out.println(createdInts);
    }

    @Test
    public void testReleaseResults() {
        cache = new ReferenceCountedCache<>(factory, 3, 5);
        Reference<Integer> r0, r1, r2, r3, r4, r5;
        r0 = cache.getOrCreate("0", factory);
        r1 = cache.getOrCreate("1", factory);
        r2 = cache.getOrCreate("2", factory);
        // check the references are to the right values
        assertEquals(Integer.valueOf(0), r0.get());
        assertEquals(Integer.valueOf(1), r1.get());
        assertEquals(Integer.valueOf(2), r2.get());
        // check the size is correct
        assertEquals(3, cache.size());
        assertEquals(0, cache.idleCount());
        r0.close();
        // after releasing one ref, we should see only the unused count go up.
        assertEquals(3, cache.size());
        assertEquals(1, cache.idleCount());
        r1.close();

        assertEquals(3, cache.size());
        assertEquals(2, cache.idleCount());

        // this should do nothing
        cache.clean();

        assertEquals(3, cache.size());
        assertEquals(2, cache.idleCount());

        // this should force a cleanup
        r3 = cache.getOrCreate("3", factory);
        cache.clean();
        assertEquals(2, cache.size());
        assertEquals(0, cache.idleCount());

        // check the expected methods were called on the factory
        verify(factory).clean(0);
        verify(factory).clean(1);
        verify(factory, times(2)).clean(anyInt());
    }

    @Test
    public void testMultiThreaded() throws Exception {
        cache = new ReferenceCountedCache<>(factory, 15, 7);
        int retrievers = 50;
        int cleaners = 5;
        startBarrier = new CyclicBarrier(retrievers + 1);
        endBarrier = new CyclicBarrier(retrievers);
        retrieving = true;
        ExecutorService xs = Executors.newFixedThreadPool(retrievers + cleaners);
        List<Future<List<Integer>>> retrievals = new ArrayList<>();
        List<Future<Long>> cleanTallies = new ArrayList<>();

        for (int i = 0; i < retrievers; i++)
            retrievals.add(xs.submit(new Retriever(20)));
        for (int i = 0; i < cleaners; i++)
            cleanTallies.add(xs.submit(new Cleaner()));

        startBarrier.await();
        long cleaned = 0;
        for (Future<Long> cleanTally : cleanTallies)
            cleaned += cleanTally.get();

        Set<Integer> results = newIdentityHashSet();
        for (Future<List<Integer>> retrieval : retrievals)
            results.addAll(retrieval.get());

        Set<Integer> created = newIdentityHashSet(createdInts);

        Set<Integer> deleted = newIdentityHashSet(deletedInts);

        System.out.printf("%ncreated %d values", created.size());
        System.out.printf("%ndeleted %d values", deleted.size());
        System.out.printf("%nfetched %d values", results.size());
        System.out.printf("%ncleaned %d values", cleaned);
        System.out.printf("%nremaining entries: %s%n", cache.snapshot());

        assertEquals(deleted.size(), cleaned);
        assertEquals(created, unionByIdentity(deleted, cache.snapshot().values()));
        assertEquals(created, results);
    }

    private static <T> Set<T> newIdentityHashSet() {
        return Collections.newSetFromMap(new IdentityHashMap<T, Boolean>());
    }

    private static <T> Set<T> newIdentityHashSet(Collection<? extends T> c) {
        Set<T> result = newIdentityHashSet();
        result.addAll(c);
        return result;
    }

    private static <T> Set<T> unionByIdentity(Collection<T>...collections) {
        Set<T> result = newIdentityHashSet();
        for(Collection<T> c : collections)
            result.addAll(c);
        return result;
    }


    class Retriever implements Callable<List<Integer>> {
        final int bound;
        final Random random = new Random();

        Retriever(int bound) { this.bound = bound; }

        @Override
        public List<Integer> call() throws Exception {
            List<Integer> list = new ArrayList<>();
            try {
                startBarrier.await();
                for (int i = 0; i < 1_000; i++) {
                    try (Reference<Integer> ref = cache.getOrCreate("" + random.nextInt(bound), factory)) {
                        list.add(ref.get());
                    }
                }
                endBarrier.await();
                retrieving = false;
            } catch (Throwable t) {
                System.out.printf("Retriever aborted with %s.%n", t);
                t.printStackTrace(System.out);
            }
            return list;

        }
    }

    class Cleaner implements Callable<Long> {
        @Override
        public Long call() throws Exception {
            long cleaned = 0;
            while (retrieving) cleaned += cache.clean();
            return cleaned;
        }
    }
}
