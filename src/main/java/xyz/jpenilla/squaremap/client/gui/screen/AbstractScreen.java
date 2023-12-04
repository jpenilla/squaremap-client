package xyz.jpenilla.squaremap.client.gui.screen;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import xyz.jpenilla.squaremap.client.SquaremapClientInitializer;
import xyz.jpenilla.squaremap.client.keyboard.Key;
import xyz.jpenilla.squaremap.client.scheduler.Task;

public abstract class AbstractScreen extends Screen {
    final SquaremapClientInitializer squaremap;
    final Screen parent;
    final KeyHandler keyHandler;

    protected AbstractScreen(SquaremapClientInitializer squaremap, Screen parent) {
        super(Component.translatable("squaremap-client.screen.options.title"));
        this.squaremap = squaremap;
        this.parent = parent;
        this.keyHandler = new KeyHandler();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
        if (this.minecraft != null && this.minecraft.level != null) {
            guiGraphics.fillGradient(0, 0, this.width, this.height, 0xD00F4863, 0xC0370038);
        } else {
            this.renderDirtBackground(guiGraphics);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.keyHandler.isListening(keyCode) || super.keyPressed(keyCode, scanCode, modifiers);
    }

    protected void openScreen(Screen screen) {
        if (this.minecraft != null) {
            this.minecraft.setScreen(screen);
        }
    }

    @Override
    public void onClose() {
        this.squaremap.getConfigManager().save();
        this.keyHandler.cancel();
        if (this.minecraft != null) {
            this.minecraft.setScreen(this.parent);
        }
    }

    protected void drawText(GuiGraphics guiGraphics, String text, int x, int y) {
        drawText(guiGraphics, Component.nullToEmpty(text), x, y);
    }

    protected void drawText(GuiGraphics guiGraphics, Component text, int x, int y) {
        x = x - (int) (this.font.width(text) / 2F);
        guiGraphics.drawString(this.font, text, x, y, 0xFFFFFFFF, true);
    }

    public class KeyHandler extends Task {
        private final Map<Integer, Key> keys = new HashMap<>();

        public KeyHandler() {
            super(0, true);
            SquaremapClientInitializer.instance().getScheduler().addTask(this);
        }

        public void listen(int code, Key.Action action) {
            this.keys.put(code, new Key(action));
        }

        public boolean isListening(int code) {
            return this.keys.containsKey(code);
        }

        public boolean isPressed(int code) {
            return minecraft != null && InputConstants.isKeyDown(minecraft.getWindow().getWindow(), code);
        }

        @Override
        public void run() {
            this.keys.forEach((code, key) -> {
                if (isPressed(code)) {
                    key.press();
                } else if (key.pressed()) {
                    key.release();
                }
            });
        }
    }
}
