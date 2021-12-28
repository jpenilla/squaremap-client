package xyz.jpenilla.squaremap.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.lwjgl.opengl.GL11;
import xyz.jpenilla.squaremap.client.SquaremapClientInitializer;
import xyz.jpenilla.squaremap.client.config.MiniMapConfig;
import xyz.jpenilla.squaremap.client.manager.TextureManager;
import xyz.jpenilla.squaremap.client.scheduler.Task;
import xyz.jpenilla.squaremap.client.tiles.Tile;
import xyz.jpenilla.squaremap.client.util.Image;

public class MiniMap {
    private final static int MAP_SIZE = 512;

    private final SquaremapClientInitializer squaremap;
    private final Minecraft client;

    private boolean enabled;
    private boolean visible;

    private LocalPlayer player;

    private int windowWidth;
    private int windowHeight;

    private DynamicTexture mapTexture;
    private final Image image = new Image(MAP_SIZE);

    private int textX;
    private int textZ;
    private final float textScale = 0.85F;

    private boolean northLocked;
    private boolean circular;
    private boolean showFrame;
    private boolean showCoordinates;
    private boolean showDirections;

    private Anchor anchorX;
    private Anchor anchorZ;
    private int anchorOffsetX;
    private int anchorOffsetZ;

    private int zoom;
    private float deltaZoom;
    private int zoomLevel;

    private int size;
    private int halfSize;
    private int halfSizeSq;
    private int doubleSize;

    private float percentX;
    private float percentZ;

    private int centerX;
    private int centerZ;
    private int left;
    private int top;

    private Tile tile;
    private final Object lock = new Object();
    private boolean updating;

    private UpdateTask updateTask;

    public MiniMap(SquaremapClientInitializer squaremap) {
        this.squaremap = squaremap;
        this.client = Minecraft.getInstance();
    }

    public void initialize() {
        HudRenderCallback.EVENT.register((matrixStack, delta) -> {
            if (!this.enabled || !this.visible) {
                return;
            }
            if (this.client.options.renderDebug) {
                return;
            }
            // do not show minimap if no squaremap world set
            if (this.squaremap.getWorld() == null) {
                return;
            }
            this.render(matrixStack, delta);
        });
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void toggle() {
        if (this.isEnabled()) {
            this.disable();
        } else {
            this.enable();
        }
    }

    public void enable() {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(this::enable);
            return;
        }

        this.enabled = true;
        this.visible = true;

        this.player = this.client.player;

        this.windowWidth = this.client.getWindow().getGuiScaledWidth();
        this.windowHeight = this.client.getWindow().getGuiScaledHeight();

        if (this.mapTexture == null) {
            this.mapTexture = new DynamicTexture(MAP_SIZE, MAP_SIZE, true);
            this.client.getTextureManager().register(TextureManager.MAP, this.mapTexture);
        }

        MiniMapConfig config = this.squaremap.getConfig().getMinimap();

        this.northLocked = config.getNorthLock();
        this.circular = config.getCircular();
        this.showFrame = config.getDrawFrame();
        this.showCoordinates = config.getCoordinates();
        this.showDirections = config.getDirections();

        setZoomLevel(config.getZoom());

        setSize(config.getSize());

        this.anchorX = Anchor.get(config.getAnchorX());
        this.anchorZ = Anchor.get(config.getAnchorZ());
        this.anchorOffsetX = config.getAnchorOffsetX();
        this.anchorOffsetZ = config.getAnchorOffsetZ();
        this.percentX = this.anchorX.getPercent(this.windowWidth, this.anchorOffsetX);
        this.percentZ = this.anchorZ.getPercent(this.windowHeight, this.anchorOffsetZ);

        updateCenter();

        this.updateTask = new UpdateTask(config.getUpdateInterval());
        this.squaremap.getScheduler().addTask(this.updateTask);

        this.deltaZoom = 0.0F;

        updateMapTexture();
    }

