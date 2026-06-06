package dev.threeadd.packeteventssk.config;

import dev.threeadd.packeteventssk.PacketEventsSK;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Config {

    private final PacketEventsSK plugin;
    private FileConfiguration config;
    private File configFile;

    private final Map<String, Object> configValues = new HashMap<>();

    public Config(PacketEventsSK plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    private void loadConfig() {
        if (this.configFile == null) {
            this.configFile = new File(this.plugin.getDataFolder(), "config.yml");
        }
        if (!this.configFile.exists()) {
            this.plugin.saveResource("config.yml", false);
        }
        this.config = YamlConfiguration.loadConfiguration(this.configFile);

        updateConfig();
        loadConfigValues();
    }

    private void updateConfig() {
        try {
            boolean hasUpdated = false;
            InputStream stream = this.plugin.getResource(this.configFile.getName());
            if (stream == null) throw new IllegalStateException("Couldn't find " + this.configFile.getName());
            InputStreamReader is = new InputStreamReader(stream);
            YamlConfiguration newConfig = YamlConfiguration.loadConfiguration(is);
            ConfigurationSection mainSection = newConfig.getConfigurationSection("");
            if (mainSection == null)
                throw new IllegalStateException("Couldn't find main section in " + this.configFile.getName());

            for (String key : mainSection.getKeys(true)) {
                if (!this.config.contains(key)) {
                    this.config.set(key, newConfig.get(key));
                    hasUpdated = true;
                }

                if (!newConfig.contains(key)) {
                    this.config.set(key, null);
                    hasUpdated = true;
                }
            }
            if (hasUpdated)
                this.config.save(this.configFile);
        } catch (IOException e) {
            throw new IllegalStateException("Couldn't update config file " + this.configFile.getName(), e);
        }
    }

    private void loadConfigValues() {

        for (Configurable<?> configurable : Configurable.getList()) {
            String identifier = configurable.getId();

            Object object = configurable.getType().cast(this.config.get(identifier));
            this.configValues.put(identifier, object);
        }
    }

    public <T> T getConfigValue(Configurable<T> configurable) {
        Object value = this.configValues.get(configurable.getId());
        return configurable.getType().cast(value);
    }
}
