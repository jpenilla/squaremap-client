package xyz.jpenilla.squaremap.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import xyz.jpenilla.squaremap.client.SquaremapClientInitializer;
import xyz.jpenilla.squaremap.client.gui.screen.widget.Button;

public class OptionsScreen extends AbstractScreen {
    private static final Component SUBTITLE = new TranslatableComponent("squaremap-client.screen.options.subtitle");
    public static final Component RENDERER = new TranslatableComponent("squaremap-client.screen.options.renderer.title");
    private static final Component RENDERER_TOOLTIP = new TranslatableComponent("squaremap-client.screen.options.renderer.tooltip");
    public static final Component MINIMAP = new TranslatableComponent("squaremap-client.screen.options.minimap.title");
    private static final Component MINIMAP_TOOLTIP = new TranslatableComponent("squaremap-client.screen.options.minimap.tooltip");

    public static final Component ON = CommonComponents.OPTION_ON.copy().withStyle(ChatFormatting.GREEN);
    public static final Component OFF = CommonComponents.OPTION_OFF.copy().withStyle(ChatFormatting.RED);
    public static final Component YES = CommonComponents.GUI_YES.copy().withStyle(ChatFormatting.GREEN);
    public static final Component NO = CommonComponents.GUI_NO.copy().withStyle(ChatFormatting.RED);

    public OptionsScreen(Screen parent) {
        this(SquaremapClientInitializer.instance(), parent);
    }

    public OptionsScreen(SquaremapClientInitializer squaremap, Screen parent) {
        super(squaremap, parent);
    }

    @Override
    public void init() {
        if (this.minecraft != null && parent instanceof FullMapScreen) {
            this.parent.init(this.minecraft, this.width, this.height);
        }

        int center = (int) (this.width / 2F);

        this.options = List.of(
            new Button(this, center - 154, 65, 150, 20,
                RENDERER, RENDERER_TOOLTIP,
                (button) -> openScreen(new MapItemRendererScreen(this.squaremap, this))
            ),
            new Button(this, center + 4, 65, 150, 20,
                MINIMAP, MINIMAP_TOOLTIP,
                (button) -> openScreen(new MiniMapScreen(this.squaremap, this))
            )
        );

        this.options.forEach(this::addRenderableWidget);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float delta) {
        int centerX = (int) (this.width / 2F);

        if (this.parent instanceof FullMapScreen) {
            int centerY = (int) (this.height / 2F);
            this.parent.render(matrixStack, centerX, centerY, 0);
        }

        super.render(matrixStack, mouseX, mouseY, delta);

        drawText(matrixStack, this.title, centerX, 15);
        drawText(matrixStack, SUBTITLE, centerX, 30);
    }
}
