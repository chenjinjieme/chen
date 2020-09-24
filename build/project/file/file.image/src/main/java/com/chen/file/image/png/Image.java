package com.chen.file.image.png;

import java.nio.ByteBuffer;

public class Image {
    private static final int GREYSCALE = 0;
    private static final int TRUE_COLOUR = 2;
    private static final int INDEXED_COLOUR = 3;
    private static final int GREYSCALE_WITH_ALPHA = 4;
    private static final int TRUE_COLOUR_WITH_ALPHA = 6;
    private static final int NONE = 0;
    private static final int SUB = 1;
    private static final int UP = 2;
    private static final int AVERAGE = 3;
    private static final int PAETH = 4;
    private static final Pixel NULL = new Pixel();
    private final IHDRChunk ihdr;
    private final ByteBuffer buffer;
    private final Pixel[][] pixels;
    private final int bits;
    private final int pixelStride;
    private final int scanlineStride;

    Image(IHDRChunk ihdr, ByteBuffer buffer) {
        this.ihdr = ihdr;
        this.buffer = buffer;
        pixels = new Pixel[ihdr.width][ihdr.height];
        bits = switch (ihdr.colorType) {
            case GREYSCALE, INDEXED_COLOUR -> 1;
            case TRUE_COLOUR -> 3;
            case GREYSCALE_WITH_ALPHA -> 2;
            case TRUE_COLOUR_WITH_ALPHA -> 4;
            default -> 0;
        };
        pixelStride = bits * ihdr.bitDepth / 8;
        scanlineStride = 1 + pixelStride * ihdr.width;
    }

    private static int paeth(int a, int b, int c) {
        var pa = b - c;
        var pb = a - c;
        var pc = Math.abs(pa + pb);
        return (pa = Math.abs(pa)) <= (pb = Math.abs(pb)) && pa <= pc ? a : pb <= pc ? b : c;
    }

    private Pixel pixelParse(int x, int y) {
        var stride = y > 0 ? scanlineStride * y : 0;
        var filterType = buffer.get(stride);
        if (x > 0) stride += x * pixelStride;
        var samples = new int[bits];
        var a = filterType == SUB || filterType == AVERAGE || filterType == PAETH ? pixel(x - 1, y) : null;
        var b = filterType == UP || filterType == AVERAGE || filterType == PAETH ? pixel(x, y - 1) : null;
        var c = filterType == PAETH ? pixel(x - 1, y - 1) : null;
        for (var i = 0; i < bits; i++)
            samples[i] = switch (filterType) {
                case NONE -> buffer.get(++stride) & 0xff;
                case SUB -> buffer.get(++stride) + a.samples[i] & 0xff;
                case UP -> buffer.get(++stride) + b.samples[i] & 0xff;
                case AVERAGE -> buffer.get(++stride) + (a.samples[i] + b.samples[i] >> 1) & 0xff;
                case PAETH -> buffer.get(++stride) + paeth(a.samples[i], b.samples[i], c.samples[i]) & 0xff;
                default -> 0;
            };
        return new Pixel(samples, ihdr.bitDepth);
    }

    public Pixel pixel(int x, int y) {
        return x < 0 || y < 0 ? NULL : pixels[x][y] == null ? pixels[x][y] = pixelParse(x, y) : pixels[x][y];
    }
}
