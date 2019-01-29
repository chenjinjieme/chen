package com.chen.util.data;

import java.util.*;

class Message extends AbstractMap<String, Object> {
    private final String message;
    private final MessageEntry entry = new MessageEntry();
    private final MessageEntrySet entrySet = new MessageEntrySet();

    Message(String message) {
        this.message = message;
    }

    public Set<Entry<String, Object>> entrySet() {
        return entrySet;
    }

    private class MessageEntry implements Entry<String, Object> {
        public String getKey() {
            return "message";
        }

        public Object getValue() {
            return message;
        }

        public Object setValue(Object value) {
            throw new UnsupportedOperationException();
        }
    }

    private class MessageEntrySet extends AbstractSet<Entry<String, Object>> {
        public Iterator<Entry<String, Object>> iterator() {
            return new MessageEntrySetIterator();
        }

        public int size() {
            return 1;
        }

        private class MessageEntrySetIterator implements Iterator<Entry<String, Object>> {
            private MessageEntry next = entry;

            public boolean hasNext() {
                return next != null;
            }

            public Entry<String, Object> next() {
                if (next == null) throw new NoSuchElementException();
                MessageEntry entry = next;
                next = null;
                return entry;
            }
        }
    }
}
