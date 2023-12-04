package xyz.jpenilla.squaremap.client.gui.screen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import xyz.jpenilla.squaremap.client.SquaremapClientInitializer;
import xyz.jpenilla.squaremap.client.config.options.BooleanOption;
import xyz.jpenilla.squaremap.client.gui.screen.widget.Button;
import xyz.jpenilla.squaremap.client.gui.screen.widget.Coordinates;
import xyz.jpenilla.squaremap.client.gui.screen.widget.FullMapWidget;

public class FullMapScreen extends AbstractScreen {
    private static final Component OPTIONS = Component.translatable("squaremap-client.screen.full-map.options");
    private static final Component OPTIONS_TOOLTIP = Component.translatable("squaremap-client.screen.full-map.options.tooltip");

    private static final Component ZOOM_IN = Component.literal("+");
    private static final Component ZOOM_OUT = Component.literal("-");
    private static final Component LINK = Component.literal("?");

    private static final Component SHOW_SELF = Component.translatable("squaremap-client.screen.full-map.show-self");
    private static final Component SHOW_SELF_TOOLTIP = Component.translatable("squaremap-client.screen.full-map.show-self.tooltip");

    private static final Component BLANK = Component.empty();

    private static final Component LINK_CONFIRMATION = Component.translatable("chat.link.confirmTrusted");

    private static final Component OPEN = Component.translatable("chat.link.open");
    private static final Component COPY = Component.translatable("chat.copy");
    private static final Component CANCEL = CommonComponents.GUI_CANCEL;

    private final List<GuiEventListener> reverse = new ArrayList<>();
    private final List<net.minecraft.client.gui.components.Button> confirmLink = new ArrayList<>();

    private FullMapWidget fullmap;

    private ResourceLocation background;
    private Component openURL;

    public FullMapScreen(SquaremapClientInitializer squaremap, Screen parent) {
        super(squaremap, parent);

        // hide minimap so we're not drawing it needlessly
        this.squaremap.getMiniMap().setVisible(false);
    }

    @Override
    protected void init() {
        updateBackground();

        // skip straight to options screen if no squaremap world set
        if (this.squaremap.getWorld() == null) {
            openScreen(new OptionsScreen(this.squaremap, null));
            return;
        }

        // fullmap is a clickable widget for click and drag conveniences
        addRenderableWidget(this.fullmap = new FullMapWidget(this.squaremap, this.minecraft, this.width, this.height));

        List.of(
            new Button(this, 5, 5, 20, 20, ZOOM_IN, BLANK, (button) -> this.fullmap.zoom(1)),
            new Button(this, 5, 25, 20, 20, ZOOM_OUT, BLANK, (button) -> this.fullmap.zoom(-1)),
            new Button(this, 5, this.height - 25, 20, 20, LINK, BLANK, (button) -> this.openURL = this.fullmap.getUrl()),
            new Coordinates(this.fullmap, 30, this.height - 25, 50, 20),
            // TODO sidebar for world and player select
            new Button(this, this.width - 87, this.height - 25, 80, 20, OPTIONS, OPTIONS_TOOLTIP, (button) -> openScreen(new OptionsScreen(this.squaremap, this))),
            new Button(this, this.width - 107, 5, 100, 20, new BooleanOption(
                SHOW_SELF, SHOW_SELF_TOOLTIP,
                () -> this.fullmap.showSelf(),
                value -> {
                    this.fullmap.showSelf(value);
                    this.squaremap.getConfig().fullMap().showSelf(value);
                }
            ), OptionsScreen::yesNo)
        ).forEach(this::addRenderableWidget);

        this.confirmLink.clear();
        this.confirmLink.addAll(List.of(
            net.minecraft.client.gui.components.Button.builder(OPEN, button -> openLink()).pos(this.width / 2 - 50 - 105, this.height / 6 + 96).size(100, 20).build(),
            net.minecraft.client.gui.components.Button.builder(COPY, button -> copyLink()).pos(this.width / 2 - 50, this.height / 6 + 96).size(100, 20).build(),
            net.minecraft.client.gui.components.Button.builder(CANCEL, button -> cancel()).pos(this.width / 2 - 50 + 105, this.height / 6 + 96).size(100, 20).build()
        ));

        // reverse the elements to draw bottom up and click top down
        this.reverse.addAll(this.children());
        Collections.reverse(this.reverse);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        if (this.background == null) {
            renderBackground(guiGraphics, mouseX, mouseY, delta);
        } else {
            this.squaremap.getTextureManager().drawTexture(guiGraphics.pose(), this.background, 0, 0, this.width, this.height, 0, 0, this.width / 512F, this.height / 512F);
        }

        super.render(guiGraphics, mouseX, mouseY, delta);

        drawText(guiGraphics, this.title, (int) (this.width / 2F), 15);

        if (openURL != null) {
            guiGraphics.fillGradient(0, 0, this.width, this.height, 0xD00F4863, 0xC0370038);
            guiGraphics.drawCenteredString(this.font, LINK_CONFIRMATION, this.width / 2, 70, 16777215);
            guiGraphics.drawCenteredString(this.font, this.openURL, this.width / 2, 90, 16777215);
            for (net.minecraft.client.gui.components.Button buttonWidget : this.confirmLink) {
                buttonWidget.render(guiGraphics, mouseX, mouseY, delta);
            }
        }
    }

    @Override
    public void renderBackground(GuiGraphics matrixStack, int i, int j, float f) {
        // don't waste cpu on super's background
    }

    private void updateBackground() {
        if (this.minecraft == null || this.minecraft.level == null) {
            this.background = null;
        } else {
            this.background = this.squaremap.getTextureManager().getTexture(this.minecraft.level);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // reverse so we click buttons before clicking map
        Iterator<? extends GuiEventListener> iter = (this.openURL != null ? this.confirmLink : this.reverse).iterator();
        GuiEventListener element;
        do {
            if (!iter.hasNext()) {
                return false;
            }
            element = iter.next();
        } while (!element.mouseClicked(mouseX, mouseY, button));
        this.setFocused(element);
        if (button == 0) {
            this.setDragging(true);
        }
        return true;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        if (this.openURL != null) {
            cancel();
            return false;
        }
        return true;
    }

    @Override
    public void onClose() {
        this.fullmap.onClose();
        this.squaremap.getMiniMap().setVisible(true);
        super.onClose();
    }

    private void openLink() {
        Util.getPlatform().openUri(this.openURL.getString());
        this.openURL = null;
    }

    private void copyLink() {
        if (this.minecraft != null) {
            this.minecraft.keyboardHandler.setClipboard(this.openURL.getString());
        }
        this.openURL = null;
    }

    private void cancel() {
        this.openURL = null;
    }
}