    public void disable() {
        this.enabled = false;

        if (this.updateTask != null) {
            this.updateTask.cancel();
        }
        this.updateTask = null;

        this.mapTexture = null;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public int getTop() {
        return this.top;
    }

    public int getLeft() {
        return this.left;
    }

    public int getCenterX() {
        return this.centerX;
    }

    public int getCenterZ() {
        return this.centerZ;
    }

    public void setCenter(int centerX, int centerZ, boolean updateAnchors) {
        this.percentX = centerX / (float) this.windowWidth;
        this.percentZ = centerZ / (float) this.windowHeight;
        if (updateAnchors) {
            this.anchorX = Anchor.get(this.percentX * 100F);
            this.anchorZ = Anchor.get(this.percentZ * 100F);
            this.anchorOffsetX = this.anchorX.getOffset(this.windowWidth, centerX);
            this.anchorOffsetZ = this.anchorZ.getOffset(this.windowHeight, centerZ);
        }
        updateCenter();
        this.squaremap.getConfig().getMinimap().setAnchorX(this.anchorX.value());
        this.squaremap.getConfig().getMinimap().setAnchorZ(this.anchorZ.value());
        this.squaremap.getConfig().getMinimap().setAnchorOffsetX(this.anchorOffsetX);
        this.squaremap.getConfig().getMinimap().setAnchorOffsetZ(this.anchorOffsetZ);
    }

    public void updateCenter() {
        this.centerX = (int) (this.anchorX.percent() * this.windowWidth) + this.anchorOffsetX;
        this.centerZ = (int) (this.anchorZ.percent() * this.windowHeight) + this.anchorOffsetZ;
        this.left = this.centerX - this.halfSize;
        this.top = this.centerZ - this.halfSize;
        this.textX = (int) (this.centerX / this.textScale);
        this.textZ = (int) ((this.centerZ + this.halfSize) / this.textScale) + 5; // add 5 for padding
    }

    public int getSize() {
        return this.size;
    }

    public int getHalfSize() {
        return this.halfSize;
    }

    public void addSize(int add) {
        setSize(this.size + add);
        updateCenter();
    }

    private void setSize(int size) {
        if (size < 64) size = 64;
        if (size > 512) size = 512;
        this.size = size;
        this.halfSize = (int) (size / 2F);
        this.halfSizeSq = this.halfSize * this.halfSize;
        this.doubleSize = this.size * 2;
        this.squaremap.getConfig().getMinimap().setSize(this.size);
    }

    public int getZoom() {
        return this.zoom;
    }

    public void addZoomLevel(int add) {
        setZoomLevel(this.zoomLevel + add);
    }

    public void setZoomLevel(int level) {
        this.zoomLevel = Math.min(Math.max(level, 0), 6);
        int zoom = this.zoomLevel <= 0 ? 64 : (int) (7 * Math.pow(2, this.zoomLevel)) + 64;
        this.zoom = Math.min(Math.max(zoom, 64), 512);
        this.squaremap.getConfig().getMinimap().setZoom(this.zoomLevel);
    }

    public void tickDeltaZoom(float delta) {
        if (Math.abs(this.zoom - this.deltaZoom) > 0.01F) {
            this.deltaZoom = this.deltaZoom + delta / 5F * (this.zoom - this.deltaZoom);
        } else {
            this.deltaZoom = this.zoom;
        }
    }

    public void setUpdateInterval(int value) {
        this.updateTask.updateInterval = value;
    }

    public void setShowCoordinates(boolean value) {
        this.showCoordinates = value;
    }

    public void setShowDirections(boolean value) {
        this.showDirections = value;
    }

    public void setCircular(boolean value) {
        this.circular = value;
    }

    public void setShowFrame(boolean value) {
        this.showFrame = value;
    }

    public void setNorthLocked(boolean value) {
        this.northLocked = value;
    }

    public boolean contains(double centerX, double centerZ) {
        centerX -= this.centerX;
        centerZ -= this.centerZ;
        return (centerX * centerX + centerZ * centerZ) < this.halfSizeSq;
    }

    public void checkWindowResize() {
        int width = this.client.getWindow().getGuiScaledWidth();
        int height = this.client.getWindow().getGuiScaledHeight();
        boolean update = false;
        int x = this.centerX;
        int z = this.centerZ;
        if (this.windowWidth != width) {
            switch (this.anchorX) {
                case MID -> x = width / 2 + this.left - this.windowWidth / 2 + this.halfSize;
                case HIGH -> x = width + this.left - this.windowWidth + this.halfSize;
            }
            this.windowWidth = width;
            update = true;
        }
        if (this.windowHeight != height) {
            switch (this.anchorZ) {
                case MID -> z = height / 2 + this.top - this.windowHeight / 2 + this.halfSize;
                case HIGH -> z = height + this.top - this.windowHeight + this.halfSize;
            }
            this.windowHeight = height;
            update = true;
        }
        if (update) {
            setCenter(x, z, false);
        }
    }

    public void updateMapTexture() {
        if (!this.enabled || !this.visible) {
            return;
        }
        if (this.updating) {
            //updating = false;
            return;
        }
        if (this.squaremap.getWorld() == null) {
            return;
        }
        if (this.mapTexture.getPixels() == null) {
            return;
        }
        CompletableFuture.runAsync(() -> {
            int blockX, blockZ, x, z;
            int startX = (int) Math.floor(this.player.getX()) - (int) (MAP_SIZE / 2F);
            int startZ = (int) Math.floor(this.player.getZ()) - (int) (MAP_SIZE / 2F) + 1;
            synchronized (this.lock) {
                this.updating = true;
                for (x = 0; x < MAP_SIZE; x++) {
                    for (z = 0; z < MAP_SIZE; z++) {
                        blockX = startX + x;
                        blockZ = startZ + z;
                        this.tile = this.squaremap.getTileManager().get(this.squaremap.getWorld(), blockX >> 9, blockZ >> 9, this.squaremap.getWorld().zoomMax());
                        this.image.setPixel(x, z, this.tile == null ? 0 : this.tile.getImage().getPixel(blockX & 511, blockZ & 511));
                        this.mapTexture.getPixels().setPixelRGBA(x, z, this.image.getPixel(x, z));
                    }
                }
                this.mapTexture.upload();
                this.updating = false;
            }
        }).whenComplete(($, throwable) -> {
            this.updating = false;
            if (throwable != null) {
                throwable.printStackTrace();
            }
        });
    }

    public void render(PoseStack matrixStack, float delta) {
        TextureManager tex = this.squaremap.getTextureManager();

        // get fresh reference to player since vanilla destroys this object on dimension change
        this.player = this.client.player;

        // setup opengl stuff - this ensures we draw how we expect in case another mod left without cleaning up
        matrixStack.pushPose();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();

        // tuck it behind chat/hotbar
        matrixStack.translate(0.0D, 0.0D, -999.9D);

        tickDeltaZoom(this.client.getDeltaFrameTime());

        // setup texture size stuff - most of this is used multiple times so these act like a "cache" for this frame
        float zoomDelta = this.deltaZoom; // things happen so fast this can change a few lines apart here
        float scale = this.size / zoomDelta;
        float halfScale = scale / 2F;
        float markerOffset = scale / (scale * 2);
        float x0 = this.left + scale;
        float x1 = x0 + this.size - scale * 2;
        float y0 = this.top + scale;
        float y1 = y0 + this.size - scale * 2;
        float u = (MAP_SIZE / 2F - zoomDelta / 2F) / MAP_SIZE; // resizes with zoom _and_ size
        float u2 = (MAP_SIZE / 2F - this.size / 2F) / MAP_SIZE; // doesn't resize with size or zoom
        float v = 1.0F - u;
        float v2 = 1.0F - u2;

        // we only allow rotating if _not_ north locked and _not_ circular
        float angle = (this.player.getViewYRot(delta) - 180.0F) % 360.0F;

        // draw mask - uses special blend which writes to the alpha channel where black pixels exist.
        // the mask is twice as large as the map texture and the black pixels are the map size.
        // this ensures pixels outside the map area won't draw to the screen. this fixes rough edges
        // on circle maps and prevents corners from extending beyond the frame on rotating square maps.
        RenderSystem.blendFuncSeparate(GL11.GL_ZERO, GL11.GL_ONE, GL11.GL_SRC_COLOR, GL11.GL_ZERO);
        {
            RenderSystem.setShaderTexture(0, this.circular ? TextureManager.MASK_CIRCLE : TextureManager.MASK_SQUARE);
            GuiComponent.blit(matrixStack, this.left - this.halfSize, this.top - this.halfSize, 0, 0, 0, this.doubleSize, this.doubleSize, this.doubleSize, this.doubleSize);
        }

        // draw sky background - uses special blend which only writes where high alpha values exist from above
        RenderSystem.blendFunc(GL11.GL_DST_ALPHA, GL11.GL_ONE_MINUS_DST_ALPHA);
        {
            matrixStack.pushPose();
            tex.drawTexture(matrixStack, tex.getTexture(this.player.level), x0 - halfScale, y0 + halfScale, x1 - halfScale, y1 + halfScale, u2, v2);
            matrixStack.popPose();
        }

        // draw map - still using special blend from above
        {
            matrixStack.pushPose();
            if (!this.northLocked) {
                if (!this.circular) {
                    // scale square map to hide missing pixels in corners
                    matrixStack.translate(this.centerX, this.centerZ, 0);
                    matrixStack.scale(1.41421356237F, 1.41421356237F, 1.41421356237F);
                    matrixStack.translate(-this.centerX, -this.centerZ, 0);
                }
                rotateScene(matrixStack, this.centerX, this.centerZ, -angle);
            }
            tex.drawTexture(matrixStack, TextureManager.MAP, x0 - halfScale, y0 + halfScale, x1 - halfScale, y1 + halfScale, u, v);
            matrixStack.popPose();
        }

        // use a special blend that supports translucent pixels for the remaining textures
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // draw self marker
        {
            matrixStack.pushPose();
            if (this.northLocked) {
                // only allow rotating if map is not rotating or if northlocked
                rotateScene(matrixStack, this.centerX, this.centerZ, angle);
            }
            tex.drawTexture(matrixStack, TextureManager.SELF, x0 + markerOffset, y0, x1 + markerOffset, y1, u2, v2);
            matrixStack.popPose();
        }

        // draw the frame
        if (this.showFrame) {
            RenderSystem.setShaderTexture(0, this.circular ? TextureManager.FRAME_CIRCLE : TextureManager.FRAME_SQUARE);
            GuiComponent.blit(matrixStack, this.left, this.top, 0, 0, 0, this.size, this.size, this.size, this.size);
        }

        // draw cardinal directions
        if (this.showDirections) {
            int dirX = (int) (this.centerX / this.textScale) + 1;
            int dirY = (int) (this.centerZ / this.textScale) - 3;
            float angle2 = this.northLocked ? 0.0F : angle;
            float distance = (this.halfSize / this.textScale) + 4;
            if (!this.circular) {
                if (!this.northLocked && angle2 != 0.0F) {
                    distance /= cos(45.0F - Math.abs(45.0F + (-Math.abs(angle2) % 90.0F)));
                }
            }
            matrixStack.pushPose();
            matrixStack.scale(this.textScale, this.textScale, this.textScale);
            {
                matrixStack.pushPose();
                matrixStack.translate(distance * sin(angle2 - 180), distance * cos(angle2 - 180), 0);
                text(matrixStack, "N", dirX, dirY);
                matrixStack.popPose();
                matrixStack.pushPose();
                matrixStack.translate(distance * sin(angle2 + 90), distance * cos(angle2 + 90), 0);
                text(matrixStack, "E", dirX, dirY);
                matrixStack.popPose();
                matrixStack.pushPose();
                matrixStack.translate(distance * sin(angle2), distance * cos(angle2), 0);
                text(matrixStack, "S", dirX, dirY);
                matrixStack.popPose();
                matrixStack.pushPose();
                matrixStack.translate(distance * sin(angle2 - 90), distance * cos(angle2 - 90), 0);
                text(matrixStack, "W", dirX, dirY);
                matrixStack.popPose();
            }
            matrixStack.popPose();
        }

        // draw coordinates
        if (this.showCoordinates) {
            matrixStack.pushPose();
            matrixStack.scale(this.textScale, this.textScale, this.textScale);
            text(matrixStack, String.format("%s, %s, %s", (int) Math.floor(this.player.getX()), (int) Math.floor(this.player.getY()), (int) Math.floor(this.player.getZ())), this.textX, this.textZ + 8);
            matrixStack.popPose();
        }

        // done
        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
        matrixStack.popPose();
    }

    private void text(PoseStack matrixStack, String text, int x, int y) {
        x -= (int) (this.client.font.width(text) / 2F);
        this.client.font.draw(matrixStack, text, x + 1, y + 1, 0xFF3F3F3F);
        this.client.font.draw(matrixStack, text, x, y, 0xFFFFFFFF);
    }

    private void rotateScene(PoseStack matrixStack, int x, int y, float degrees) {
        matrixStack.translate(x, y, 0);
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(degrees));
        matrixStack.translate(-x, -y, 0);
    }

    private float cos(float degree) {
        return (float) Math.cos(Math.toRadians(degree));
    }

    private float sin(float degree) {
        return (float) Math.sin(Math.toRadians(degree));
    }

    private enum Anchor {
        LOW(0), MID(50), HIGH(100);

        private final static double ONE = 100 * (1.0F / 3.0F);
        private final static double TWO = 100 * (2.0F / 3.0F);

        private final int value;

        Anchor(int value) {
            this.value = value;
        }

        private int value() {
            return value;
        }

        public float percent() {
            return value / 100F;
        }

        public int getOffset(int windowSize, int center) {
            return center - (int) (windowSize * percent());
        }

        public float getPercent(int windowSize, int offset) {
            return (windowSize * percent() + offset) / windowSize;
        }

        private static Anchor get(double value) {
            return value < ONE ? LOW : value < TWO ? MID : HIGH;
        }
    }

    private class UpdateTask extends Task {
        private int updateInterval;
        private int tick;

        private UpdateTask(int updateInterval) {
            super(0, true);
            this.updateInterval = updateInterval;
        }

        @Override
        public void run() {
            if (this.tick++ >= this.updateInterval) {
                updateMapTexture();
                this.tick = 0;
            }
            checkWindowResize();
        }
    }
}
