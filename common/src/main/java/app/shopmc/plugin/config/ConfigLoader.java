package app.shopmc.plugin.config;


public interface ConfigLoader {

    boolean getBoolean(final String key);

    String getString(final String key);

}