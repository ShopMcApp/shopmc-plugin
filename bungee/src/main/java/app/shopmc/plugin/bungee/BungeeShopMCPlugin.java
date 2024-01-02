package app.shopmc.plugin.bungee;

import app.shopmc.plugin.config.Config;
import app.shopmc.plugin.config.EmptyConfigFieldException;
import app.shopmc.plugin.resource.ResourceLoader;
import app.shopmc.plugin.resource.ResourceLoaderException;
import app.shopmc.plugin.router.Socket;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.config.Configuration;
import org.java_websocket.handshake.ServerHandshake;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class BungeeShopMCPlugin extends Plugin {
    private Socket socket;
    public static Config config;
    private ScheduledTask reconnectTask;
    private final ProxyServer proxyServer = ProxyServer.getInstance();

    @Override
    public void onEnable() {
        // init config file
        final ResourceLoader<Configuration> resourceLoader = new BungeeResourceLoader(this.getClass(), this.getDataFolder());
        try {
            if (resourceLoader.saveDefault("config.yml")) {
                this.getLogger().info("Default file config.yml has been saved, configure it and restart proxy");
                return;
            }

            try {
                final Configuration cfgFile = resourceLoader.load("config.yml");
                config = new Config(new BungeeConfigLoader(cfgFile));
            } catch (final EmptyConfigFieldException exception) {
                this.getLogger().severe(exception.getMessage());
            }
        } catch (final ResourceLoaderException exception) {
            this.getLogger().severe("Reason: " + exception.getCause().getMessage());
        }

        socket = new Socket(config.key) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                getLogger().info("Connection opened");
            }

            @Override
            public void onCommand(String command) {
                proxyServer.getPluginManager().dispatchCommand(proxyServer.getConsole(), command);
                getLogger().info("Executed command: " + command);
            }


            @Override
            public void onClose(int code, String reason, boolean remote) {
                getLogger().warning("Connection closed");
                reconnectTask = proxyServer.getScheduler().schedule(BungeeShopMCPlugin.this, () -> {
                    if (!socket.isOpen()) {
                        socket.reconnect();
                    } else {
                        reconnectTask.cancel();
                    }
                }, 200, TimeUnit.MILLISECONDS);
            }

            @Override
            public void onError(Exception ex) {
                getLogger().log(Level.SEVERE, "An error occurred in WebSocketClient", ex);
            }
        };
        socket.connect();
        socket.setConnectionLostTimeout(0);
    }

    @Override
    public void onDisable() {
        if (socket != null) {
            socket.close();
        }
        if (reconnectTask != null) {
            reconnectTask.cancel();
        }
    }
}
