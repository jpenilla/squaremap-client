package xyz.jpenilla.squaremap.client.gui.screen.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.LinkedHashSet;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import xyz.jpenilla.squaremap.client.SquaremapClientInitializer;
import xyz.jpenilla.squaremap.client.manager.TextureManager;
import xyz.jpenilla.squaremap.client.tiles.Tile;
import xyz.jpenilla.squaremap.client.util.Util;
import xyz.jpenilla.squaremap.client.util.WorldInfo;

public class FullMapWidget implements Widget, GuiEventListener, NarratableEntry {
    private static final boolean DEBUG = false;

    private final Set<Tile> tiles = new LinkedHashSet<>();

    private final SquaremapClientInitializer squaremap;
    private final Minecraft client;
    private final int width;
    private final int height;
    private final WorldInfo world;

    private double panX;
    private double panY;

    private double offsetX;
    private double offsetY;

    private int zoom;

    private long lastClick;

    private boolean showSelf;

    public FullMapWidget(SquaremapClientInitializer squaremap, Minecraft client, int width, int height) {
        this.squaremap = squaremap;
        this.client = client;
        this.width = width;
        this.height = height;

        this.showSelf = this.squaremap.getConfig().fullMap().showSelf();

        this.world = this.squaremap.getWorld();
        this.zoom = this.world.zoomDefault();

        if (this.client.player != null) {
            this.offsetX = screenToWorld(-this.width / 2F, 0) + this.client.player.getBlockX();
            this.offsetY = screenToWorld(-this.height / 2F, 0) + this.client.player.getBlockZ();
        }

        updateTiles();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.panX = mouseX;
        this.panY = mouseY;

        long now = System.currentTimeMillis();
        if (now - this.lastClick < 250) {
            zoom(mouseX, mouseY, 1);
        }
        this.lastClick = now;

        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.panX = 0;
        this.panY = 0;
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        this.offsetX -= (mouseX - this.panX) / getScale();
        this.offsetY -= (mouseY - this.panY) / getScale();

        this.panX = mouseX;
        this.panY = mouseY;

        updateTiles();

        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        zoom(mouseX, mouseY, amount);

        return true;
    }

    public void zoom(double amount) {
        zoom(this.width / 2F, this.height / 2F, amount);
    }

    private void zoom(double mouseX, double mouseY, double amount) {
        int newZoom = Math.min(Math.max(this.zoom + (amount > 0 ? 1 : -1), 0), this.world.zoomMax() + this.world.zoomExtra());
        if (newZoom == this.zoom) {
            return;
        }

        double beforeX = screenToWorld(mouseX, this.offsetX);
        double beforeY = screenToWorld(mouseY, this.offsetY);

        this.zoom = newZoom;

        double afterX = screenToWorld(mouseX, this.offsetX);
        double afterY = screenToWorld(mouseY, this.offsetY);

        this.offsetX += (beforeX - afterX);
        this.offsetY += (beforeY - afterY);

        updateTiles();
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return true;
    }

    public void onClose() {
        this.tiles.clear();
    }

    public double worldToScreen(double world, double offset) {
        return (world - offset) * getScale();
    }

    public double screenToWorld(double screen, double offset) {
        return (screen / getScale()) + offset;
    }

    public double getScale() {
        return (1.0D / Math.pow(2, this.world.zoomMax() - this.zoom)) / this.client.getWindow().getGuiScale();
    }

    public int getPosX(double mouseX) {
        return (int) screenToWorld(mouseX, this.offsetX);
    }

    public int getPosY(double mouseY) {
        return (int) screenToWorld(mouseY, this.offsetY);
    }

    public Component getUrl() {
        return Component.literal(String.format("%s/?world=%s&zoom=%s&x=%s&z=%s",
            this.squaremap.getServerManager().getUrl(),
            this.world.name(),
            this.zoom,
            Math.round(getPosX(this.width / 2F)),
            Math.round(getPosY(this.height / 2F))
        ));
    }

