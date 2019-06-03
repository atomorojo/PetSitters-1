package PetSitters.auxiliary;

import PetSitters.exception.ExceptionCache;

import java.util.HashMap;

public class LRUCache<K, V> {
    HashMap<K, MapContent> map;
    CustomLinkedList<K> chain;
    private int sizeLimit;


    public LRUCache(int sizeLimit) {
        map = new HashMap<>();
        chain = new CustomLinkedList<K>();
        this.sizeLimit = sizeLimit;
    }

    public int getSizeLimit() {
        return sizeLimit;
    }

    public void setSizeLimit(int sizeLimit) {
        this.sizeLimit = sizeLimit;
    }

    private void deleteLeastRecentlyUsed() {
        Node<K> key = chain.getFirstReference();
        map.remove(key.getElem());
        chain.popFirst();
    }

    public V getFromCache(K parameter) throws ExceptionCache {
        if (map.containsKey(parameter)) {
            MapContent mapContent = map.get(parameter);
            chain.removeByReference(mapContent.pointer);
            chain.addLast(parameter);
            mapContent.setPointer(chain.getLastReference());
            map.put(parameter, mapContent);
            return mapContent.getValue();
        } else {
            throw new ExceptionCache("There is no cached result for this parameter");
        }
    }

    public void update(K key, V value) {
        if (map.containsKey(key)) {
            MapContent mapContent = map.get(key);
            chain.removeByReference(mapContent.pointer);
        }

        if (map.size() >= sizeLimit) {
            deleteLeastRecentlyUsed();
        }

        chain.addLast(key);
        Node<K> ite = chain.getLastReference();
        map.put(key, new MapContent(ite, value));
    }

    private class MapContent {
        Node<K> pointer;
        V value;

        public MapContent(Node<K> pointer, V value) {
            this.pointer = pointer;
            this.value = value;
        }

        public Node<K> getPointer() {
            return pointer;
        }

        public void setPointer(Node<K> pointer) {
            this.pointer = pointer;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }
    }
}
