package tk.itemszop.itemszopbungeecord;

import static tk.itemszop.itemszopbungeecord.Itemszop.socket;


public class WebSocketReconnectTask implements Runnable {
    private static final Itemszop task = Itemszop.getInstance();
    public static boolean disabled = true;
    @Override
    public void run() {
        if (!disabled && !socket.isOpen()) {
            if (Settings.IMP.DEBUG) { task.getLogger().info("Reconnecting to websocket..."); }
            socket.reconnect();
        }
    }
}