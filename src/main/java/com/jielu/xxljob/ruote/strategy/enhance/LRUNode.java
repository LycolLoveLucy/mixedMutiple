package com.jielu.xxljob.ruote.strategy.enhance;
public class LRUNode {
    int key;
    int value;
    LRUNode head;
    LRUNode tail;
    LRUNode prev;
    LRUNode next;

    public LRUNode() {
    }
    public LRUNode(int key, int value) {
        this.key = key;
        this.value = value;
    }
    public LRUNode addLast(LRUNode tail) {
        if (this.head == null) {
            this.head = tail;
            return tail;
        }
        if (this.tail == null) {
            this.tail = tail;
            this.head.next = this.tail;
            this.tail.prev = this.head;
            return tail;
        }
        this.tail.next = tail;
        tail.prev = this.tail;
        this.tail = tail;
        return tail;

    }
    public int removeFirst() {
        LRUNode first = this.head;
        int firstKey = first.key;
        this.head = this.head.next;
        if (this.head == null) {
            this.tail = null;
        }
        return firstKey;
    }


    /**
     * 移除元素并添加至末尾
     */
    public void moveToLast(LRUNode node) {
        LRUNode prevNode = node.prev;
        LRUNode nextNode = node.next;
        if (node == this.tail || this.tail == null) {
            return;
        }
        if (prevNode != null) {
            prevNode.next = nextNode;
        }
        if (nextNode != null) {
            nextNode.prev = prevNode;
        }
        if (node == this.head) {
            this.head = nextNode;
        }
        addLast(node);

    }

}