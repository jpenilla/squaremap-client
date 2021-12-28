package xyz.jpenilla.squaremap.client.manager;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import xyz.jpenilla.squaremap.client.SquaremapClientInitializer;
import xyz.jpenilla.squaremap.client.configuration.Lang;
import xyz.jpenilla.squaremap.client.duck.MapTexture;
import xyz.jpenilla.squaremap.client.mixin.MapRendererAccess;
import xyz.jpenilla.squaremap.client.util.Constants;
import xyz.jpenilla.squaremap.client.util.WorldInfo;

public class NetworkManager {
    private final ResourceLocation channel = new ResourceLocation("squaremap", "client");
    private final SquaremapClientInitializer squaremap;

    public NetworkManager(SquaremapClientInitializer squaremap) {
        this.squaremap = squaremap;
    }

    public void initialize() {
        ClientPlayNetworking.registerGlobalReceiver(this.channel, (client, handler, buf, responseSender) -> {
            ByteArrayDataInput packet = in(buf.accessByteBufWithCorrectSize());
            int packetType = packet.readInt();
            int protocol = packet.readInt();
            if (protocol != Constants.PROTOCOL) {
                Lang.chat("squaremap-client.error.server.incompatible");
                this.squaremap.disable();
                return;
            }
            switch (packetType) {
                case Constants.SERVER_DATA -> this.squaremap.getServerManager().processPacket(packet);
                case Constants.UPDATE_WORLD -> {
                    int response = packet.readInt();
                    if (response != Constants.RESPONSE_SUCCESS) {
                        this.squaremap.disable();
                        return;
                    }
                    ResourceLocation key = new ResourceLocation(packet.readUTF());
                    WorldInfo world = this.squaremap.getServerManager().getWorld(key);
                    this.squaremap.setWorld(world);
                    if (world != null && this.squaremap.getConfig().getMinimap().getEnabled()) {
                        this.squaremap.getMiniMap().enable();
                    }
                }
                case Constants.MAP_DATA -> {
                    int response = packet.readInt();
                    switch (response) {
                        case Constants.ERROR_NO_SUCH_MAP, Constants.ERROR_NO_SUCH_WORLD, Constants.ERROR_NOT_VANILLA_MAP -> {
                            int id = packet.readInt();
                            MapTexture texture = (MapTexture) ((MapRendererAccess) Minecraft.getInstance().gameRenderer.getMapRenderer()).maps().get(id);
                            if (texture != null) {
                                texture.skip();
                            }
                        }
                        case Constants.RESPONSE_SUCCESS -> {
                            int id = packet.readInt();
                            byte scale = packet.readByte();
                            int x = packet.readInt();
                            int z = packet.readInt();
                            ResourceLocation key = new ResourceLocation(packet.readUTF());
                            WorldInfo world = this.squaremap.getServerManager().getWorld(key);
                            MapTexture texture = (MapTexture) ((MapRendererAccess) Minecraft.getInstance().gameRenderer.getMapRenderer()).maps().get(id);
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
            this.squaremap.getScheduler().addTask(20, () -> sendPacket(packet));
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
