package ua.gwm.sponge_plugin.library.utils;

import java.util.Map;

public class Pair<K, V> implements Map.Entry<K, V> {

    private K key;
    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public K getKey() {
        return key;
    }

    public K setKey(K key) {
        K old_key = this.key;
        this.key = key;
        return old_key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public V setValue(V value) {
        V old_value = this.value;
        this.value = value;
        return old_value;
    }
}
