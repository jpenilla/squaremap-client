package net.pl3x.map.fabric.manager;

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
import net.pl3x.map.fabric.Pl3xMap;
import net.pl3x.map.fabric.tiles.Tile;
import net.pl3x.map.fabric.tiles.TileDownloader;
import net.pl3x.map.fabric.util.Constants;
import net.pl3x.map.fabric.util.World;

public class TileManager {
    private final TileDownloader tileDownloader;
    private final Map<String, Tile> tiles = new HashMap<>();
    private final Object lock = new Object();

    private int size;
    private Timer timer;

    public TileManager(Pl3xMap pl3xmap) {
        this.tileDownloader = new TileDownloader(pl3xmap);
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

    public Tile get(World world, int x, int z, int zoom) {
        zoom = Math.min(Math.max(zoom, 0), world.getZoomMax());
        String key = world.getName() + "." + zoom + "." + x + "." + z;
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

    public Tile loadTile(World world, int x, int z, int zoom) {
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
