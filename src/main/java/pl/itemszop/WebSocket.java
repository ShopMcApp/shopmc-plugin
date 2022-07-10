package pl.itemszop;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

import static org.bukkit.Bukkit.getServer;

public class WebSocket extends WebSocketClient {
    private static final Itemszop plugin = Itemszop.getInstance();
    public WebSocket(URI serverUri) {
        super(serverUri);
    }
    @Override
    public void onOpen(ServerHandshake handshakedata) {
        send("{\"t\":\"d\",\"d\":{\"r\":1,\"a\":\"q\",\"b\":{\"p\":\"/servers/" + plugin.serverId + "/commands/" + plugin.secret + "\",\"h\":\"\"}}}");
        if (Settings.IMP.DEBUG == true) { plugin.getLogger().info("Połączono z " + plugin.serverId); }
    }
    @Override
    public void onMessage(String message) {
        JsonObject json = new JsonParser().parse(message).getAsJsonObject();
        if (json.get("t").getAsString().equals("d")) {
            JsonElement data = json.get("d").getAsJsonObject().get("b").getAsJsonObject().get("d");
            if (data.isJsonObject()) {
                for (Object entry : data.getAsJsonObject().entrySet()) {
                    send("{\"t\":\"d\",\"d\":{\"r\":1,\"a\":\"p\",\"b\":{\"p\":\"/servers/" + plugin.serverId + "/commands/" + plugin.secret + "/" + entry.toString().split("=")[0] + "\",\"d\":null}}}");
                    Bukkit.getScheduler().runTask(plugin, () -> Bukkit.dispatchCommand(getServer().getConsoleSender(), data.getAsJsonObject().get(entry.toString().split("=")[0]).getAsString()));
                }
            }
        }
    }
    @Override
    public void onClose(int code, String reason, boolean remote) {
        if (Settings.IMP.DEBUG == true) { plugin.getLogger().info("Rozłączono z WebSocketem: " + reason); }
        if (code != 1000) {
            new WebSocketReconnectTask().runTaskTimer(plugin, 0L, (Settings.IMP.CHECK_TIME * 20 ));
        }
    }
    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }
}