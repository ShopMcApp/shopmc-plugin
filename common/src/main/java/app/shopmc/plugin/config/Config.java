package app.shopmc.plugin.config;

public class Config {
    public final String customer;
    public final String server;

    public Config(final ConfigLoader loader) throws EmptyConfigFieldException {
        this.customer = loader.getString("customer");
        this.server = loader.getString("server");
        this.checkValues();
    }

    private void checkValues() throws EmptyConfigFieldException {
        if (this.customer == null || this.customer.isEmpty()) {
            throw new EmptyConfigFieldException("customer");
        }

        if (this.server == null || this.server.isEmpty()) {
            throw new EmptyConfigFieldException("server");
        }
    }
}
