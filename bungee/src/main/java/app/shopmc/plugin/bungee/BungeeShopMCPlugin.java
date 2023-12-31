package app.shopmc.plugin.bungee;

import app.shopmc.plugin.config.Config;
import app.shopmc.plugin.config.EmptyConfigFieldException;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class BungeeShopMCPlugin extends Plugin {
    private WebSocketClient socket;
    private ScheduledTask reconnectTask;
    public static Config config;
    private final ProxyServer proxyServer = ProxyServer.getInstance();
    private final BungeeShopMCPlugin pluginInstance = this;
    private final String pluginName = getDescription().getName();

    @Override
    public void onEnable() {
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

        try {
            config = new Config(new BungeeConfigLoader(net.md_5.bungee.config.ConfigurationProvider.getProvider(net.md_5.bungee.config.YamlConfiguration.class).load(configFile)));
        } catch (EmptyConfigFieldException | IOException exception) {
            getLogger().log(Level.SEVERE, String.format("[%s] %s", pluginName, exception.getMessage()));
            proxyServer.getPluginManager().unregisterCommands(pluginInstance);
            return;
        }

        String serverURI = "wss://router.shopmc.app/" + config.key;
        socket = new WebSocketClient(URI.create(serverURI)) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                getLogger().info("[ShopMC] connection opened");
            }

            @Override
            public void onMessage(String commands) {
                long startTime = System.nanoTime();

                getLogger().info("[ShopMC] Received commands: " + commands);

                String[] commandArray = commands.split("\n");

                for (String command : commandArray) {
                    long commandStartTime = System.nanoTime();
                    proxyServer.getPluginManager().dispatchCommand(proxyServer.getConsole(), command);
                    long commandEndTime = System.nanoTime();
                    long commandExecutionTime = commandEndTime - commandStartTime;

                    logExecutionTime(command, commandExecutionTime);
                }

                long endTime = System.nanoTime();
                long totalTime = endTime - startTime;

                logTotalTime(totalTime);
            }

            private void logExecutionTime(String command, long executionTime) {
                if (executionTime < 1_000_000) {
                    getLogger().info("[ShopMC] Command executed in " + executionTime + " ns: " + command);
                } else {
                    getLogger().info("[ShopMC] Command executed in " + (executionTime / 1_000_000) + " ms: " + command);
                }
            }

            private void logTotalTime(long totalTime) {
                if (totalTime < 1_000_000) {
                    getLogger().info("[ShopMC] All commands executed in " + totalTime + " ns");
                } else {
                    getLogger().info("[ShopMC] All commands executed in " + (totalTime / 1_000_000) + " ms");
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                getLogger().warning("[ShopMC] connection closed");
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
