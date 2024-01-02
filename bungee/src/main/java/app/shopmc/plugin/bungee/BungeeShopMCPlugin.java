package app.shopmc.plugin.bungee;

import app.shopmc.plugin.config.Config;
import app.shopmc.plugin.config.EmptyConfigFieldException;
import app.shopmc.plugin.router.Socket;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.config.YamlConfiguration;
import org.java_websocket.handshake.ServerHandshake;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import static net.md_5.bungee.config.ConfigurationProvider.getProvider;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class BungeeShopMCPlugin extends Plugin {
    private Socket socket;
    private ScheduledTask reconnectTask;
    public static Config config;
    private final ProxyServer proxyServer = ProxyServer.getInstance();
    private final BungeeShopMCPlugin pluginInstance = this;
    private final String pluginName = getDescription().getName();

    @Override
    public void onEnable() {
        // init config file
        File dataFolder = getDataFolder();
        dataFolder.mkdirs();
        File configFile = new File(dataFolder, "config.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                getLogger().log(Level.SEVERE, "Error creating config file", e);
            }
        }

        // check if config is correct
        try {
            config = new Config(new BungeeConfigLoader(getProvider(YamlConfiguration.class).load(configFile)));
        } catch (EmptyConfigFieldException | IOException exception) {
            getLogger().log(Level.SEVERE, exception.getMessage());
            proxyServer.getPluginManager().unregisterCommands(pluginInstance);
            return;
        }

        socket = new Socket(config.key) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                getLogger().info("Connection opened");
            }

            @Override
            public void onCommand(String command) {
                proxyServer.getPluginManager().dispatchCommand(proxyServer.getConsole(), command);
                getLogger().info("Executed command:" + command);
            }


            @Override
            public void onClose(int code, String reason, boolean remote) {
                getLogger().warning("Connection closed");
                reconnectTask = proxyServer.getScheduler().schedule(pluginInstance, () -> {
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
