package app.shopmc.plugin.velocity;

import app.shopmc.plugin.config.ConfigLoader;
import com.moandjiezana.toml.Toml;

public class VelocityConfigLoader implements ConfigLoader {

    private final Toml configFile;

    public VelocityConfigLoader(final Toml configFile) {
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
