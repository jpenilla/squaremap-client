package xyz.jpenilla.squaremap.client.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class Config {
    private RendererConfig renderer = new RendererConfig();
    private MiniMapConfig minimap = new MiniMapConfig();
    private FullMap fullMap = new FullMap();

    public RendererConfig getRenderer() {
        return renderer;
    }

    public void setRenderer(RendererConfig renderer) {
        this.renderer = renderer;
    }

    public MiniMapConfig getMinimap() {
        return minimap;
    }

    public void setMinimap(MiniMapConfig minimap) {
        this.minimap = minimap;
    }

    public FullMap fullMap() {
        return this.fullMap;
    }

    @ConfigSerializable
    public static final class FullMap {
        private boolean showSelf = true;

        public boolean showSelf() {
            return this.showSelf;
        }

        public void showSelf(boolean showSelf) {
            this.showSelf = showSelf;
        }
    }
}
