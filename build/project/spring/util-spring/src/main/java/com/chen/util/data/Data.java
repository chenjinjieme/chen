package com.chen.util.data;

import com.github.pagehelper.Page;

import java.util.*;

class Data extends AbstractMap<String, Object> {
    private final Object data;
    private final boolean page;
    private final AbstractEntry entry;
    private final DataEntrySet entrySet = new DataEntrySet();

    Data(Page data) {
        this(data, true);
    }

    Data(Object data) {
        this(data, false);
    }

    private Data(Object data, boolean page) {
        this.data = data;
        this.page = page;
        entry = new MessageEntry();
    }

    public Set<Entry<String, Object>> entrySet() {
        return entrySet;
    }

    private abstract class AbstractEntry implements Entry<String, Object> {
        AbstractEntry next;

        public Object setValue(Object value) {
            throw new UnsupportedOperationException();
        }
    }

    private class MessageEntry extends AbstractEntry {
        private MessageEntry() {
            next = new DataEntry();
        }

        public String getKey() {
            return "message";
        }

        public Object getValue() {
            return "success";
        }
    }

    private class DataEntry extends AbstractEntry {
        private DataEntry() {
            if (page) next = new PageNumEntry();
        }

        public String getKey() {
            return "data";
        }

        public Object getValue() {
            return data;
        }
    }

    private class PageNumEntry extends AbstractEntry {
        private PageNumEntry() {
            next = new TotalEntry();
        }

        public String getKey() {
            return "pageNum";
        }

        public Object getValue() {
            return ((Page) data).getPageNum();
        }
    }

    private class TotalEntry extends AbstractEntry {
        public String getKey() {
            return "total";
        }

        public Object getValue() {
            return ((Page) data).getTotal();
        }
    }

    private class DataEntrySet extends AbstractSet<Entry<String, Object>> {
        public Iterator<Entry<String, Object>> iterator() {
            return new DataEntrySetIterator();
        }

        public int size() {
            return page ? 4 : 2;
        }

        private class DataEntrySetIterator implements Iterator<Entry<String, Object>> {
            private AbstractEntry next = entry;

            public boolean hasNext() {
                return next != null;
            }

            public Entry<String, Object> next() {
                if (next == null) throw new NoSuchElementException();
                AbstractEntry entry = next;
                next = entry.next;
                return entry;
            }
        }
    }
}
