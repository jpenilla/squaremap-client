package xyz.jpenilla.squaremap.client.configuration;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Config {
    @SerializedName("renderer")
    @Expose
    private RendererConfig renderer;
    @SerializedName("minimap")
    @Expose
    private MiniMapConfig minimap;

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
}
