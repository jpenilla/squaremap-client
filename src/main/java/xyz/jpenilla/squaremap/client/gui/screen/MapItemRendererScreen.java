package xyz.jpenilla.squaremap.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import xyz.jpenilla.squaremap.client.SquaremapClientInitializer;
import xyz.jpenilla.squaremap.client.config.options.BooleanOption;
import xyz.jpenilla.squaremap.client.gui.screen.widget.Button;

import java.util.List;

public class MapItemRendererScreen extends AbstractScreen {
    private static final Component RENDERER_ENABLED = new TranslatableComponent("squaremap-client.screen.options.renderer.enabled");
    private static final Component RENDERER_ENABLED_TOOLTIP = new TranslatableComponent("squaremap-client.screen.options.renderer.enabled.tooltip");
    private static final Component FOG_OF_WAR = new TranslatableComponent("squaremap-client.screen.options.renderer.fog-of-war");
    private static final Component FOG_OF_WAR_TOOLTIP = new TranslatableComponent("squaremap-client.screen.options.renderer.fog-of-war.tooltip");

    protected MapItemRendererScreen(SquaremapClientInitializer squaremap, Screen parent) {
        super(squaremap, parent);
    }

    @Override
    public void init() {
        if (this.minecraft != null && this.parent instanceof AbstractScreen abstractScreen && abstractScreen.parent instanceof FullMapScreen) {
            abstractScreen.parent.init(this.minecraft, this.width, this.height);
        }

        int center = (int) (this.width / 2F);

        this.options = List.of(
            new Button(this, center - 154, 65, 150, 20, new BooleanOption(
                RENDERER_ENABLED, RENDERER_ENABLED_TOOLTIP,
                () -> this.squaremap.getConfig().getRenderer().getEnabled(),
                value -> {
                    this.squaremap.getConfig().getRenderer().setEnabled(value);
                    this.squaremap.setRendererEnabled(value);
                }
            )) {
                @Override
                public Component displayText() {
                    return option().getValue() ? OptionsScreen.ON : OptionsScreen.OFF;
                }
            },
            new Button(this, center + 4, 65, 150, 20, new BooleanOption(
                FOG_OF_WAR, FOG_OF_WAR_TOOLTIP,
                () -> this.squaremap.getConfig().getRenderer().getFogOfWar(),
                value -> this.squaremap.getConfig().getRenderer().setFogOfWar(value)
            )) {
                @Override
                public Component displayText() {
                    return option().getValue() ? OptionsScreen.ON : OptionsScreen.OFF;
                }
            }
        );

        this.options.forEach(this::addRenderableWidget);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float delta) {
        int centerX = (int) (this.width / 2F);

        if (this.parent instanceof AbstractScreen abstractScreen && abstractScreen.parent instanceof FullMapScreen) {
            int centerY = (int) (this.height / 2F);
            abstractScreen.parent.render(matrixStack, centerX, centerY, 0);
        }

        super.render(matrixStack, mouseX, mouseY, delta);

        drawText(matrixStack, this.title, centerX, 15);
        drawText(matrixStack, OptionsScreen.RENDERER, centerX, 30);
    }
}
