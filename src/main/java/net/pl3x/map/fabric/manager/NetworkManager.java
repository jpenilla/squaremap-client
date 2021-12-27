package net.pl3x.map.fabric.manager;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.netty.buffer.Unpooled;
import java.util.UUID;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.pl3x.map.fabric.Pl3xMap;
import net.pl3x.map.fabric.configuration.Lang;
import net.pl3x.map.fabric.duck.MapTexture;
import net.pl3x.map.fabric.mixin.MapRendererAccess;
import net.pl3x.map.fabric.util.Constants;
import net.pl3x.map.fabric.util.WorldInfo;

public class NetworkManager {
    private final ResourceLocation channel = new ResourceLocation(Constants.MODID, Constants.MODID);
    private final Pl3xMap pl3xmap;

    public NetworkManager(Pl3xMap pl3xmap) {
        this.pl3xmap = pl3xmap;
    }

    public void initialize() {
        ClientPlayNetworking.registerGlobalReceiver(this.channel, (client, handler, buf, responseSender) -> {
            ByteArrayDataInput packet = in(buf.accessByteBufWithCorrectSize());
            int packetType = packet.readInt();
            int protocol = packet.readInt();
            if (protocol != Constants.PROTOCOL) {
                Lang.chat("pl3xmap.error.server.incompatible");
                this.pl3xmap.disable();
                return;
            }
            switch (packetType) {
                case Constants.SERVER_DATA -> this.pl3xmap.getServerManager().processPacket(packet);
                case Constants.UPDATE_WORLD -> {
                    int response = packet.readInt();
                    if (response != Constants.RESPONSE_SUCCESS) {
                        this.pl3xmap.disable();
                        return;
                    }
                    UUID uuid = UUID.fromString(packet.readUTF());
                    WorldInfo world = this.pl3xmap.getServerManager().getWorld(uuid);
                    this.pl3xmap.setWorld(world);
                    if (world != null && this.pl3xmap.getConfig().getMinimap().getEnabled()) {
                        this.pl3xmap.getMiniMap().enable();
                    }
                }
                case Constants.MAP_DATA -> {
                    int response = packet.readInt();
                    switch (response) {
                        case Constants.ERROR_NO_SUCH_MAP, Constants.ERROR_NO_SUCH_WORLD, Constants.ERROR_NOT_VANILLA_MAP -> {
                            int id = packet.readInt();
                            MapTexture texture = (MapTexture) ((MapRendererAccess)Minecraft.getInstance().gameRenderer.getMapRenderer()).maps().get(id);
                            if (texture != null) {
                                texture.skip();
                            }
                        }
                        case Constants.RESPONSE_SUCCESS -> {
                            int id = packet.readInt();
                            byte scale = packet.readByte();
                            int x = packet.readInt();
                            int z = packet.readInt();
                            UUID uuid = UUID.fromString(packet.readUTF());
                            WorldInfo world = this.pl3xmap.getServerManager().getWorld(uuid);
                            MapTexture texture = (MapTexture) ((MapRendererAccess)Minecraft.getInstance().gameRenderer.getMapRenderer()).maps().get(id);
                            if (world != null && texture != null) {
                                texture.setData(scale, x, z, world);
                            }
                        }
                    }
                }
            }
        });
    }

    public void requestServerData() {
        ByteArrayDataOutput out = out();
        out.writeInt(Constants.SERVER_DATA);
        sendPacket(out);
    }

    public void requestMapData(int id) {
        ByteArrayDataOutput out = out();
        out.writeInt(Constants.MAP_DATA);
        out.writeInt(id);
        sendPacket(out);
    }

    private void sendPacket(ByteArrayDataOutput packet) {
        if (Minecraft.getInstance().getConnection() != null) {
            ClientPlayNetworking.send(this.channel, new FriendlyByteBuf(Unpooled.wrappedBuffer(packet.toByteArray())));
        } else {
            this.pl3xmap.getScheduler().addTask(20, () -> sendPacket(packet));
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    private ByteArrayDataOutput out() {
        return ByteStreams.newDataOutput();
    }

    @SuppressWarnings("UnstableApiUsage")
    private ByteArrayDataInput in(byte[] bytes) {
        return ByteStreams.newDataInput(bytes);
    }
}
