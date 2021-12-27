package net.pl3x.map.fabric.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import net.pl3x.map.fabric.Pl3xMap;
import net.pl3x.map.fabric.configuration.Config;

public class ConfigManager {
    private final Gson gson = new GsonBuilder()
            .disableHtmlEscaping()
            .serializeNulls()
            .setLenient()
            .setPrettyPrinting()
            .create();

    private File configFile;
    private Config config;

    public Config getConfig() {
        if (this.config == null) {
            reload();
        }
        return this.config;
    }

    public void reload() {
        try {
            File file = getConfigFile();
            if (!file.exists()) {
                InputStream stream = Pl3xMap.class.getResourceAsStream("/pl3xmap.json");
                if (stream == null) {
                    throw new IllegalStateException("Could not extract default configuration from jar");
                }
                Files.copy(stream, file.getAbsoluteFile().toPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (Reader reader = new BufferedReader(new FileReader(getConfigFile()))) {
            this.config = this.gson.fromJson(reader, Config.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try (FileWriter writer = new FileWriter(getConfigFile())) {
            this.gson.toJson(this.config, writer);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File getConfigDir() throws IOException {
        File dir = new File("config");
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException("Could not create config directory");
            }
        }
        return dir;
    }

    private File getConfigFile() throws IOException {
        if (this.configFile != null) {
            return this.configFile;
        }
        this.configFile = new File(getConfigDir(), "pl3xmap.json");
        return this.configFile;
    }
}
