package xyz.jpenilla.squaremap.client.util;

public class Constants {
    public static final String MODID = "squaremap-client";

    public static final int SERVER_DATA = 0;
    public static final int MAP_DATA = 1;
    public static final int UPDATE_WORLD = 2;

    public static final int PROTOCOL = 3;

    public static final int RESPONSE_SUCCESS = 200;

    public static final int ERROR_NO_SUCH_MAP = -1;
    public static final int ERROR_NO_SUCH_WORLD = -2;
    public static final int ERROR_NOT_VANILLA_MAP = -3;

    public static final long TILE_UPDATE_INTERVAL = 10000; // 10 seconds
}
