package pl.itemszop;

import org.bukkit.scheduler.BukkitRunnable;

import static pl.itemszop.Itemszop.socket;

public class WebSocketReconnectTask extends BukkitRunnable {
    private static final Itemszop task = Itemszop.getInstance();
    @Override
    public void run() {
        if (!socket.isOpen()) {
            if (Settings.IMP.DEBUG == true) { task.getLogger().info("Reconnecting to websocket..."); }
            socket.reconnect();
        } else {
            cancel();
        }
    }
}