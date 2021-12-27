package net.pl3x.map.fabric.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.gui.MapRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MapRenderer.class)
public interface MapRendererAccess {

    @Accessor("maps")
    Int2ObjectMap<MapRenderer.MapInstance> maps();

}
