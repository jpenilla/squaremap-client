package xyz.jpenilla.squaremap.client.gui.screen.widget;

import java.text.NumberFormat;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import xyz.jpenilla.squaremap.client.mixin.AdvancementToastAccess;

public class Coordinates extends AbstractWidget {
    private static final NumberFormat NUMBER = NumberFormat.getInstance(Locale.US);
    private final FullMapWidget fullmap;
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    public Coordinates(FullMapWidget fullmap, int x, int y, int width, int height) {
        super(x, y, width, height, null);
        this.fullmap = fullmap;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Component getCoordinates(int mouseX, int mouseY) {
        String x = NUMBER.format(this.fullmap.getPosX(mouseX));
        String y = NUMBER.format(this.fullmap.getPosY(mouseY));
        return Component.translatable("%s %s", x, y);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        Minecraft minecraftClient = Minecraft.getInstance();
        Font textRenderer = minecraftClient.font;
        Component coordinates = getCoordinates(mouseX, mouseY);
        int w2 = (int) ((textRenderer.width(coordinates) + 20) / 2F);
        int h2 = (int) (this.height / 2F);
        guiGraphics.blitSprite(AdvancementToastAccess.backgroundSprite(), x, y, w2 * 2, h2 * 2);
        guiGraphics.drawCenteredString(textRenderer, coordinates, this.x + w2, this.y + (this.height - 8) / 2, 0xffcccccc);
    }

    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput builder) {
    }
}
