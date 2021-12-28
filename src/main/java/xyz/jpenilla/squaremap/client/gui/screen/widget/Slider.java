package xyz.jpenilla.squaremap.client.gui.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import xyz.jpenilla.squaremap.client.configuration.options.IntegerOption;
import org.lwjgl.glfw.GLFW;

public class Slider extends AbstractWidget implements Tickable {
    private final Screen screen;
    private final IntegerOption option;
    private final double step;
    private double value;
    private int tooltipDelay;

    public Slider(Screen screen, int x, int y, int width, int height, IntegerOption option) {
        super(x, y, width, height, option.getName());
        this.screen = screen;
        this.option = option;
        this.step = option.getMin() / (double) option.getMax();
        this.value = getRatio(option.getValue());

        updateMessage();
    }

    @Override
    protected int getYImage(boolean hovered) {
        return 0;
    }

    @Override
    public void updateNarration(NarrationElementOutput builder) {
        this.defaultButtonNarrationText(builder);
    }

    @Override
    public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float delta) {
        super.renderButton(matrixStack, mouseX, mouseY, delta);
        if (this.isHoveredOrFocused() && this.tooltipDelay > 10) {
            this.renderToolTip(matrixStack, mouseX, mouseY);
        }
    }

    @Override
    protected void renderBg(PoseStack matrixStack, Minecraft client, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int i = (this.isHoveredOrFocused() ? 2 : 1) * 20;
        this.blit(matrixStack, this.x + (int) (this.value * (double) (this.width - 8)), this.y, 0, 46 + i, 4, 20);
        this.blit(matrixStack, this.x + (int) (this.value * (double) (this.width - 8)) + 4, this.y, 196, 46 + i, 4, 20);
    }

    @Override
    public void renderToolTip(PoseStack matrixStack, int mouseX, int mouseY) {
        List<FormattedCharSequence> tooltip = Minecraft.getInstance().font.split(this.option.tooltip(), 200);
        this.screen.renderTooltip(matrixStack, tooltip, mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean bl = keyCode == GLFW.GLFW_KEY_LEFT;
        if (bl || keyCode == GLFW.GLFW_KEY_RIGHT) {
            float f = bl ? -1.0F : 1.0F;
            this.setValue(this.value + (double) (f / (float) (this.width - 8)));
        }
        return false;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.setValueFromMouse(mouseX);
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
        this.setValueFromMouse(mouseX);
        super.onDrag(mouseX, mouseY, deltaX, deltaY);
    }

    private void setValueFromMouse(double mouseX) {
        this.setValue((mouseX - (double) (this.x + 4)) / (double) (this.width - 8));
    }

    private void setValue(double value) {
        double d = this.value;
        this.value = Mth.clamp(value, 0.0D, 1.0D);
        int intVal = getValue(this.value);
        if (d != this.value) {
            this.option.setValue(intVal);
        }
        this.value = getRatio(intVal);

        updateMessage();
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        super.playDownSound(Minecraft.getInstance().getSoundManager());
    }

    private double getRatio(double value) {
        return Mth.clamp((this.adjust(value) - this.option.getMin()) / (this.option.getMax() - this.option.getMin()), 0.0D, 1.0D);
    }

    private int getValue(double ratio) {
        return (int) Math.round(this.adjust(Mth.lerp(Mth.clamp(ratio, 0.0D, 1.0D), this.option.getMin(), this.option.getMax())));
    }

    private double adjust(double value) {
        if (this.step > 0.0F) {
            value = this.step * (float) Math.round(value / this.step);
        }
        return Mth.clamp(value, this.option.getMin(), this.option.getMax());
    }

    private void updateMessage() {
        setMessage(Component.nullToEmpty(this.option.getName().getString() + ": " + this.option.getValue()));
    }

    @Override
    public void tick() {
        if (this.isHoveredOrFocused()) {
            this.tooltipDelay++;
        } else if (this.tooltipDelay > 0) {
            this.tooltipDelay = 0;
        }
    }
}
