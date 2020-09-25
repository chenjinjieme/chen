package com.chen.test.maven;

import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;

public class Repository {
    private final Map<Path, Group> groupMap = new TreeMap<>();

    Map<Path, Group> groupMap() {
        return groupMap;
    }
}
