package com.chen.file.torrent;

import com.chen.core.lang.ByteSequence;

import java.util.List;

public class Piece {
    private int index;
    private ByteSequence sequence;
    private List<File> files;
    private long offset;
    private boolean checked;

    Piece(int index, ByteSequence sequence, List<File> files, long offset) {
        this.index = index;
        this.sequence = sequence;
        this.files = files;
        this.offset = offset;
    }

    public int index() {
        return index;
    }

    public ByteSequence sequence() {
        return sequence;
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
