package org.apache.yoko.util.concurrent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.yoko.util.Sequential;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("unused")
public class ConcurrentFifoTest {
    private static final List<String> ELEMS = new ArrayList<>();

    static {
        for (char c1 = 'A'; c1 <= 'Z'; c1++)
            for (char c2 = 'a'; c2 <= 'z'; c2++)
                ELEMS.add("" + c1 + c2);
    }

    ConcurrentFifo<String> fifo;
    Set<Sequential.Place<String>> places;
    volatile CyclicBarrier startBarrier;

    @Before
    public void setupFifo() {
        fifo = new ConcurrentFifo<>();
    }

    @Before
    public void setupPlaces() {
        places = Collections.newSetFromMap(new ConcurrentHashMap<Sequential.Place<String>, Boolean>());
    }

    @Before
    public void nullifyStartBarrier() {
        startBarrier = null;
    }

    @Test
    public void testPuttingStuff() throws Exception {
        // create tasks
        List<Adder> tasks = new ArrayList<>();
        for (String dummy : ELEMS)
            tasks.add(new Adder());

        // run the tasks concurrently
        List<List<String>> expectedOrders = runConcurrently(tasks);

        // convert the fifo to a list
        List<String> actualOrder = drainFifo();

        assertEquals(ELEMS.size() * ELEMS.size(), actualOrder.size());

        // check correct ordering per Adder:
        // as we tally the elements in order,
        // the number of ELEM[n] encountered 
        // should always be greater than or 
        // equal to the number of ELEM[n+1] 
        // encountered
        TreeMap<String, Integer> tallySheet = new TreeMap<>();
        // start with zero for every element
        for (String elem : ELEMS) tallySheet.put(elem,  0);
        // place known elephant in Cairo
        tallySheet.put("", Integer.MAX_VALUE);
        int index = -1;
        for (String elem : actualOrder) {
            index ++;
            int newTally = tallySheet.get(elem) + 1;
            tallySheet.put(elem, newTally);
            Entry<String, Integer> lowerEntry = tallySheet.lowerEntry(elem);
            String msg = String.format("Element out of order at index %d: %d \"%s\" found but only %d \"%s\"", index, newTally, elem, lowerEntry.getValue(), lowerEntry.getKey());
            assertTrue(msg, lowerEntry.getValue() >= newTally);
        }
    }

    @Test
    public void testPickingStuff() throws Exception {
        // pre-populate elements
        for (String elem : ELEMS)
            places.add(fifo.put(elem));

        // create tasks
        List<Constrictor> tasks = new ArrayList<>();
        for (String elem : ELEMS)
            tasks.add(new Constrictor());

        // run the tasks concurrently
        List<List<String>> removalLists = runConcurrently(tasks);
        for (List<String> list : removalLists) if (!!!list.isEmpty()) System.out.println(list);

        // convert the queue to a list
        List<String> remainingElements = drainFifo();

        // check for the right number of entries
        assertEquals(Collections.emptyList(), remainingElements);

        // check everything was removed exactly once
        List<String> checkedRemovals = concatenate(removalLists);
        Collections.sort(checkedRemovals);
        assertEquals(ELEMS, checkedRemovals);
    }

    @Test
    public void testGettingStuff() throws Exception {
        // pre-populate elements
        for (String elem : ELEMS)
            places.add(fifo.put(elem));

        // create tasks
        List<Wiper> tasks = new ArrayList<>();
        for (String elem : ELEMS)
            tasks.add(new Wiper());

        // run the tasks concurrently
        List<List<String>> removalLists = runConcurrently(tasks);
        for (List<String> list : removalLists) if (!!!list.isEmpty()) System.out.println(list);

        // convert the queue to a list
        List<String> remainingElements = drainFifo();

        // check for the right number of entries
        assertEquals(Collections.emptyList(), remainingElements);

        // check everything was removed exactly once
        List<String> checkedRemovals = concatenate(removalLists);
        Collections.sort(checkedRemovals);
        assertEquals(ELEMS, checkedRemovals);

        // check no-one removed anything out of order
        // i.e. each list of removed elements should remain unchanged by sorting
        for (List<String> removed : removalLists)
            assertEquals(new ArrayList<>(new TreeSet<>(removed)), removed);
    }

    @Test
    public void testAllOpsConcurrently() throws Exception {
        // pre-populate all the possible elements
        for (String elem : ELEMS)
            places.add(fifo.put(elem));

        // create tasks
        List<Callable<List<String>>> tasks = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            switch (i % 4) {
            case 0:
            case 2:
                tasks.add(new Adder());
                break;
            case 1:
                tasks.add(new Constrictor());
                break;
            case 3:
                tasks.add(new Wiper());
                break;
            }
        }

        List<String> added = new ArrayList<>(), removed = new ArrayList<>();

        // collate additions and subtractions
        Iterator<Callable<List<String>>> iterator = tasks.iterator();
        for (List<String> results : runConcurrently(tasks))
            (iterator.next() instanceof Adder ? added : removed).addAll(results);
        added.addAll(ELEMS); // these were added up front
        Collections.sort(added);
        List<String> remainder = drainFifo();
        removed.addAll(remainder);
        Collections.sort(removed);

        assertEquals(added, removed);

    }

    private <T> List<T> concatenate(List<List<T>> lists) {
        List<T> result = new ArrayList<>();
        for (List<T> list : lists)
            result.addAll(list);
        return result;
    }

    private List<String> drainFifo() {
        List<String> queuedOrder = new ArrayList<>();
        do {
            String o = fifo.remove();
            if (o == null) break;
            queuedOrder.add(o);
        } while (true);
        return queuedOrder;
    }

    private <T> List<T> runConcurrently(List<? extends Callable<T>> tasks)
            throws InterruptedException, ExecutionException {
        // start all the threads
        startBarrier = new CyclicBarrier(tasks.size());
        ExecutorService xs = Executors.newFixedThreadPool(tasks.size());
        try {
            List<Future<T>> futures = new ArrayList<>();
            for (Callable<T> task : tasks)
                futures.add(xs.submit(task));

            // collect the results
            List<T> results = new ArrayList<>();
            for (Future<T> future : futures)
                results.add(future.get());
            return results;
        } finally {
            xs.shutdown();
        }
    }

    /** Adds stuff to the FIFO */
    class Adder implements Callable<List<String>> {
        @Override
        public List<String> call() throws Exception {
            List<String> added = new ArrayList<>();
            startBarrier.await();
            for (String elem : new ArrayList<>(ELEMS)) {
                places.add(fifo.put(elem));
                added.add(elem);
            }
            return added;
        }
    }

    /** Constrains the FIFO by removing specific elements */
    class Constrictor implements Callable<List<String>> {
        @Override
        public List<String> call() throws Exception {
            List<String> constricted = new ArrayList<>();
            startBarrier.await();
            while (!!! places.isEmpty()) {
                // get a copy of the known places in the fifo and shuffle it
                List<Sequential.Place<String>> myPlaces = new ArrayList<>(places);
                Collections.shuffle(myPlaces);
                // remove everything we can
                for (Sequential.Place<String> place : myPlaces) {
                    String elem = place.relinquish();
                    if (elem != null)
                        constricted.add(elem);
                    places.remove(place);
                }
            }
            return constricted;
        }
    }

    /** Wipes out the FIFO by getting all the elements */
    class Wiper implements Callable<List<String>> {
        @Override
        public List<String> call() throws Exception {
            List<String> wiped = new ArrayList<>();
            startBarrier.await();
            for (String elem = fifo.remove(); elem != null; elem = fifo.remove())
                wiped.add(elem);
            return wiped;
        }
    }
}
