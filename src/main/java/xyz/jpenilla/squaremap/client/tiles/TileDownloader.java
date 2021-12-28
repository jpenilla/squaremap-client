package xyz.jpenilla.squaremap.client.tiles;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import xyz.jpenilla.squaremap.client.SquaremapClientInitializer;

public class TileDownloader {
    public static final Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() / 2);
    private final java.util.Map<Tile, CompletableFuture<Void>> queue = new HashMap<>();
    private final SquaremapClientInitializer squaremap;

    public TileDownloader(SquaremapClientInitializer squaremap) {
        this.squaremap = squaremap;
    }

    public void queue(Tile tile) {
        if (this.queue.containsKey(tile)) {
            return; // already downloading
        }
        this.queue.put(tile, CompletableFuture.runAsync(new TileQueue(this.squaremap, tile), executor)
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    this.queue.remove(tile);
                    return null;
                })
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        throwable.printStackTrace();
                    }
                    this.queue.remove(tile);
                })
        );
    }

    public void clear() {
        Iterator<java.util.Map.Entry<Tile, CompletableFuture<Void>>> iter = this.queue.entrySet().iterator();
        while (iter.hasNext()) {
            iter.next().getValue().cancel(true);
            iter.remove();
        }
    }
}
