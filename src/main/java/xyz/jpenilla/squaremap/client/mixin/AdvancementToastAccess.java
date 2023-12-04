package xyz.jpenilla.squaremap.client.mixin;

import net.minecraft.client.gui.components.toasts.AdvancementToast;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AdvancementToast.class)
public interface AdvancementToastAccess {

    @Accessor("BACKGROUND_SPRITE")
    static ResourceLocation backgroundSprite() {
        throw new AssertionError();
    }

}
