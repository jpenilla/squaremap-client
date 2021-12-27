package net.pl3x.map.fabric.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.pl3x.map.fabric.Pl3xMap;
import net.pl3x.map.fabric.gui.MiniMap;
import net.pl3x.map.fabric.gui.screen.widget.MiniMapWidget;
import org.lwjgl.glfw.GLFW;

public class PositionScreen extends AbstractScreen {
    private static final String SIZE = I18n.get("pl3xmap.screen.position.size", "%s", "%s");
    private static final String CENTER = I18n.get("pl3xmap.screen.position.center", "%s", "%s");
    private static final String HELP_1 = I18n.get("pl3xmap.screen.position.help1");
    private static final String HELP_2 = I18n.get("pl3xmap.screen.position.help2");
    private static final String HELP_3 = I18n.get("pl3xmap.screen.position.help3");
    private static final String HELP_4 = I18n.get("pl3xmap.screen.position.help4");

    private final MiniMap minimap;

    public PositionScreen(Pl3xMap pl3xmap, Screen parent) {
        super(pl3xmap, parent);
        this.minimap = pl3xmap.getMiniMap();

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
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float delta) {
        super.render(matrixStack, mouseX, mouseY, delta);

        int centerX = (int) (this.width / 2F);
        int centerY = (int) (this.height / 2F);

        drawText(matrixStack, this.title, centerX, 15);

        drawText(matrixStack, String.format(SIZE, this.minimap.getSize(), this.minimap.getZoom()), centerX, 30);
        drawText(matrixStack, String.format(CENTER, this.minimap.getCenterX(), this.minimap.getCenterZ()), centerX, 40);

        drawText(matrixStack, HELP_1, centerX, centerY - 30);
        drawText(matrixStack, HELP_2, centerX, centerY - 10);
        drawText(matrixStack, HELP_3, centerX, centerY + 10);
        drawText(matrixStack, HELP_4, centerX, centerY + 30);
    }

    @Override
    public void onClose() {
        this.minimap.setVisible(true);
        super.onClose();
    }
}
