package net.pl3x.map.fabric.mixin;

import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Toast.class)
public interface ToastAccess {

    @Accessor("TEXTURE")
    static ResourceLocation texture() {
        throw new AssertionError();
    }

}
