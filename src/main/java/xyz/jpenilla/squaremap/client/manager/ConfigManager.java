package xyz.jpenilla.squaremap.client.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;
import xyz.jpenilla.squaremap.client.SquaremapClientInitializer;
import xyz.jpenilla.squaremap.client.config.Config;

public class ConfigManager {
    private final Gson gson = new GsonBuilder()
        .disableHtmlEscaping()
        .serializeNulls()
        .setLenient()
        .setPrettyPrinting()
        .create();

    private Path configFile;
    private Config config;

    public Config getConfig() {
        if (this.config == null) {
            reload();
        }
        return this.config;
    }

    public void reload() {
        try {
            Path file = this.getConfigFile();
            if (!Files.exists(file)) {
                try (final InputStream stream = SquaremapClientInitializer.class.getResourceAsStream("/squaremap-client.json")) {
                    if (stream == null) {
                        throw new IllegalStateException("Could not extract default configuration from jar");
                    }
                    Files.copy(stream, file.toAbsolutePath());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (final BufferedReader reader = Files.newBufferedReader(this.configFile)) {
            this.config = this.gson.fromJson(reader, Config.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            Files.writeString(this.configFile, this.gson.toJson(this.config));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Path getConfigFile() throws IOException {
        if (this.configFile != null) {
            return this.configFile;
        }
        final Path configDir = FabricLoader.getInstance().getConfigDir();

        this.configFile = configDir.resolve("squaremap-client.json");

        // check for old configs
        if (!Files.isRegularFile(this.configFile)) {
            final Path oldConfig = FabricLoader.getInstance().getConfigDir().resolve("pl3xmap.json");
            if (Files.isRegularFile(oldConfig)) {
                Files.copy(oldConfig, this.configFile);
                Files.move(oldConfig, oldConfig.resolveSibling(oldConfig.getFileName().toString() + ".bak"));
            }
        }

        return this.configFile;
    }
}
