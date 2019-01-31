package com.chen.base.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileList {
    private String name;
    private List<FileList> list;
    private FileList parent;

    public FileList(String path) {
        this(new File(path));
    }

    public FileList(File file) {
        this(file, null);
    }

    private FileList(File file, FileList parent) {
        name = file.getName();
        this.parent = parent;
        if (file.isDirectory()) {
            list = new ArrayList<>();
            File[] files = file.listFiles();
            if (files != null) for (File f : files) list.add(new FileList(f, this));
        }
    }

    private FileList(String name, FileList child) {
        this.name = name;
        list = new ArrayList<>();
        list.add(child);
    }

    public FileList setParent(String parent) {
        this.parent = new FileList(parent, this);
        return this.parent;
    }

    public String toString() {
        return parent == null ? name : parent.toString() + "/" + name;
    }

    public List<String> list() {
        ArrayList<String> list = new ArrayList<>();
        list(list);
        return list;
    }

    private void list(List<String> list) {
        if (this.list == null) list.add(toString());
        else for (FileList fileList : this.list) fileList.list(list);
    }
}
