package com.chen.test.hash;

public class File implements Path {
    private String name;
    private String hash;

    public File(String name, String hash) {
        this.name = name;
        this.hash = hash;
    }

    public String name() {
        return name;
    }

    public File name(String name) {
        this.name = name;
        return this;
    }

    public String hash() {
        return hash;
    }

    public Object getValue() {
        return hash;
    }

    public int compareTo(Path o) {
        return o instanceof Directory ? 1 : name.compareToIgnoreCase(o.name());
    }
}
