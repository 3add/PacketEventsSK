package threeadd.packetEventsSK.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import threeadd.packetEventsSK.PacketEventsSK;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Config {

    private static final Logger log = LoggerFactory.getLogger(Config.class);
    private final PacketEventsSK plugin;
    private FileConfiguration config;
    private File configFile;

    private final Map<String, Object> configValues = new HashMap<>();

    public Config(PacketEventsSK plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    private void loadConfig() {
        if (configFile == null) {
            configFile = new File(plugin.getDataFolder(), "config.yml");
        }
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);

        updateConfig();
        loadConfigValues();
    }

    private void updateConfig() {
        try {
            boolean hasUpdated = false;
            InputStream stream = plugin.getResource(configFile.getName());
            if (stream == null) throw new IllegalStateException("Couldn't find " + configFile.getName());
            InputStreamReader is = new InputStreamReader(stream);
            YamlConfiguration newConfig = YamlConfiguration.loadConfiguration(is);
            ConfigurationSection mainSection = newConfig.getConfigurationSection("");
            if (mainSection == null) throw new IllegalStateException("Couldn't find main section in " + configFile.getName());

            for (String key : mainSection.getKeys(true)) {
                if (!config.contains(key)) {
                    config.set(key, newConfig.get(key));
                    hasUpdated = true;
                }

                if (!newConfig.contains(key)) {
                    config.set(key, null);
                    hasUpdated = true;
                }
            }
            if (hasUpdated)
                config.save(configFile);
        } catch (IOException e) {
            log.error("Failed to save {}", configFile.getName(), e);
        }
    }

    private void loadConfigValues() {

        for (Configurable<?> configurable : Configurable.getList()) {
            String identifier = configurable.getId();

            Object object = configurable.getType().cast(config.get(identifier));
            configValues.put(identifier, object);
        }
    }

    public <T> T getConfigValue(Configurable<T> configurable) {
        Object value = configValues.get(configurable.getId());
        return configurable.getType().cast(value);
    }
}
