package tk.itemszop.itemszopspigot;

import org.bukkit.scheduler.BukkitRunnable;

import static tk.itemszop.itemszopspigot.Itemszop.socket;


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