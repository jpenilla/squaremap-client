package net.pl3x.map.fabric.configuration.options;

import net.minecraft.network.chat.Component;
import net.pl3x.map.fabric.gui.screen.widget.Button;

public interface Option<T> {
    T getValue();

    void setValue(T value);

    Button.PressAction onPress();

    Component getName();

    Component tooltip();
}
