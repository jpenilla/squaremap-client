package xyz.jpenilla.squaremap.client.config.options;

import net.minecraft.network.chat.Component;
import xyz.jpenilla.squaremap.client.gui.screen.widget.Button;

public interface Option<T> {
    T getValue();

    void setValue(T value);

    Button.PressAction onPress();

    Component getName();

    Component tooltip();
}
