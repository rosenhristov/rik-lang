package main.java.rosenhristov.interpreter;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Bimap<K extends Comparable<K>, V extends Comparable<V>> implements Cloneable, Serializable {

    private Map<K, V> map;
    private Map<V, K> reversedMap;

    private Bimap() {
        this.map = new LinkedHashMap<>();
        this.reversedMap = new LinkedHashMap<>();
    }

    private Bimap(Map<K, V> map) {
        this();
        putAll(map);
    }

    public static Bimap create() {
        return new Bimap();
    }

    public static Bimap of(Map<Comparable, Comparable> map) {
        return new Bimap(map);
    }


    public boolean put(K key, V value) {
        boolean isUniqueKeyValPair = isUniqueEntry(key, value);
        if (isUniqueKeyValPair) {
            this.map.put(key, value);
            this.reversedMap.put(value, key);
        }
        return isUniqueKeyValPair;
    }

    public boolean reversePut(V value, K key) {
        boolean isUniqueKeyValPair = isUniqueEntry(key, value);
        if (isUniqueKeyValPair) {
            this.reversedMap.put(value, key);
            this.map.put(key, value);
        }
        return isUniqueKeyValPair;
    }

    public void putAll(Map<? extends K, ? extends V> inputMap) {
        for (Map.Entry<? extends K, ? extends V> entry : inputMap.entrySet()) {
            map.put(entry.getKey(), entry.getValue());
            reversedMap.put(entry.getValue(), entry.getKey());
        }
    }

    public V get(K key) {
        return this.map.get(key);
    }

    public K reverseGet(V value) {
        return this.reversedMap.get(value);
    }

    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return reversedMap.containsKey(value);
    }

    public Comparable remove(Comparable key, Comparable value) {
        return map.containsKey(key)
                ? removeByKey((K) key)
                : reversedMap.containsKey(key)
                    ? removeByValue((V) key)
                    : null;
    }

    public V removeByKey(K key) {
        V value = map.get(key);
        reversedMap.remove(value);
        return map.remove(key);
    }

    public K removeByValue(V value) {
        K key = reversedMap.get(value);
        map.remove(key);
        return reversedMap.remove(value);
    }

    public Set<K> keySet() {
        return map.keySet();
    }

    public Set<V> valueSet() {
        return reversedMap.keySet();
    }

    public boolean isUniqueEntry(K key, V value) {
        return !map.containsKey(key) && !reversedMap.containsKey(value);
    }

    public Map<K, V> getMap() {
        return map;
    }

    public Map<V, K> reverse() {
        return reversedMap;
    }
}
