package net.pl3x.map.fabric.tiles;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.pl3x.map.fabric.Pl3xMap;
import net.pl3x.map.fabric.util.Constants;
import net.pl3x.map.fabric.util.Image;
import net.pl3x.map.fabric.util.WorldInfo;

public class Tile {
    public static final int SIZE = 512;

    private final WorldInfo world;
    private final int x;
    private final int z;
    private final int zoom;
    private final Image image;
    private final Object lock = new Object();

    private final ResourceLocation identifier;
    private DynamicTexture texture;

    private long lastUsed;

    public Tile(WorldInfo world, int x, int z, int zoom) {
        this.world = world;
        this.x = x;
        this.z = z;
        this.zoom = zoom;
        this.image = new Image(SIZE);
        updateLastUsed();

        this.identifier = new ResourceLocation(Constants.MODID, world.name().toLowerCase(Locale.ROOT) + "." + zoom + "." + x + "." + z);

        initTexture();
    }

    private void initTexture() {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(this::initTexture);
            return;
        }
        synchronized (lock) {
            this.texture = new DynamicTexture(SIZE, SIZE, true);
            Minecraft.getInstance().getTextureManager().register(this.identifier, this.texture);
        }
    }

    public WorldInfo getWorld() {
        return this.world;
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

    public int getZoom() {
        return this.zoom;
    }

    public Image getImage() {
        updateLastUsed();
        synchronized (this.lock) {
            return this.image;
        }
    }

    public long lastUsed() {
        return this.lastUsed;
    }

    public void updateLastUsed() {
        this.lastUsed = System.currentTimeMillis();
    }

    public void setImage(BufferedImage bufferedImage) {
        if (bufferedImage != null) {
            synchronized (this.lock) {
                for (int x = 0; x < SIZE; x++) {
                    for (int z = 0; z < SIZE; z++) {
                        this.image.setPixel(x, z, rgb2bgr(bufferedImage.getRGB(x, z)));
                    }
                }
            }
        }
        updateTexture();
    }

    public void updateTexture() {
        synchronized (lock) {
            if (this.texture == null || this.texture.getPixels() == null) {
                Pl3xMap.instance().getScheduler().addTask(20, this::updateTexture);
                return;
            }
            for (int x = 0; x < SIZE; x++) {
                for (int z = 0; z < SIZE; z++) {
                    this.texture.getPixels().setPixelRGBA(x, z, this.image.getPixel(x, z));
                }
            }
            this.texture.upload();
        }
    }

    public void render(PoseStack matrixStack, float x, float y) {
        render(matrixStack, x, y, x + SIZE, y + SIZE, 0F, 0F, 1F, 1F);
    }

    public void render(PoseStack matrixStack, float x0, float y0, float x1, float y1, float u0, float v0, float u1, float v1) {
        updateLastUsed();
        Pl3xMap.instance().getTextureManager().drawTexture(matrixStack, this.identifier, x0, y0, x1, y1, u0, v0, u1, v1);
    }

    private int rgb2bgr(int color) {
        // Minecraft flips red and blue for some reason
        // lets flip them back
        int a = color >> 24 & 0xFF;
        int r = color >> 16 & 0xFF;
        int g = color >> 8 & 0xFF;
        int b = color & 0xFF;
        return (a << 24) | (b << 16) | (g << 8) | r;
    }

    public File getFile() {
        File dir = new File(new File(new File(Constants.MODID, ip().replace(":", "-").toLowerCase(Locale.ROOT)), this.world.name()), String.valueOf(this.zoom));
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IllegalStateException("Cannot create tiles directory for " + this.world + " Directory: " + dir.getAbsolutePath());
            }
        }
        return new File(dir, this.x + "_" + this.z + ".png");
    }

    @SuppressWarnings("ConstantConditions")
    private String ip() {
        return Minecraft.getInstance().getCurrentServer().ip;
    }
}
