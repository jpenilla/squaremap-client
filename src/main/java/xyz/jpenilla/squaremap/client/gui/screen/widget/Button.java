package xyz.jpenilla.squaremap.client.gui.screen.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import xyz.jpenilla.squaremap.client.config.options.BooleanOption;

public class Button extends AbstractButton implements Tickable {
    private final Screen screen;
    private final Component tooltip;
    private final PressAction onPress;
    private final BooleanOption option;
    private final Function<Button, Component> displayText;
    private int tooltipDelay;

    public Button(Screen screen, int x, int y, int width, int height, BooleanOption option, Function<Button, Component> displayText) {
        this(screen, x, y, width, height, option.getName(), option.tooltip(), option.onPress(), option, displayText);
    }

    public Button(Screen screen, int x, int y, int width, int height, Component name, Component tooltip, PressAction onPress) {
        this(screen, x, y, width, height, name, tooltip, onPress, null, null);
    }

    public Button(Screen screen, int x, int y, int width, int height, Component name, Component tooltip, PressAction onPress, BooleanOption option, Function<Button, Component> displayText) {
        super(x, y, width, height, name);
        this.screen = screen;
        this.tooltip = tooltip;
        this.option = option;
        this.onPress = onPress;
        this.displayText = displayText;

        this.updateMessage();
    }

    @Override
    public void onPress() {
        this.onPress.onPress(this);
    }

    @Override
    public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float delta) {
        super.renderButton(matrixStack, mouseX, mouseY, delta);
        if (this.isHoveredOrFocused() && this.tooltipDelay > 10) {
            this.renderToolTip(matrixStack, mouseX, mouseY);
        }
    }

    @Override
    public void tick() {
        if (this.isHoveredOrFocused() && this.active) {
            this.tooltipDelay++;
        } else if (this.tooltipDelay > 0) {
            this.tooltipDelay = 0;
        }
    }

    @Override
    public void renderToolTip(PoseStack matrixStack, int mouseX, int mouseY) {
        List<FormattedCharSequence> tooltip = Minecraft.getInstance().font.split(this.tooltip, 150);
        this.screen.renderTooltip(matrixStack, tooltip, mouseX, mouseY);
    }

    @Override
    public void updateNarration(NarrationElementOutput builder) {
        this.defaultButtonNarrationText(builder);
    }

    public void updateMessage() {
        if (this.option != null) {
            final MutableComponent msg = Component.literal("");
            msg.append(this.option.getName());
            msg.append(Component.literal(": "));
            msg.append(this.displayText());
            this.setMessage(msg);
        }
    }

    public BooleanOption option() {
        return this.option;
    }

    public Component displayText() {
        if (this.displayText != null) {
            return this.displayText.apply(this);
        }
        return Component.literal(this.option.getValue().toString());
    }

    @FunctionalInterface
    public interface PressAction {
        void onPress(Button button);
    }
}
