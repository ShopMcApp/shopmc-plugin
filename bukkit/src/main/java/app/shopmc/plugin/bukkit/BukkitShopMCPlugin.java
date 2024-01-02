package app.shopmc.plugin.bukkit;

import app.shopmc.plugin.config.Config;
import app.shopmc.plugin.config.EmptyConfigFieldException;
import app.shopmc.plugin.router.Socket;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.java_websocket.handshake.ServerHandshake;

import java.io.File;
import java.util.logging.Level;

public class BukkitShopMCPlugin extends JavaPlugin {
    private Socket socket;
    private Config config;
    private BukkitTask reconnectTask;

    @Override
    public void onEnable() {
        // init config file if not exist
        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveDefaultConfig();
        }

        // check if config is correct
        try {
            config = new Config(new BukkitConfigLoader(this.getConfig()));
        } catch (EmptyConfigFieldException exception) {
            getLogger().severe(exception.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        Thread networkThread = new Thread(() -> {
            socket = new Socket(config.key) {
                @Override
                public void onCommand(String command) {
                    Bukkit.getScheduler().runTask(BukkitShopMCPlugin.this, () -> Bukkit.dispatchCommand(getServer().getConsoleSender(), command));
                    getLogger().info("Executed command:" + command);
                }

                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    getLogger().info("Connection opened");
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    getLogger().warning("Connection closed");
                    reconnectTask = new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!socket.isOpen()) {
                                socket.reconnect();
                            } else {
                                cancel();
                            }
                        }
                    }.runTaskLater(BukkitShopMCPlugin.this, 200);
                }

                @Override
                public void onError(Exception ex) {
                    getLogger().log(Level.SEVERE, "An error occurred in WebSocketClient", ex);
                }
            };
            socket.connect();
            socket.setConnectionLostTimeout(0);
        });
        networkThread.start();
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
