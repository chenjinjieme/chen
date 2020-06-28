package com.chen.file.torrent;

import java.nio.ByteBuffer;
import java.util.List;

public class Piece {
    private int index;
    private ByteBuffer buffer;
    private List<File> files;
    private long offset;
    private boolean checked;

    Piece(int index, ByteBuffer buffer, List<File> files, long offset) {
        this.index = index;
        this.buffer = buffer;
        this.files = files;
        this.offset = offset;
    }

    public int index() {
        return index;
    }

    public ByteBuffer buffer() {
        return buffer;
    }

    public List<File> files() {
        return files;
    }

    public long offset() {
        return offset;
    }

    public boolean checked() {
        return checked;
    }

    public Piece checked(boolean checked) {
        this.checked = checked;
        return this;
    }
}
