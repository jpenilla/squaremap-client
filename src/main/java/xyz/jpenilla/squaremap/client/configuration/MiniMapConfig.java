package xyz.jpenilla.squaremap.client.configuration;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MiniMapConfig {
    @SerializedName("enabled")
    @Expose
    private boolean enabled;
    @SerializedName("northLock")
    @Expose
    private boolean northLock;
    @SerializedName("circular")
    @Expose
    private boolean circular;
    @SerializedName("drawFrame")
    @Expose
    private boolean drawFrame;
    @SerializedName("directions")
    @Expose
    private boolean directions;
    @SerializedName("coordinates")
    @Expose
    private boolean coordinates;
    @SerializedName("updateInterval")
    @Expose
    private int updateInterval;
    @SerializedName("anchorX")
    @Expose
    private int anchorX;
    @SerializedName("anchorZ")
    @Expose
    private int anchorZ;
    @SerializedName("anchorOffsetX")
    @Expose
    private int anchorOffsetX;
    @SerializedName("anchorOffsetZ")
    @Expose
    private int anchorOffsetZ;
    @SerializedName("size")
    @Expose
    private int size;
    @SerializedName("zoom")
    @Expose
    private int zoom;

    public boolean getEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean getNorthLock() {
        return this.northLock;
    }

    public void setNorthLock(boolean northLock) {
        this.northLock = northLock;
    }

    public boolean getCircular() {
        return this.circular;
    }

    public void setCircular(boolean circular) {
        this.circular = circular;
    }

    public boolean getDrawFrame() {
        return this.drawFrame;
    }

    public void setDrawFrame(boolean drawFrame) {
        this.drawFrame = drawFrame;
    }

    public boolean getDirections() {
        return this.directions;
    }

    public void setDirections(boolean directions) {
        this.directions = directions;
    }

    public boolean getCoordinates() {
        return this.coordinates;
    }

    public void setCoordinates(Boolean coordinates) {
        this.coordinates = coordinates;
    }

    public int getUpdateInterval() {
        return this.updateInterval;
    }

    public void setUpdateInterval(int updateInterval) {
        this.updateInterval = updateInterval;
    }

    public int getAnchorX() {
        return this.anchorX;
    }

    public void setAnchorX(int anchorX) {
        this.anchorX = anchorX;
    }

    public int getAnchorZ() {
        return this.anchorZ;
    }

    public void setAnchorZ(int anchorZ) {
        this.anchorZ = anchorZ;
    }

    public int getAnchorOffsetX() {
        return this.anchorOffsetX;
    }

    public void setAnchorOffsetX(int anchorOffsetX) {
        this.anchorOffsetX = anchorOffsetX;
    }

    public int getAnchorOffsetZ() {
        return this.anchorOffsetZ;
    }

    public void setAnchorOffsetZ(int anchorOffsetZ) {
        this.anchorOffsetZ = anchorOffsetZ;
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getZoom() {
        return this.zoom;
    }

    public void setZoom(int zoom) {
        this.zoom = zoom;
    }
}
