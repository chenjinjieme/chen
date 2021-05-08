package com.chen.test.maven;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class Repository implements Iterable<Group> {
    private final Map<String, Group> groupMap = new TreeMap<>();

    public Group group(String name) {
        return groupMap.computeIfAbsent(name, Group::new);
    }

    public Iterator<Group> iterator() {
        return new GroupIterator(groupMap.values().iterator());
    }
}
