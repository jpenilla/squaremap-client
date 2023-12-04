package xyz.jpenilla.squaremap.client.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import xyz.jpenilla.squaremap.client.SquaremapClientInitializer;
import xyz.jpenilla.squaremap.client.gui.MiniMap;
import xyz.jpenilla.squaremap.client.gui.screen.widget.MiniMapWidget;

public class PositionScreen extends AbstractScreen {
    private static final Component HELP_1 = Component.translatable("squaremap-client.screen.position.help1");
    private static final Component HELP_2 = Component.translatable("squaremap-client.screen.position.help2");
    private static final Component HELP_3 = Component.translatable("squaremap-client.screen.position.help3");
    private static final Component HELP_4 = Component.translatable("squaremap-client.screen.position.help4");

    private final MiniMap minimap;

    public PositionScreen(SquaremapClientInitializer squaremap, Screen parent) {
        super(squaremap, parent);
        this.minimap = squaremap.getMiniMap();

        // add some key listeners
        this.keyHandler.listen(GLFW.GLFW_KEY_PAGE_UP, () -> this.minimap.addZoomLevel(1));
        this.keyHandler.listen(GLFW.GLFW_KEY_PAGE_DOWN, () -> this.minimap.addZoomLevel(-1));
        this.keyHandler.listen(GLFW.GLFW_KEY_KP_ADD, () -> this.minimap.addSize(16));
        this.keyHandler.listen(GLFW.GLFW_KEY_KP_SUBTRACT, () -> this.minimap.addSize(-16));

        // hide minimap so we're not drawing it twice
        this.minimap.setVisible(false);
    }

    @Override
    protected void init() {
        // minimap is a clickable widget for click and drag conveniences
        addRenderableWidget(new MiniMapWidget(this.minimap));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);

        int centerX = (int) (this.width / 2F);
        int centerY = (int) (this.height / 2F);

        drawText(guiGraphics, this.title, centerX, 15);

        drawText(guiGraphics, Component.translatable("squaremap-client.screen.position.size", this.minimap.getSize(), this.minimap.getZoom()), centerX, 30);
        drawText(guiGraphics, Component.translatable("squaremap-client.screen.position.center", this.minimap.getCenterX(), this.minimap.getCenterZ()), centerX, 40);

        drawText(guiGraphics, HELP_1, centerX, centerY - 30);
        drawText(guiGraphics, HELP_2, centerX, centerY - 10);
        drawText(guiGraphics, HELP_3, centerX, centerY + 10);
        drawText(guiGraphics, HELP_4, centerX, centerY + 30);
    }

    @Override
    public void onClose() {
        this.minimap.setVisible(true);
        super.onClose();
    }
}
