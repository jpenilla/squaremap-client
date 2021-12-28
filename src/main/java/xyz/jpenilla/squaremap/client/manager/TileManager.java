package xyz.jpenilla.squaremap.client.manager;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import javax.imageio.ImageIO;
import xyz.jpenilla.squaremap.client.SquaremapClientInitializer;
import xyz.jpenilla.squaremap.client.tiles.Tile;
import xyz.jpenilla.squaremap.client.tiles.TileDownloader;
import xyz.jpenilla.squaremap.client.util.Constants;
import xyz.jpenilla.squaremap.client.util.WorldInfo;

public class TileManager {
    private final TileDownloader tileDownloader;
    private final Map<String, Tile> tiles = new HashMap<>();
    private final Object lock = new Object();

    private int size;
    private Timer timer;

    public TileManager(SquaremapClientInitializer squaremap) {
        this.tileDownloader = new TileDownloader(squaremap);
    }

    public int count() {
        return this.size;
    }

    public void enable() {
        disable();
        this.timer = new Timer();
        this.timer.scheduleAtFixedRate(new UpdateTask(), Constants.TILE_UPDATE_INTERVAL, Constants.TILE_UPDATE_INTERVAL);
    }

    public void disable() {
        if (this.timer != null) {
            this.timer.cancel();
            this.timer = null;
        }
        this.tileDownloader.clear();
        synchronized (lock) {
            this.tiles.clear();
            this.size = 0;
        }
    }

    public Tile get(WorldInfo world, int x, int z, int zoom) {
        zoom = Math.min(Math.max(zoom, 0), world.zoomMax());
        String key = world.name() + "." + zoom + "." + x + "." + z;
        synchronized (lock) {
            Tile tile = this.tiles.get(key);
            if (tile == null) {
                tile = loadTile(world, x, z, zoom);
                this.tiles.put(key, tile);
            }
            this.size = this.tiles.size();
            return tile;
        }
    }

    public Tile loadTile(WorldInfo world, int x, int z, int zoom) {
        Tile tile = new Tile(world, x, z, zoom);
        File file = tile.getFile();
        if (!file.exists()) {
            this.tileDownloader.queue(tile);
            return tile;
        }
        CompletableFuture.runAsync(() -> {
                    try {
                        BufferedImage buffer = ImageIO.read(file);
                        if (buffer != null) {
                            tile.setImage(buffer);
                        }
                    } catch (IOException ignore) {
                    }
                }, TileDownloader.executor)
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    return null;
                })
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        throwable.printStackTrace();
                    }
                    tile.updateTexture();
                });
        return tile;
    }

    private class UpdateTask extends TimerTask {
        @Override
        public void run() {
            long now = System.currentTimeMillis();
            synchronized (lock) {
                Iterator<Map.Entry<String, Tile>> iter = tiles.entrySet().iterator();
                while (iter.hasNext()) {
                    Tile tile = iter.next().getValue();
                    if (now - tile.lastUsed() < Constants.TILE_UPDATE_INTERVAL) {
                        tileDownloader.queue(tile);
                    } else {
                        iter.remove();
                    }
                }
                size = tiles.size();
            }
        }
    }
}
