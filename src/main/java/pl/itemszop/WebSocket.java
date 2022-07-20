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

    void executeCommand(String command, String commandId){
        send("{\"t\":\"d\",\"d\":{\"r\":1,\"a\":\"p\",\"b\":{\"p\":\"/servers/" + plugin.serverId + "/commands/" + plugin.secret + "/" + commandId + "\",\"d\":null}}}");
        Bukkit.getScheduler().runTask(plugin, () -> Bukkit.dispatchCommand( getServer().getConsoleSender(), command ));
    }
    @Override
    public void onOpen(ServerHandshake handshakedata) {
        send("{\"t\":\"d\",\"d\":{\"r\":1,\"a\":\"q\",\"b\":{\"p\":\"/servers/" + plugin.serverId + "/commands/" + plugin.secret + "\",\"h\":\"\"}}}");
        if (Settings.IMP.DEBUG) { plugin.getLogger().info("Połączono z " + plugin.serverId); }
    }
    @Override
    public void onMessage(String message) {
        System.out.println(message);
        JsonObject json = new JsonParser().parse(message).getAsJsonObject();
        if(json.get("t").getAsString().equals("d")){
            JsonElement data = json.get("d").getAsJsonObject().get("b").getAsJsonObject().get("d");
            JsonElement path = json.get("d").getAsJsonObject().get("b").getAsJsonObject().get("p");
            // received via SET method
            if(data.isJsonObject()){
                for(Object entry : data.getAsJsonObject().entrySet()){
                    String commandId = entry.toString().split("=")[0];
                    String command = data.getAsJsonObject().get(commandId).getAsString();
                    executeCommand(command, commandId);
                }
            }
            // received via PUSH method
            if(data.isJsonPrimitive() && path != null){
                String[] pathArray = path.getAsString().split("/");
                String commandId = pathArray[pathArray.length-1];
                String command = data.getAsString();
                executeCommand(command, commandId);
            }
        }
    }
    @Override
    public void onClose(int code, String reason, boolean remote) {
        if (Settings.IMP.DEBUG) { plugin.getLogger().info("Rozłączono z WebSocketem: " + reason); }
        if (code != 1000) {
            new WebSocketReconnectTask().runTaskTimer(plugin, 0L, (Settings.IMP.CHECK_TIME * 20 ));
        }
    }
    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }
}