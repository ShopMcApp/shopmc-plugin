package tk.itemszop.itemszopspigot;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import tk.itemszop.itemszopspigot.Settings;

import java.net.URI;
import java.util.Objects;

import static org.bukkit.Bukkit.getServer;

public class WebSocket extends WebSocketClient {
    private static final Itemszop plugin = Itemszop.getInstance();
    public WebSocket(URI serverUri) {
        super(serverUri);
    }

    void execute(JsonObject data, String path){
        // useful params
        int amount = data.get("amount").getAsInt();
        String nick = data.get("nick").getAsString();
        JsonObject service = data.get("service").getAsJsonObject();
        //execute commands on server
        String commands = service.get("commands").getAsString().replace("[nick]", nick).replace("[amount]", Integer.toString(amount));
        for (String command : commands.split("\n")) {
            Bukkit.getScheduler().runTask(plugin, () -> Bukkit.dispatchCommand(getServer().getConsoleSender(), command));
        }
        // remove commands from firebase
        send("{\"t\":\"d\",\"d\":{\"r\":1,\"a\":\"p\",\"b\":{\"p\":\"/servers/" + plugin.serverId + "/commands/" + plugin.secret + "/" + path + "\",\"d\":null}}}");
    }
    @Override
    public void onOpen(ServerHandshake handshakedata) {
        if (Settings.IMP.DEBUG) { plugin.getLogger().info("Connected to server " + plugin.serverId); }
        // send listen request to command reference
        send("{\"t\":\"d\",\"d\":{\"r\":1,\"a\":\"q\",\"b\":{\"p\":\"/servers/" + plugin.serverId + "/commands/" + plugin.secret + "\",\"h\":\"\"}}}");
    }
    @Override
    public void onMessage(String message) {
        JsonObject json = new JsonParser().parse(message).getAsJsonObject();
        // check if response type is data
        if(json.get("t").getAsString().equals("d")){
            JsonElement _data = json.get("d").getAsJsonObject().get("b").getAsJsonObject().get("d");
            JsonElement _path = json.get("d").getAsJsonObject().get("b").getAsJsonObject().get("p");
            //check if data inside response is json
            if(_data.isJsonObject()){
                JsonObject data = _data.getAsJsonObject();
                if(data.get("nick") != null){
                    // single command response
                    String[] pathArray = _path.getAsString().split("/");
                    String path = pathArray[pathArray.length-1];
                    execute(data, path);
                } else {
                    // multi command response
                    for(Object entry : data.entrySet()){
                        String path = entry.toString().split("=")[0];
                        JsonObject data2 = data.get(path).getAsJsonObject();
                        execute(data2, path);
                    }
                }
            }
        } else if(json.get("t").getAsString().equals("c") && Objects.equals(json.get("d").getAsJsonObject().get("t").getAsString(), "r")){
            // websocket url needs update
            String newUrl = "wss://" + json.get("d").getAsJsonObject().get("d").getAsString() + "/" + plugin.firebaseWebsocketUrl.split("/")[3];
            Itemszop.socket.uri = URI.create(newUrl);
            if (Settings.IMP.DEBUG) { plugin.getLogger().info("Websocket address changed: " + newUrl); }
        }
    }
    @Override
    public void onClose(int code, String reason, boolean remote) {
        if (Settings.IMP.DEBUG) {
            plugin.getLogger().info("Websocket connection closed: " + reason);
        }
        if (code != 1000) {
            new WebSocketReconnectTask().runTaskTimer(plugin, 0L, (Settings.IMP.CHECK_TIME * 20 ));
        }
    }
    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }
}