package com.github.michaljaz.itemszop;

import org.bukkit.scheduler.BukkitRunnable;

import static com.github.michaljaz.itemszop.Itemszop.socket;

public class WebSocketReconnectTask extends BukkitRunnable {
    private static final Itemszop task = Itemszop.getInstance();
    @Override
    public void run() {
        if (!socket.isOpen()) {
            if (Settings.IMP.DEBUG) { task.getLogger().info("Reconnecting to websocket..."); }
            socket.reconnect();
        } else {
            cancel();
        }
    }
}