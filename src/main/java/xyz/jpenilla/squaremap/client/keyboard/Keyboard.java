package xyz.jpenilla.squaremap.client.keyboard;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import xyz.jpenilla.squaremap.client.SquaremapClientInitializer;
import xyz.jpenilla.squaremap.client.gui.screen.FullMapScreen;
import org.lwjgl.glfw.GLFW;

public class Keyboard {
    private final List<Key> globalKeys = new ArrayList<>();
    private final SquaremapClientInitializer squaremap;

    public Keyboard(SquaremapClientInitializer squaremap) {
        this.squaremap = squaremap;
    }

    public void initialize() {
        this.globalKeys.addAll(List.of(
                new Key("squaremap-client.key.options.open", "squaremap-client.key.category", GLFW.GLFW_KEY_M, () -> Minecraft.getInstance().setScreen(new FullMapScreen(this.squaremap, null))),

                new Key("squaremap-client.key.minimap.zoom.increase", "squaremap-client.key.category", GLFW.GLFW_KEY_PAGE_UP, () -> this.squaremap.getMiniMap().addZoomLevel(1)),
                new Key("squaremap-client.key.minimap.zoom.decrease", "squaremap-client.key.category", GLFW.GLFW_KEY_PAGE_DOWN, () -> this.squaremap.getMiniMap().addZoomLevel(-1)),

                new Key("squaremap-client.key.minimap.toggle", "squaremap-client.key.category", GLFW.GLFW_KEY_RIGHT_BRACKET, () -> this.squaremap.getMiniMap().toggle())
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> this.globalKeys.forEach(Key::check));
    }

    private static class Key {
        private final KeyMapping binding;
        private final Press press;

        private Key(String name, String category, int key, Press press) {
            this.binding = KeyBindingHelper.registerKeyBinding(new KeyMapping(name, InputConstants.Type.KEYSYM, key, category));
            this.press = press;
        }

        private void check() {
            while (this.binding.consumeClick()) {
                this.press.execute();
            }
        }

        @FunctionalInterface
        private interface Press {
            void execute();
        }
    }
}
