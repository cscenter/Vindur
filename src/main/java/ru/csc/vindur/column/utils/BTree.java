package ru.csc.vindur.column.utils;

/**
 * @author Andrey Kokorev
 *         Created on 26.10.2014.
 */
public class BTree {
    private final int pageSize;
    private Node root = new Node();

    public BTree(int pageSize) {
        this.pageSize = pageSize;
    }

    public void insert(Integer key, Integer value) {

    }

    private void insert(Node node, Integer key, Integer value) {

    }

    /**
     * Looks for value, stored by key
     * @param key
     * @return value, if found, otherwise null
     */
    public Integer findValue(Integer key) {
        return findValue(root, key);
    }

    private Integer findValue(Node node, Integer key) {
        if(node == null) return null;

        Record[] keys = node.getKeys();
        for(int i = 0, len = keys.length; i < len; i++) {
            if(keys[i].getKey() >= key)
                return findValue(node.getChildAt(i), key);
        }
        //go deeper into last
        return findValue(node.getChildAt(keys.length), key);
    }

    class Node {
        private Record[] records;
        private Node[] children; // contains elements, higher than in last child Node, linked from storage

        Node() {

        }

        Record[] getKeys() {
            return records;
        }

        Node getChildAt(int i) {
            return children[i];
        }
    }

    class Record {
        private Integer key, value;

        Record(Integer key, Integer value) {
            this.key = key;
            this.value = value;
        }

        public Integer getKey() {
            return key;
        }

        public Integer getValue() {
            return value;
        }
    }
}
