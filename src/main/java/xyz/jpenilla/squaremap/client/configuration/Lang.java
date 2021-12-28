package xyz.jpenilla.squaremap.client.configuration;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public class Lang {
    public static void actionbar(String key, Object... args) {
        send(new TranslatableComponent(key, args), true);
    }

    public static void chat(String key, Object... args) {
        send(new TranslatableComponent(key, args), false);
    }

    public static String parse(String key, Object... args) {
        if (I18n.exists(key)) {
            return I18n.get(key, args);
        }
        return String.format(key, args);
    }

    public static void send(Component text, boolean actionbar) {
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.displayClientMessage(text, actionbar);
        }
    }
}
