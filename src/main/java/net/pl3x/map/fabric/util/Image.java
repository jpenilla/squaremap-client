package net.pl3x.map.fabric.util;

public final class Image {
    private final int[][] pixels;

    public Image(int size) {
        this.pixels = new int[size][size];
    }

    public void setPixel(int x, int z, int color) {
        this.pixels[x][z] = color;
    }

    public int getPixel(int x, int z) {
        return this.pixels[x][z];
    }
}
