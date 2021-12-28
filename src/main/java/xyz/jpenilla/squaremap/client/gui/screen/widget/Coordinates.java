package xyz.jpenilla.squaremap.client.gui.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.text.NumberFormat;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import xyz.jpenilla.squaremap.client.mixin.ToastAccess;

public class Coordinates extends GuiComponent implements Widget, GuiEventListener, NarratableEntry {
    private static final NumberFormat NUMBER = NumberFormat.getInstance(Locale.US);
    private final FullMapWidget fullmap;
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    public Coordinates(FullMapWidget fullmap, int x, int y, int width, int height) {
        this.fullmap = fullmap;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Component getCoordinates(int mouseX, int mouseY) {
        String x = NUMBER.format(this.fullmap.getPosX(mouseX));
        String y = NUMBER.format(this.fullmap.getPosY(mouseY));
        return new TranslatableComponent("%s %s", x, y);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float delta) {
        Minecraft minecraftClient = Minecraft.getInstance();
        Font textRenderer = minecraftClient.font;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, ToastAccess.texture());
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        Component coordinates = getCoordinates(mouseX, mouseY);
        int w2 = (int) ((textRenderer.width(coordinates) + 20) / 2F);
        int h2 = (int) (this.height / 2F);
        blit(matrixStack, x, y, w2, h2, 0, 0, w2, h2, 256, 256);
        blit(matrixStack, x, y + h2, w2, h2, 0, 32 - h2, w2, h2, 256, 256);
        blit(matrixStack, x + w2, y, w2, h2, 160 - w2, 0, w2, h2, 256, 256);
        blit(matrixStack, x + w2, y + h2, w2, h2, 160 - w2, 32 - h2, w2, h2, 256, 256);
        drawCenteredString(matrixStack, textRenderer, coordinates, this.x + w2, this.y + (this.height - 8) / 2, 0xffcccccc);
    }

    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput builder) {
    }
}
