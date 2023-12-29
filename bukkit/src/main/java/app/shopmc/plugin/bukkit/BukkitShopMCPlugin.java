package app.shopmc.plugin.bukkit;

import app.shopmc.plugin.config.Config;
import app.shopmc.plugin.config.EmptyConfigFieldException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class BukkitShopMCPlugin extends JavaPlugin {
    public static WebSocketClient socket;
    public static Config config;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        try {
            config = new Config(new BukkitConfigLoader(this.getConfig()));
        } catch (final EmptyConfigFieldException exception) {
            this.getLogger().severe(exception.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
        }

        String serverURI = "wss://router.shopmc.app/test";
        BukkitShopMCPlugin _this = this;
        socket = new WebSocketClient(URI.create(serverURI))  {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Bukkit.getConsoleSender().sendMessage("WebSocket connection opened");
            }

            @Override
            public void onMessage(String message) {
                Bukkit.getConsoleSender().sendMessage("Received message: " + message);
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(message, JsonObject.class);
                String command = jsonObject.get("command").getAsString();
                Bukkit.getConsoleSender().sendMessage("Command to run: " + command);
                Bukkit.getScheduler().runTask(_this, () -> Bukkit.dispatchCommand(getServer().getConsoleSender(), command));
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Bukkit.getConsoleSender().sendMessage("WebSocket connection closed");
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
            }
        };
        socket.connect();
        socket.setConnectionLostTimeout(0);
    }

    @Override
    public void onDisable() {
        socket.close();
    }
}