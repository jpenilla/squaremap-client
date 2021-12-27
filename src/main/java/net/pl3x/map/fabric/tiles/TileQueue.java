package net.pl3x.map.fabric.tiles;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import net.pl3x.map.fabric.Pl3xMap;

public class TileQueue implements Runnable {
    private final Pl3xMap pl3xmap;
    private final Tile tile;

    public TileQueue(Pl3xMap pl3xmap, Tile tile) {
        this.pl3xmap = pl3xmap;
        this.tile = tile;
    }

    @Override
    public void run() {
        try {
            if (this.pl3xmap.getServerManager().getUrl() == null) {
                return;
            }
            BufferedImage buffered = ImageIO.read(new URL(String.format("%s/tiles/%s/%s/%s_%s.png",
                    this.pl3xmap.getServerManager().getUrl(),
                    this.tile.getWorld().name(),
                    this.tile.getZoom(),
                    this.tile.getX(),
                    this.tile.getZ()
            )));
            if (buffered != null) {
                ImageIO.write(buffered, "png", this.tile.getFile());
            }
            this.tile.setImage(buffered);
            this.pl3xmap.updateAllMapTextures();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
