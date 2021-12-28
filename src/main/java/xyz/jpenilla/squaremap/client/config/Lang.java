package xyz.jpenilla.squaremap.client.config;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public class Lang {
    public static void actionbar(String key, Object... args) {
        send(new TranslatableComponent(key, args), true);
    }

    public static void chat(String key, Object... args) {
        send(new TranslatableComponent(key, args), false);
    }

    public static void send(Component text, boolean actionbar) {
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.displayClientMessage(text, actionbar);
        }
    }
}
