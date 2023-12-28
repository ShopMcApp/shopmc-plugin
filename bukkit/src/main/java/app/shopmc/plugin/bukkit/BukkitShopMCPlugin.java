package app.shopmc.plugin.bukkit;

import org.bukkit.plugin.java.JavaPlugin;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class BukkitShopMCPlugin extends JavaPlugin {
    public static WebSocketClient socket;

    @Override
    public void onEnable() {
        String serverURI = "wss://router.shopmc.app";
        getLogger().info("start");
        socket = new WebSocketClient(URI.create(serverURI)) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                getLogger().info("WebSocket connection opened");
            }

            @Override
            public void onMessage(String message) {
                getLogger().info("Received message: " + message);
                // Obsługa otrzymanego komunikatu
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                getLogger().info("WebSocket connection closed");
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