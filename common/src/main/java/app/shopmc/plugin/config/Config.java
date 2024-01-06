package app.shopmc.plugin.config;

public class Config {
    public final String key;

    public Config(final ConfigLoader loader) throws EmptyConfigFieldException {
        this.key = loader.getString("key");
        checkValues();
    }

    private void checkValues() throws EmptyConfigFieldException {
        if (key == null || key.isEmpty()) {
            throw new EmptyConfigFieldException("key");
        }
    }
}
