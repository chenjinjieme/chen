package com.chen.test.maven;

import java.util.Iterator;

public class GroupIterator implements Iterator<Group> {
    private final Iterator<Group> iterator;
    private GroupIterator groupIterator;
    private Group group;

    public GroupIterator(Iterator<Group> iterator) {
        this.iterator = iterator;
    }

    public boolean hasNext() {
        if (group == null) {
            if (groupIterator == null || !groupIterator.hasNext()) {
                if (!iterator.hasNext()) return false;
                group = iterator.next();
                groupIterator = group.groupIterator();
            } else group = groupIterator.next();
        }
        return true;
    }

    public Group next() {
        if (hasNext()) {
            var group = this.group;
            this.group = null;
            return group;
        } else return null;
    }
}
