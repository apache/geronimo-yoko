package org.apache.yoko.util.concurrent;

import org.apache.yoko.util.Fifa;
import org.apache.yoko.util.Fifo;

import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Objects.requireNonNull;

/**
 * A thread-safe queue that allows concurrent modification of non-adjacent elements.
 */
class ConcurrentFifo<T> implements Fifo<T>, Fifa<T> {
    /*
     * This class relies on consistent lock ordering. Locks are ALWAYS obtained
     * in the order of elements in the queue. The locks used are the monitors
     * of the node elements, and each node's monitor guards the relationship
     * between that node and its successor. By implication, it guards the other
     * node's back reference as well.
     *
     * So, for a delete operation, two nodes must be locked: the node to be
     * deleted, and the previous node, but NOT IN THAT ORDER! Moreover, after
     * the previous node is locked, the relationship must be checked to ensure
     * it is still current. The convention observed is that next() is only
     * accessed while the lock is held, and it is cross-checked against any
     * unguarded calls to prev().
     *
     * NOTE: this is not double-check locking (DCL) because the early access
     * never obviates a synchronized block, and results are always checked 
     * within the guarded section. Therefore, it is not necessary for any of
     * the non-final fields to be volatile.
     * 
     * DCL: https://www.cs.umd.edu/~pugh/java/memoryModel/jsr-133-faq.html#dcl
     *
     * If inconsistency is detected, all locks are to be released and the
     * operation restarted from scratch, unless it can be determined the
     * operation has definitively failed due to concurrent modification.
     *
     * Operations on non-adjacent nodes are concurrent. Concurrent operations
     * on a node or on adjacent nodes will contend for monitors, but never
     * deadlock.
     */

    private final Head<T> head = new Head<>();
    private final Foot<T> foot = new Foot<>(head);
    protected final AtomicInteger size = new AtomicInteger(0);

    @Override
    public int size() { return size.get(); }

    /**
     * Get, without removing it, the oldest remaining element from this FIFO.
     * This method does not block and returns an answer consistent with the state of the FIFO at some point.
     *
     * @return the oldest remaining element or <code>null</code> if the FIFO was empty
     */
    @Override
    public T peek() {
        return recursivePeek(head);
    }

    /**
     * Find the first non-null value.
     */
    private T recursivePeek(PNode<T> start) {
        synchronized (start) {
            NNode<T> nn = start.next();
            if (nn == foot) return null;
            VNode<T> node = (VNode<T>) nn;
            T result = node.get();
            return (result == null) ? recursivePeek(node) : result;
        }
    }

    /**
     * Add an element to the end of this FIFO.
     *
     * @param elem must not be <code>null</code>
     * @return an object representing the place in the queue
     */
    @Override
    public Place<T> put(T elem) {
        do {
            final PNode<T> pnode = foot.prev();
            // lock penultimate node
            synchronized (pnode) {
                // RETRY if structure changed
                if (pnode.next() != foot) continue;
                // create a new node
                final VNode<T> node = createNode(elem);
                // insert new node
                synchronized (node) {
                    node.insertAfter(pnode);
                    size.incrementAndGet();
                }
                // return place in queue
                return new Place<T>() {
                    @Override
                    public T relinquish() {
                        return remove(node);
                    }
                };
            }
        } while (true);
    }

    protected VNode<T> createNode(T elem) {
        return new StrongNode<>(requireNonNull(elem));
    }

    /**
     * Remove the least recently added element.
     *
     * @return the removed element, or <code>null</code> if the FIFO was empty.
     */
    @Override
    public T remove() {
        return recursiveRemove(head);
    }

    /**
     * Find and remove the first non-null value
     */
    private T recursiveRemove(PNode<T> start) {
        synchronized (start) {
            NNode<T> nn = start.next();
            if (nn == foot) return null;
            VNode<T> node = (VNode<T>) nn;
            T result = node.get();
            if (result == null)
                return recursiveRemove(node);
            synchronized (node) {
                node.delete();
                size.decrementAndGet();
                return result;
            }
        }
    }

    /**
     * Remove the specified node from the FIFO, if present.
     * @return the element if it was successfully removed,
     *         otherwise <code>null</code>
     */
    protected T remove(VNode<T> node) {
        do {
            // retrieve previous node
            final PNode<T> pNode = node.prev();
            // FAIL if node already deleted
            if (pNode == null) return null;
            // lock previous node
            synchronized (pNode) {
                // RETRY if structure has changed
                if (pNode.next() != node) continue;
                // Remove node from chain and report success
                synchronized (node) {
                    node.delete();
                    size.decrementAndGet();
                }
                return node.get();
            }
        } while (true);
    }
}
