package xyz.jpenilla.squaremap.client.gui.screen.widget;

import com.mojang.blaze3d.vertex.PoseStack;
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
    public void updateNarration(NarrationElementOutput builder) {
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
    public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float delta) {
        matrixStack.pushPose();
        // we don't want to tuck this one behind anything
        matrixStack.translate(0.0D, 0.0D, 999.9D);
        this.minimap.render(matrixStack, delta);
        matrixStack.popPose();
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
        this.x = (int) (mouseX - clickX);
        this.y = (int) (mouseY - clickY);
        this.minimap.setCenter(this.x + this.minimap.getHalfSize(), this.y + this.minimap.getHalfSize(), true);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (!Screen.hasControlDown()) {
            this.minimap.addZoomLevel((int) amount);
        } else {
            this.minimap.addSize(amount > 0 ? 16 : -16);
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
