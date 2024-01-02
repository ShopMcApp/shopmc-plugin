package app.shopmc.plugin.bungee;

import net.md_5.bungee.config.Configuration;
import app.shopmc.plugin.config.ConfigLoader;

public class BungeeConfigLoader implements ConfigLoader {

    private final Configuration configFile;

    public BungeeConfigLoader(final Configuration configFile) {
        this.configFile = configFile;
    }

    @Override
    public boolean getBoolean(final String key) {
        return this.configFile.getBoolean(key);
    }

    @Override
    public String getString(final String key) {
        return this.configFile.getString(key);
    }
}