    public void updateTiles() {
        this.tiles.clear();

        if (this.world == null) {
            return;
        }

        double extra = Math.pow(2, this.zoom > this.world.zoomMax() ? this.zoom - this.world.zoomMax() : 0);
        double pow = Math.pow(2, this.world.zoomMax() - this.zoom) * extra;

        double x0 = Math.round(((int) screenToWorld(0, this.offsetX) >> 9) / pow - 1);
        double y0 = Math.round(((int) screenToWorld(0, this.offsetY) >> 9) / pow - 1);
        double x1 = Math.round(((int) screenToWorld(this.width, this.offsetX) >> 9) / pow);
        double y1 = Math.round(((int) screenToWorld(this.height, this.offsetY) >> 9) / pow);

        for (double x = x0; x <= x1; x++) {
            for (double y = y0; y <= y1; y++) {
                this.tiles.add(this.squaremap.getTileManager().get(this.world, (int) x, (int) y, this.zoom));
            }
        }
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float delta) {
        double extra = Math.pow(2, this.zoom > this.world.zoomMax() ? this.zoom - this.world.zoomMax() : 0);
        double pow = Math.pow(2, this.world.zoomMax() - this.zoom) * extra;
        double size = Tile.SIZE / this.client.getWindow().getGuiScale() * extra;

        matrixStack.pushPose();
        for (Tile tile : this.tiles) {
            float x0 = (float) this.worldToScreen(tile.getX() * 512 * pow, this.offsetX);
            float y0 = (float) this.worldToScreen(tile.getZ() * 512 * pow, this.offsetY);
            float x1 = (float) (x0 + size);
            float y1 = (float) (y0 + size);
            tile.render(matrixStack, x0, y0, x1, y1, 0F, 0F, 1F, 1F);

            if (DEBUG) {
                GuiComponent.fill(matrixStack, (int) x0, (int) y0, (int) x0 + 1, (int) y1, 0x88888888);
                GuiComponent.fill(matrixStack, (int) x0, (int) y0, (int) x1, (int) y0 + 1, 0x88888888);
            }
        }
        matrixStack.popPose();

        if (this.showSelf) {
            final double playerSize = Tile.SIZE / this.client.getWindow().getGuiScale() * 2;
            this.drawPlayerIcon(matrixStack, delta, playerSize, this.client.player.getX(), this.client.player.getZ());
        }

        if (DEBUG) {
            int i = -1;
            debug(matrixStack, "player pos: " + this.client.player.getBlockX() + " " + this.client.player.getBlockZ(), ++i);
            debug(matrixStack, "pow: " + pow, ++i);
            debug(matrixStack, "offset: " + this.offsetX + " " + this.offsetY, ++i);
            debug(matrixStack, "mouse: " + mouseX + " " + mouseY, ++i);
            debug(matrixStack, "scale: " + getScale(), ++i);
            debug(matrixStack, "zoom: " + this.zoom, ++i);
            debug(matrixStack, "size: " + size, ++i);
            debug(matrixStack, "window: " + width + " " + height, ++i);
            debug(matrixStack, "tile count: " + this.tiles.size(), ++i);
            debug(matrixStack, "loaded tile count: " + this.squaremap.getTileManager().count(), ++i);
        }
    }

    private void drawPlayerIcon(final PoseStack matrixStack, final float delta, final double size, final double xPos, final double zPos) {
        matrixStack.pushPose();
        final float x = (float) (this.worldToScreen(xPos, this.offsetX) - size / 2);
        final float y = (float) (this.worldToScreen(zPos, this.offsetY) - size / 2);
        final float angle = (this.client.player.getViewYRot(delta) - 180.0F) % 360.0F;
        Util.rotateScene(matrixStack, x + size / 2, y + size / 2, angle);
        this.squaremap.getTextureManager()
            .drawTexture(matrixStack, TextureManager.SELF, x, y, (float) (x + size), (float) (y + size), 0F, 0F, 1F, 1F);
        matrixStack.popPose();
    }

    private void debug(PoseStack matrixStack, String str, int y) {
        this.client.font.drawShadow(matrixStack, Component.nullToEmpty(str), 10, 50 + 10 * y, 0xFFFFFFFF);
    }

    @Override
    public boolean changeFocus(boolean lookForwards) {
        return true;
    }

    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput builder) {
    }

    public boolean showSelf() {
        return this.showSelf;
    }

    public void showSelf(boolean showSelf) {
        this.showSelf = showSelf;
    }
}
