package com.github.michaljaz.itemszop;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class WebSocketReconnectTask extends BukkitRunnable {
    JavaPlugin plugin;
    WebSocket socket;
    public WebSocketReconnectTask(JavaPlugin plugin, WebSocket socket) {
        this.plugin = plugin;
        this.socket = socket;
    }
    @Override
    public void run() {
        if (!socket.isOpen()) {
            plugin.getLogger().info("Reconnecting to websocket...");
            socket.reconnect();
        } else {
            cancel();
        }
    }
}
