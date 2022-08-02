package com.jielu.cache.localcache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

public final class ConcurrentLinkedHashMap<S, C> extends ConcurrentHashMap {

    private AtomicReference<Node> node = new AtomicReference(new Node(new Object()));
    private AtomicReference<Node> first = new AtomicReference(node.get().getFirst());

    @Override
    public Object put(Object key, Object value) {
        final ReentrantLock reentrantLock = new ReentrantLock();
        reentrantLock.lock();
        Node current = new Node(key);
        node.get().next = current;
        node.set(node.get().next);
        reentrantLock.unlock();
        Object v = super.put(key, value);
        return v;
    }

    /**
     * @param size
     * @param exceedSize Remove the size of top when capacity is overflow ,the overflow size is "exceedSize"
     */
    public void removeHeadLoopWithSize(int size, final int exceedSize) {
        if (this.size() < exceedSize) {
            return;
        }
        while (first.get() != null && size >= 1) {
            --size;
            first.set(first.get().next);
            this.remove(first.get().k);
        }
        this.node.set(first.get());
    }

    private static class Node {
        Node next;
        Node first;
        transient Object k;
        public Node(Object k) {

         if (first == null) {
              first = this;
            }
             this.k = k;
          }

       private Node getFirst() {
            return first;
        }
    }


}
