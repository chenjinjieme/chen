package com.chen.core.security;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

public class MessageDigest {
    private java.security.MessageDigest messageDigest;

    private MessageDigest(java.security.MessageDigest messageDigest) {
        this.messageDigest = messageDigest;
    }

    public static MessageDigest md5() {
        try {
            return new MessageDigest(java.security.MessageDigest.getInstance("MD5"));
        } catch (NoSuchAlgorithmException e) {
            throw new MessageDigestException(e);
        }
    }

    public static MessageDigest sha1() {
        try {
            return new MessageDigest(java.security.MessageDigest.getInstance("SHA-1"));
        } catch (NoSuchAlgorithmException e) {
            throw new MessageDigestException(e);
        }
    }

    public static MessageDigest sha256() {
        try {
            return new MessageDigest(java.security.MessageDigest.getInstance("SHA-256"));
        } catch (NoSuchAlgorithmException e) {
            throw new MessageDigestException(e);
        }
    }

    public void update(byte input) {
        messageDigest.update(input);
    }

    public void update(byte[] input, int offset, int len) {
        messageDigest.update(input, offset, len);
    }

    public void update(byte[] input) {
        messageDigest.update(input);
    }

    public void update(ByteBuffer input) {
        messageDigest.update(input);
    }

    public byte[] digest() {
        return messageDigest.digest();
    }

    public byte[] digest(byte[] input, int offset, int len) {
        messageDigest.update(input, offset, len);
        return messageDigest.digest();
    }

    public byte[] digest(byte[] input) {
        return messageDigest.digest(input);
    }

    public byte[] digest(Path path) throws IOException {
        try (var channel = FileChannel.open(path)) {
            var buffer = ByteBuffer.allocate(1024);
            for (long l = channel.size(), read; l > 0 && (read = channel.read(buffer.clear())) > 0; l -= read) messageDigest.update(buffer.flip());
            return messageDigest.digest();
        }
    }
}
