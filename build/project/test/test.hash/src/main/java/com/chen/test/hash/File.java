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

    public File hash(String hash) {
        this.hash = hash;
        return this;
    }

    public boolean equals(Object o) {
        return this == o || o instanceof Path && name.equals(((Path) o).name());
    }

    public int hashCode() {
        return name.hashCode();
    }
}
