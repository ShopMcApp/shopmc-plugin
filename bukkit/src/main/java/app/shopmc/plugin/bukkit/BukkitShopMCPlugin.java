package app.shopmc.plugin.bukkit;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class BukkitShopMCPlugin extends JavaPlugin {
    public static WebSocketClient socket;

    @Override
    public void onEnable() {
        String serverURI = "wss://router.shopmc.app/test";
        Bukkit.getConsoleSender().sendMessage("start");
        BukkitShopMCPlugin _this = this;
        socket = new WebSocketClient(URI.create(serverURI))  {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Bukkit.getConsoleSender().sendMessage("WebSocket connection opened");
            }

            @Override
            public void onMessage(String message) {
                Bukkit.getConsoleSender().sendMessage("Received message: " + message);
                Bukkit.getScheduler().runTask(_this, () -> Bukkit.dispatchCommand(getServer().getConsoleSender(), message));

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

    }
}