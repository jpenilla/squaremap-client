package xyz.jpenilla.squaremap.client.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class MiniMapConfig {
    private boolean enabled = true;
    @Setting("northLock")
    private boolean northLock = true;
    private boolean circular = false;
    @Setting("drawFrame")
    private boolean drawFrame = false;
    private boolean directions = true;
    private boolean coordinates = false;
    @Setting("updateInterval")
    private int updateInterval = 5;
    @Setting("anchorX")
    private int anchorX = 100;
    @Setting("anchorZ")
    private int anchorZ = 0;
    @Setting("anchorOffsetX")
    private int anchorOffsetX = -72;
    @Setting("anchorOffsetZ")
    private int anchorOffsetZ = 72;
    private int size = 128;
    private int zoom = 3;
    private boolean showClock = false;
    private boolean clockRealTime = false;
    private boolean clock24Hrs = true;

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

    public boolean getShowClock() {
        return this.showClock;
    }

    public void setShowClock(boolean showClock) {
        this.showClock = showClock;
    }

    public boolean getClockRealTime() {
        return this.clockRealTime;
    }

    public void setClockRealTime(boolean clockRealTime) {
        this.clockRealTime = clockRealTime;
    }

    public boolean getClock24Hrs() {
        return this.clock24Hrs;
    }

    public void setClock24Hrs(boolean clock24hrs) {
        this.clock24Hrs = clock24hrs;
    }
}
