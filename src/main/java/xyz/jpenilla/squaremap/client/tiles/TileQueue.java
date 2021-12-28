package xyz.jpenilla.squaremap.client.tiles;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import xyz.jpenilla.squaremap.client.SquaremapClientInitializer;

public final class TileQueue implements Runnable {
    private final SquaremapClientInitializer squaremap;
    private final Tile tile;

    public TileQueue(SquaremapClientInitializer squaremap, Tile tile) {
        this.squaremap = squaremap;
        this.tile = tile;
    }

    @Override
    public void run() {
        try {
            if (this.squaremap.getServerManager().getUrl() == null) {
                return;
            }
            BufferedImage buffered = ImageIO.read(new URL(String.format("%s/tiles/%s/%s/%s_%s.png",
                    this.squaremap.getServerManager().getUrl(),
                    this.tile.getWorld().name(),
                    this.tile.getZoom(),
                    this.tile.getX(),
                    this.tile.getZ()
            )));
            if (buffered != null) {
                ImageIO.write(buffered, "png", this.tile.getFile());
            }
            this.tile.setImage(buffered);
            this.squaremap.updateAllMapTextures();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
