package com.chen.file.image.png;

public class Pixel {
    private static final double[] LINEAR = new double[65536];
    private static final double[] NONLINEAR = new double[65536];
    int[] samples;
    int[] bands;
    int[] bands16;
    int bitDepth;
    boolean trueColor;
    boolean alpha;

    public Pixel() {
        samples = new int[5];
    }

    public Pixel(int[] samples, int bitDepth) {
        this.samples = samples;
        bands = new int[]{-1, -1, -1, -1, -1};
        bands16 = new int[]{-1, -1, -1, -1, -1};
        this.bitDepth = bitDepth;
        var length = samples.length;
        trueColor = length > 2;
        alpha = (length & 1) == 0;
    }

    private static int transform(int sample, int bitDepth) {
        return switch (bitDepth) {
            case 1 -> sample == 0 ? 0 : 0xff;
            case 2 -> sample << 6 | sample << 4 | sample << 2 | sample;
            case 4 -> sample << 4 | sample;
            case 8 -> sample;
            case 16 -> {
                var height = (sample += 128) >> 8;
                var low = sample & 0xff;
                yield height > low ? height - 1 : height;
            }
            default -> 0;
        };
    }

    private static double linear(int band16) {
        return band16 > 0 && LINEAR[band16] == 0 ? LINEAR[band16] = band16 < 2651 ? band16 / 12.92 : Math.pow(band16 / 69139.425 + 11 / 211.0, 2.4) * 65535 : LINEAR[band16];
    }

    private static double nonlinear(int band16) {
        return band16 > 0 && NONLINEAR[band16] == 0 ? NONLINEAR[band16] = band16 < 206 ? band16 * 12.92 : Math.pow(band16 / 65535.0, 1 / 2.4) * 69139.425 - 3604.425 : NONLINEAR[band16];
    }

    public int greyscale() {
        return bands[0] < 0 ? bands[0] = trueColor ? transform(greyscale16(), 16) : transform(samples[0], bitDepth) : bands[0];
    }

    public int greyscale16() {
        return bands16[0] < 0 ? bands16[0] = trueColor ? (int) (0.21263901 * linear(red16()) + 0.71516868 * linear(green16()) + 0.07219232 * linear(blue16()) + 0.5) : bitDepth == 16 ? samples[0] : greyscale() << 8 | greyscale() : bands16[0];
    }

    public int red() {
        return bands[1] < 0 ? bands[1] = trueColor ? transform(samples[0], bitDepth) : transform(red16(), 16) : bands[1];
    }

    public int red16() {
        return bands16[1] < 0 ? bands16[1] = trueColor ? bitDepth == 16 ? samples[0] : red() << 8 | bands[1] : (int) (nonlinear(greyscale16()) + 0.5) : bands16[1];
    }

    public int green() {
        return bands[2] < 0 ? bands[2] = trueColor ? transform(samples[1], bitDepth) : transform(green16(), 16) : bands[2];
    }

    public int green16() {
        return bands16[2] < 0 ? bands16[2] = trueColor ? bitDepth == 16 ? samples[1] : green() << 8 | bands[2] : (int) (nonlinear(greyscale16()) + 0.5) : bands16[2];
    }

    public int blue() {
        return bands[3] < 0 ? bands[3] = trueColor ? transform(samples[2], bitDepth) : transform(blue16(), 16) : bands[3];
    }

    public int blue16() {
        return bands16[3] < 0 ? bands16[3] = trueColor ? bitDepth == 16 ? samples[2] : blue() << 8 | bands[3] : (int) (nonlinear(greyscale16()) + 0.5) : bands16[3];
    }

    public int alpha() {
        return bands[4] < 0 ? bands[4] = alpha ? transform(samples[trueColor ? 3 : 1], bitDepth) : 0xff : bands[4];
    }

    public int alpha16() {
        return bands16[4] < 0 ? bands16[4] = alpha ? bitDepth == 16 ? samples[trueColor ? 3 : 1] : alpha() << 8 | bands[4] : 0xffff : bands16[4];
    }

    public int rgb() {
        return red() << 16 | green() << 8 | blue();
    }

    public int argb() {
        return alpha() << 24 | red() << 16 | green() << 8 | blue();
    }
}
