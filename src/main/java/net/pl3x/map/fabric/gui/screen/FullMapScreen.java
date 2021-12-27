package net.pl3x.map.fabric.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.pl3x.map.fabric.Pl3xMap;
import net.pl3x.map.fabric.gui.screen.widget.Button;
import net.pl3x.map.fabric.gui.screen.widget.Coordinates;
import net.pl3x.map.fabric.gui.screen.widget.FullMapWidget;

public class FullMapScreen extends AbstractScreen {
    private static final Component OPTIONS = new TranslatableComponent("pl3xmap.screen.full-map.options");
    private static final Component OPTIONS_TOOLTIP = new TranslatableComponent("pl3xmap.screen.full-map.options.tooltip");

    private static final Component ZOOM_IN = Component.nullToEmpty("+");
    private static final Component ZOOM_OUT = Component.nullToEmpty("-");
    private static final Component LINK = Component.nullToEmpty("?");

    private static final Component BLANK = Component.nullToEmpty("");

    private static final Component LINK_CONFIRMATION = new TranslatableComponent("chat.link.confirmTrusted");

    private static final Component OPEN = new TranslatableComponent("chat.link.open");
    private static final Component COPY = new TranslatableComponent("chat.copy");
    private static final Component CANCEL = CommonComponents.GUI_CANCEL;

    private final List<GuiEventListener> reverse = new ArrayList<>();
    private final List<net.minecraft.client.gui.components.Button> confirmLink = new ArrayList<>();

    private FullMapWidget fullmap;

    private ResourceLocation background;
    private Component openURL;

    public FullMapScreen(Pl3xMap pl3xmap, Screen parent) {
        super(pl3xmap, parent);

        // hide minimap so we're not drawing it needlessly
        this.pl3xmap.getMiniMap().setVisible(false);
    }

    @Override
    protected void init() {
        updateBackground();

        // skip straight to options screen if no pl3x world set
        if (this.pl3xmap.getWorld() == null) {
            openScreen(new OptionsScreen(this.pl3xmap, null));
            return;
        }

        // fullmap is a clickable widget for click and drag conveniences
        addRenderableWidget(this.fullmap = new FullMapWidget(this.pl3xmap, this.minecraft, this.width, this.height));

        List.of(
                new Button(this, 5, 5, 20, 20, ZOOM_IN, BLANK, (button) -> this.fullmap.zoom(1)),
                new Button(this, 5, 25, 20, 20, ZOOM_OUT, BLANK, (button) -> this.fullmap.zoom(-1)),
                new Button(this, 5, this.height - 25, 20, 20, LINK, BLANK, (button) -> this.openURL = this.fullmap.getUrl()),
                new Coordinates(this.fullmap, 30, this.height - 25, 50, 20),
                // TODO sidebar for world and player select
                new Button(this, this.width - 87, this.height - 25, 80, 20, OPTIONS, OPTIONS_TOOLTIP, (button) -> openScreen(new OptionsScreen(this.pl3xmap, this)))
        ).forEach(this::addRenderableWidget);

        this.confirmLink.clear();
        this.confirmLink.addAll(List.of(
                new net.minecraft.client.gui.components.Button(this.width / 2 - 50 - 105, this.height / 6 + 96, 100, 20, OPEN, (button) -> openLink()),
                new net.minecraft.client.gui.components.Button(this.width / 2 - 50, this.height / 6 + 96, 100, 20, COPY, (button) -> copyLink()),
                new net.minecraft.client.gui.components.Button(this.width / 2 - 50 + 105, this.height / 6 + 96, 100, 20, CANCEL, (button) -> cancel())
        ));

        // reverse the elements to draw bottom up and click top down
        this.reverse.addAll(this.children());
        Collections.reverse(this.reverse);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float delta) {
        if (this.background == null) {
            renderBackground(matrixStack);
        } else {
            this.pl3xmap.getTextureManager().drawTexture(matrixStack, this.background, 0, 0, this.width, this.height, 0, 0, this.width / 512F, this.height / 512F);
        }

        super.render(matrixStack, mouseX, mouseY, delta);

        drawText(matrixStack, this.title, (int) (this.width / 2F), 15);

        if (openURL != null) {
            this.fillGradient(matrixStack, 0, 0, this.width, this.height, 0xD00F4863, 0xC0370038);
            drawCenteredString(matrixStack, this.font, LINK_CONFIRMATION, this.width / 2, 70, 16777215);
            drawCenteredString(matrixStack, this.font, this.openURL, this.width / 2, 90, 16777215);
            for (net.minecraft.client.gui.components.Button buttonWidget : this.confirmLink) {
                buttonWidget.render(matrixStack, mouseX, mouseY, delta);
            }
        }
    }

    @Override
    public void renderBackground(PoseStack matrixStack, int vOffset) {
        // don't waste cpu on super's background
    }

    private void updateBackground() {
        if (this.minecraft == null || this.minecraft.level == null) {
            this.background = null;
        } else {
            this.background = this.pl3xmap.getTextureManager().getTexture(this.minecraft.level);
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
        this.pl3xmap.getMiniMap().setVisible(true);
        super.onClose();
    }

    private void openLink() {
        Util.getPlatform().openUri(this.openURL.getContents());
        this.openURL = null;
    }

    private void copyLink() {
        if (this.minecraft != null) {
            this.minecraft.keyboardHandler.setClipboard(this.openURL.getContents());
        }
        this.openURL = null;
    }

    private void cancel() {
        this.openURL = null;
    }
}
