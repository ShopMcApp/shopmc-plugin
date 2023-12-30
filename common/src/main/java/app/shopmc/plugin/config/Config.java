package app.shopmc.plugin.config;

public class Config {
    public final String key;

    public Config(final ConfigLoader loader) throws EmptyConfigFieldException {
        this.key = loader.getString("key");
        this.checkValues();
    }

    private void checkValues() throws EmptyConfigFieldException {
        if (this.key == null || this.key.isEmpty()) {
            throw new EmptyConfigFieldException("key");
        }
    }
}
