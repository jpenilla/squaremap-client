package xyz.jpenilla.squaremap.client.util;

import net.minecraft.resources.ResourceLocation;

public record WorldInfo(ResourceLocation key, String name, int zoomMax, int zoomDefault, int zoomExtra) {
}
