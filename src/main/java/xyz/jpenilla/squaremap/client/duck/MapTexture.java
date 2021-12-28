package xyz.jpenilla.squaremap.client.duck;

import xyz.jpenilla.squaremap.client.util.WorldInfo;

public interface MapTexture {
    void skip();

    void setData(byte scale, int centerX, int centerZ, WorldInfo world);

    void updateImage();
}
