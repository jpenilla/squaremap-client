package xyz.jpenilla.squaremap.client.gui.screen.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import xyz.jpenilla.squaremap.client.gui.MiniMap;

public class MiniMapWidget extends AbstractWidget {
    private final MiniMap minimap;

    private int clickX;
    private int clickY;

    public MiniMapWidget(MiniMap minimap) {
        super(minimap.getLeft(), minimap.getTop(), minimap.getSize(), minimap.getSize(), null);
        this.minimap = minimap;
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput builder) {
        this.defaultButtonNarrationText(builder);
    }

    @Override
    public int getWidth() {
        return this.minimap.getSize();
    }

    @Override
    public int getHeight() {
        return this.minimap.getSize();
    }

    @Override
    public void renderWidget(GuiGraphics matrixStack, int mouseX, int mouseY, float delta) {
        this.minimap.render(matrixStack, delta);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.clickX = (int) (mouseX - this.minimap.getLeft());
        this.clickY = (int) (mouseY - this.minimap.getTop());
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        this.clickX = 0;
        this.clickY = 0;
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
        this.setX((int) (mouseX - clickX));
        this.setY((int) (mouseY - clickY));
        this.minimap.setCenter(this.getX() + this.minimap.getHalfSize(), this.getY() + this.minimap.getHalfSize(), true);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!Screen.hasControlDown()) {
            this.minimap.addZoomLevel((int) scrollY);
        } else {
            this.minimap.addSize(scrollY > 0 ? 16 : -16);
        }
        return true;
    }

    @Override
    protected boolean clicked(double mouseX, double mouseY) {
        return this.active && this.visible && this.minimap.contains(mouseX, mouseY);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.active && this.visible && this.minimap.contains(mouseX, mouseY);
    }
}
