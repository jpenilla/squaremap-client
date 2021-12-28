package xyz.jpenilla.squaremap.client.manager;

import com.google.common.io.ByteArrayDataInput;
import java.util.HashMap;
import java.util.Map;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.resources.ResourceLocation;
import xyz.jpenilla.squaremap.client.SquaremapClientInitializer;
import xyz.jpenilla.squaremap.client.util.Constants;
import xyz.jpenilla.squaremap.client.util.WorldInfo;

public class ServerManager {
    private final SquaremapClientInitializer squaremap;
    private final Map<ResourceLocation, WorldInfo> worlds = new HashMap<>();

    private boolean isOnServer;
    private String url;

    public ServerManager(SquaremapClientInitializer squaremap) {
        this.squaremap = squaremap;
    }

    public void initialize() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (!client.isLocalServer()) {
                this.squaremap.enable();
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            if (!client.isLocalServer()) {
                this.squaremap.getServerManager().disable();
                this.squaremap.getTileManager().disable();
                this.squaremap.getMiniMap().disable();

                this.squaremap.clearAllData();
            }
        });
    }

    public void enable() {
        this.squaremap.getScheduler().addTask(0, () -> squaremap.getNetworkManager().requestServerData());
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

    public WorldInfo getWorld(ResourceLocation key) {
        return this.worlds.get(key);
    }

    public void processPacket(ByteArrayDataInput packet) {
        int response = packet.readInt();
        if (response != Constants.RESPONSE_SUCCESS) {
            this.squaremap.disable();
        }
        this.url = packet.readUTF();
        int count = packet.readInt();
        for (int i = 0; i < count; i++) {
            ResourceLocation key = new ResourceLocation(packet.readUTF());
            String name = packet.readUTF();
            int zoomMax = packet.readInt();
            int zoomDefault = packet.readInt();
            int zoomExtra = packet.readInt();
            this.worlds.put(key, new WorldInfo(key, name, zoomMax, zoomDefault, zoomExtra));
        }
        ResourceLocation key = new ResourceLocation(packet.readUTF());
        this.squaremap.setWorld(this.worlds.get(key));
        if (this.squaremap.getConfig().getMinimap().getEnabled()) {
            this.squaremap.getMiniMap().enable();
        }
    }
}
