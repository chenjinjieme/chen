package com.chen.spring.base.data;

import java.util.*;

public class Parameter extends AbstractMap<String, Object> {
    private final ParameterEntrySet entrySet = new ParameterEntrySet();
    private int size;
    private ParameterEntry entry;

    public Parameter() {
    }

    public Parameter(String key, Object value) {
        add(key, value);
    }

    public Parameter add(String key, Object value) {
        size++;
        entry = new ParameterEntry(key, value, entry);
        return this;
    }

    public Parameter set(String key, Object value) {
        put(key, value);
        return this;
    }

    public Object put(String key, Object value) {
        for (ParameterEntry entry = this.entry; entry != null; entry = entry.next)
            if (Objects.equals(key, entry.key)) return entry.setValue(value);
        add(key, value);
        return null;
    }

    public Parameter clear(String key, Object value) {
        clear();
        return add(key, value);
    }

    public Set<Entry<String, Object>> entrySet() {
        return entrySet;
    }

    private class ParameterEntry implements Entry<String, Object> {
        private final String key;
        private Object value;
        private ParameterEntry next;

        private ParameterEntry(String key, Object value, ParameterEntry next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public String getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }

        public Object setValue(Object value) {
            Object o = this.value;
            this.value = value;
            return o;
        }
    }

    private class ParameterEntrySet extends AbstractSet<Entry<String, Object>> {
        public Iterator<Entry<String, Object>> iterator() {
            return new ParameterEntrySetIterator();
        }

        public int size() {
            return size;
        }

        public void clear() {
            size = 0;
            entry = null;
        }

        private class ParameterEntrySetIterator implements Iterator<Entry<String, Object>> {
            private ParameterEntry next = entry;

            public boolean hasNext() {
                return next != null;
            }

            public Entry<String, Object> next() {
                if (next == null) throw new NoSuchElementException();
                ParameterEntry entry = next;
                next = entry.next;
                return entry;
            }
        }
    }
}
