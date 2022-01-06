package xyz.jpenilla.squaremap.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import xyz.jpenilla.squaremap.client.config.Config;
import xyz.jpenilla.squaremap.client.duck.MapTexture;
import xyz.jpenilla.squaremap.client.gui.MiniMap;
import xyz.jpenilla.squaremap.client.keyboard.Keyboard;
import xyz.jpenilla.squaremap.client.manager.ConfigManager;
import xyz.jpenilla.squaremap.client.manager.NetworkManager;
import xyz.jpenilla.squaremap.client.manager.ServerManager;
import xyz.jpenilla.squaremap.client.manager.TextureManager;
import xyz.jpenilla.squaremap.client.manager.TileManager;
import xyz.jpenilla.squaremap.client.mixin.MapRendererAccess;
import xyz.jpenilla.squaremap.client.scheduler.Scheduler;
import xyz.jpenilla.squaremap.client.util.WorldInfo;

public class SquaremapClientInitializer implements ClientModInitializer {
    private static SquaremapClientInitializer instance;

    public static SquaremapClientInitializer instance() {
        return instance;
    }

    private final ConfigManager configManager;

    private final NetworkManager networkManager;
    private final ServerManager serverManager;
    private final TileManager tileManager;
    private final TextureManager textureManager;

    private final Scheduler scheduler;
    private final Keyboard keyboard;

    private final MiniMap minimap;

    private boolean rendererEnabled;
    private WorldInfo world;

    public SquaremapClientInitializer() {
        instance = this;

        this.configManager = new ConfigManager();

        this.networkManager = new NetworkManager(this);
        this.serverManager = new ServerManager(this);
        this.tileManager = new TileManager(this);
        this.textureManager = new TextureManager();

        this.scheduler = new Scheduler();
        this.keyboard = new Keyboard(this);

        this.minimap = new MiniMap(this);
    }

    @Override
    public void onInitializeClient() {
        if (this.configManager.getConfig() == null) {
            new IllegalStateException("Could not load squaremap-client configuration").printStackTrace();
            return;
        }

        this.networkManager.initialize();
        this.serverManager.initialize();

        this.scheduler.initialize();
        this.keyboard.initialize();

        this.minimap.initialize();

        // we need a context to initialize textures on
        this.scheduler.addTask(0, this.textureManager::initialize);
    }

    public void enable() {
        this.rendererEnabled = configManager.getConfig().getRenderer().getEnabled();

        this.serverManager.enable();
        this.tileManager.enable();
    }

    public void disable() {
        this.rendererEnabled = false;

        this.scheduler.cancelAll();

        this.serverManager.disable();
        this.tileManager.disable();

        this.minimap.disable();

        clearAllData();
    }

    public boolean rendererEnabled() {
        return rendererEnabled;
    }

    public void setRendererEnabled(boolean value) {
        this.rendererEnabled = value;
    }

    public void updateAllMapTextures() {
        ((MapRendererAccess) Minecraft.getInstance().gameRenderer.getMapRenderer()).maps()
            .values().forEach(texture -> ((MapTexture) texture).updateImage());
    }

    public void clearAllData() {
        this.serverManager.disable();
        this.tileManager.disable();

        updateAllMapTextures();
    }

    public Config getConfig() {
        return this.configManager.getConfig();
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public ServerManager getServerManager() {
        return this.serverManager;
    }

    public NetworkManager getNetworkManager() {
        return this.networkManager;
    }

    public TileManager getTileManager() {
        return this.tileManager;
    }

    public TextureManager getTextureManager() {
        return this.textureManager;
    }

    public Scheduler getScheduler() {
        return this.scheduler;
    }

    public MiniMap getMiniMap() {
        return this.minimap;
    }

    public WorldInfo getWorld() {
        return this.world;
    }

    public void setWorld(WorldInfo world) {
        this.world = world;
    }
}
