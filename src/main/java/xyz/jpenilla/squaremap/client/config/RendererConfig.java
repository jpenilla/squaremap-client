package xyz.jpenilla.squaremap.client.config;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RendererConfig {
    @SerializedName("enabled")
    @Expose
    private boolean enabled;
    @SerializedName("fogOfWar")
    @Expose
    private boolean fogOfWar;

    public boolean getEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean getFogOfWar() {
        return this.fogOfWar;
    }

    public void setFogOfWar(boolean fogOfWar) {
        this.fogOfWar = fogOfWar;
    }
}
