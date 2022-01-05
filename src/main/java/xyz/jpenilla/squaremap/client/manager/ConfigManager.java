package xyz.jpenilla.squaremap.client.manager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import xyz.jpenilla.squaremap.client.config.Config;
import xyz.jpenilla.squaremap.client.util.Util;

public class ConfigManager {
    private Path configFile;
    private Config config;

    private static HoconConfigurationLoader loader(final Path path) {
        return HoconConfigurationLoader.builder()
            .path(path)
            .build();
    }

    public Config getConfig() {
        if (this.config == null) {
            reload();
        }
        return this.config;
    }

    public void reload() {
        final Path file;
        try {
            file = this.getConfigFile();
        } catch (final IOException e) {
            throw Util.rethrow(e);
        }

        try {
            final HoconConfigurationLoader loader = loader(file);
            final CommentedConfigurationNode node = loader.load();
            this.config = node.get(Config.class);
        } catch (IOException e) {
            Util.rethrow(e);
        }
    }

    public void save() {
        final HoconConfigurationLoader loader = loader(this.configFile);
        final CommentedConfigurationNode node = loader.createNode();
        try {
            node.set(this.config);
            loader.save(node);
        } catch (final IOException e) {
            Util.rethrow(e);
        }
    }

    private Path getConfigFile() throws IOException {
        if (this.configFile != null) {
            return this.configFile;
        }
        final Path configDir = FabricLoader.getInstance().getConfigDir();

        this.configFile = configDir.resolve("squaremap-client.conf");

        // check for old configs
        if (!Files.isRegularFile(this.configFile)) {
            final Path pl3xConfig = FabricLoader.getInstance().getConfigDir().resolve("pl3xmap.json");
            if (Files.isRegularFile(pl3xConfig)) {
                Files.copy(pl3xConfig, this.configFile);
                Files.move(pl3xConfig, pl3xConfig.resolveSibling(pl3xConfig.getFileName().toString() + ".bak"));
                return this.configFile;
            }

            final Path oldSquaremapConfig = FabricLoader.getInstance().getConfigDir().resolve("squaremap-client.json");
            if (Files.isRegularFile(oldSquaremapConfig)) {
                Files.copy(oldSquaremapConfig, this.configFile);
                Files.move(oldSquaremapConfig, oldSquaremapConfig.resolveSibling(oldSquaremapConfig.getFileName().toString() + ".bak"));
            }
        }

        return this.configFile;
    }
}
