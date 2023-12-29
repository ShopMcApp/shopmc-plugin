package app.shopmc.plugin.config;

public final class EmptyConfigFieldException extends Exception {

    public EmptyConfigFieldException(final String fieldName) {
        super(String.format("You have to fill '%s' field in configuration file for the plugin to work properly. Disabling...", fieldName));
    }
}
