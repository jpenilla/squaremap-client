package xyz.jpenilla.squaremap.client.tiles;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import net.minecraft.util.thread.NamedThreadFactory;
import xyz.jpenilla.squaremap.client.SquaremapClientInitializer;

public class TileDownloader {
    public static final Executor EXECUTOR = Executors.newFixedThreadPool(
        Math.min(Runtime.getRuntime().availableProcessors() / 2, 4),
        new NamedThreadFactory("squaremap-client-tiledownloader")
    );
    private final Map<Tile, CompletableFuture<Void>> queue = new HashMap<>();
    private final SquaremapClientInitializer squaremap;

    public TileDownloader(SquaremapClientInitializer squaremap) {
        this.squaremap = squaremap;
    }

    public void queue(Tile tile) {
        if (this.queue.containsKey(tile)) {
            return; // already downloading
        }
        final CompletableFuture<Void> future = CompletableFuture.runAsync(new TileQueue(this.squaremap, tile), EXECUTOR)
            .whenComplete(($, ex) -> {
                if (ex != null) {
                    ex.printStackTrace();
                }

                this.queue.remove(tile);
            });
        this.queue.put(tile, future);
    }

    public void clear() {
        Iterator<Map.Entry<Tile, CompletableFuture<Void>>> iter = this.queue.entrySet().iterator();
        while (iter.hasNext()) {
            iter.next().getValue().cancel(true);
            iter.remove();
        }
    }
}
