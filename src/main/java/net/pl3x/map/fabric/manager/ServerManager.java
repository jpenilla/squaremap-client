package net.pl3x.map.fabric.manager;

import com.google.common.io.ByteArrayDataInput;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.pl3x.map.fabric.Pl3xMap;
import net.pl3x.map.fabric.util.Constants;
import net.pl3x.map.fabric.util.World;

public class ServerManager {
    private final Pl3xMap pl3xmap;
    private final Map<UUID, World> worlds = new HashMap<>();

    private boolean isOnServer;
    private String url;

    public ServerManager(Pl3xMap pl3xmap) {
        this.pl3xmap = pl3xmap;
    }

    public void initialize() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (!client.isLocalServer()) {
                this.pl3xmap.enable();
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            if (!client.isLocalServer()) {
                this.pl3xmap.getServerManager().disable();
                this.pl3xmap.getTileManager().disable();
                this.pl3xmap.getMiniMap().disable();

                this.pl3xmap.clearAllData();
            }
        });
    }

    public void enable() {
        this.pl3xmap.getScheduler().addTask(0, () -> pl3xmap.getNetworkManager().requestServerData());
        this.isOnServer = true;
    }

    public void disable() {
        this.isOnServer = false;
        this.worlds.clear();
        this.url = null;
    }

    public boolean isOnServer() {
        return this.isOnServer;
    }

    public String getUrl() {
        return url;
    }

    public World getWorld(UUID uuid) {
        return this.worlds.get(uuid);
    }

    public void processPacket(ByteArrayDataInput packet) {
        int response = packet.readInt();
        if (response != Constants.RESPONSE_SUCCESS) {
            this.pl3xmap.disable();
        }
        this.url = packet.readUTF();
        int count = packet.readInt();
        for (int i = 0; i < count; i++) {
            UUID uuid = UUID.fromString(packet.readUTF());
            String name = packet.readUTF();
            int zoomMax = packet.readInt();
            int zoomDefault = packet.readInt();
            int zoomExtra = packet.readInt();
            this.worlds.put(uuid, new World(uuid, name, zoomMax, zoomDefault, zoomExtra));
        }
        UUID uuid = UUID.fromString(packet.readUTF());
        this.pl3xmap.setWorld(this.worlds.get(uuid));
        if (this.pl3xmap.getConfig().getMinimap().getEnabled()) {
            this.pl3xmap.getMiniMap().enable();
        }
    }
}
