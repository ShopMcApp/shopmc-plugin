package app.shopmc.plugin.bukkit;

import org.bukkit.configuration.ConfigurationSection;
import app.shopmc.plugin.config.ConfigLoader;

public class BukkitConfigLoader implements ConfigLoader {

    private final ConfigurationSection configFile;

    public BukkitConfigLoader(final ConfigurationSection configFile) {
        this.configFile = configFile;
    }

    @Override
    public boolean getBoolean(final String key) {
        return configFile.getBoolean(key);
    }

    @Override
    public String getString(final String key) {
        return configFile.getString(key);
    }
}
