package pl.itemszop;

import org.bukkit.Bukkit;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.URI;
import java.util.Objects;

import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.Bukkit.getServer;

public class WebSocket extends WebSocketClient {

    private static Itemszop plugin = Itemszop.getInstance();

    public WebSocket(Itemszop plugin, URI serverUri) {
        super(serverUri);
        WebSocket.plugin = plugin;
    }


    @Override
    public void onOpen(ServerHandshake handshakedata) {
        send("{\"t\":\"d\",\"d\":{\"r\":1,\"a\":\"q\",\"b\":{\"p\":\"/servers/" + plugin.serverId + "/commands/" + plugin.secret + "\",\"h\":\"\"}}}");
        getLogger().info("§aPołączono" + handshakedata);
    }

    @Override
    public void onMessage(String message) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(message);
            if (Objects.equals(json.get("t").toString(), "d")) {
                JSONObject json_data = (JSONObject) parser.parse(json.get("d").toString());
                json_data = (JSONObject) parser.parse(json_data.get("b").toString());
                if (json_data.get("d") != null && json_data.get("d").toString().length() > 0) {
                    json_data = (JSONObject) parser.parse(json_data.get("d").toString());
                    for (Object commandId : json_data.keySet()) {
                        String command = json_data.get(commandId).toString();
                        send("{\"t\":\"d\",\"d\":{\"r\":1,\"a\":\"p\",\"b\":{\"p\":\"/servers/" + plugin.serverId + "/commands/" + plugin.secret + "/" + commandId + "\",\"d\":null}}}");
                        Bukkit.getScheduler().runTask(plugin, () -> Bukkit.dispatchCommand(getServer().getConsoleSender(), command));

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        getLogger().info("§cRozłączono" + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }
}