package net.pl3x.map.fabric.keyboard;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.pl3x.map.fabric.Pl3xMap;
import net.pl3x.map.fabric.gui.screen.FullMapScreen;
import org.lwjgl.glfw.GLFW;

public class Keyboard {
    private final List<Key> globalKeys = new ArrayList<>();
    private final Pl3xMap pl3xmap;

    public Keyboard(Pl3xMap pl3xmap) {
        this.pl3xmap = pl3xmap;
    }

    public void initialize() {
        this.globalKeys.addAll(List.of(
                new Key("pl3xmap.key.options.open", "pl3xmap.key.category", GLFW.GLFW_KEY_M, () -> Minecraft.getInstance().setScreen(new FullMapScreen(this.pl3xmap, null))),

                new Key("pl3xmap.key.minimap.zoom.increase", "pl3xmap.key.category", GLFW.GLFW_KEY_PAGE_UP, () -> this.pl3xmap.getMiniMap().addZoomLevel(1)),
                new Key("pl3xmap.key.minimap.zoom.decrease", "pl3xmap.key.category", GLFW.GLFW_KEY_PAGE_DOWN, () -> this.pl3xmap.getMiniMap().addZoomLevel(-1)),

                new Key("pl3xmap.key.minimap.toggle", "pl3xmap.key.category", GLFW.GLFW_KEY_RIGHT_BRACKET, () -> this.pl3xmap.getMiniMap().toggle())
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
