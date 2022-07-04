package pl.itemszop;

import com.neovisionaries.ws.client.*;
import com.sun.tools.javac.Main;
import net.elytrium.java.commons.mc.serialization.Serializer;
import net.elytrium.java.commons.mc.serialization.Serializers;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Base64;
import java.util.Objects;

import static org.bukkit.Bukkit.getScheduler;

public final class Itemszop extends JavaPlugin {
    private static Itemszop instance;
    private static Serializer serializer;
    WebSocket ws;
    Main plugin;
    public static Itemszop getInstance() {
        return instance;
    }

    String key = Settings.IMP.KEY;
    String firebaseWebsocketUrl = Settings.IMP.FIREBASEWEBSOCKETURL;
    String serverId = Settings.IMP.SERVERID;
    String secret = Settings.IMP.SECRET;

    @Override
    public void onEnable() {
        Settings.IMP.reload(new File(this.getDataFolder(), "config.yml"), Settings.IMP.PREFIX);

        // decode config key
        byte[] decoded = Base64.getDecoder().decode(Settings.IMP.KEY);
        String decodedStr = new String(decoded, StandardCharsets.UTF_8);
        String[] stringList = decodedStr.split("@");
        secret = stringList[0];
        firebaseWebsocketUrl = stringList[1];
        serverId = stringList[2];

        // cut websocket url param
        int index = firebaseWebsocketUrl.indexOf("&s=");
        if(index != -1){
            String[] urlList = firebaseWebsocketUrl.split("&");
            firebaseWebsocketUrl = urlList[0] + "&" + urlList[2];
            getLogger().info(firebaseWebsocketUrl);
        }
        getLogger().info(secret);
        getLogger().info(firebaseWebsocketUrl);
        getLogger().info(serverId);

        // intro
        getLogger().info(this.getName() + this.getDescription().getVersion() + " by " + this.getDescription().getAuthors() + "\n Plugin do sklepu serwera - https://github.com/michaljaz/itemszop-plugin");

        //connect to firebase
        try {
            connectToFirebase();
        } catch (IOException | WebSocketException e) {
            e.printStackTrace();
        }
    }

    void connectToFirebase() throws IOException, WebSocketException {
        getLogger().info("Connecting to " + firebaseWebsocketUrl + "...");
        WebSocket ws = new WebSocketFactory().createSocket(firebaseWebsocketUrl);

        ws.addListener(new WebSocketAdapter() {
            @Override
            public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                super.onConnected(websocket, headers);
                getLogger().info("Połączono!");
                ws.sendText("{\"t\":\"d\",\"d\":{\"r\":1,\"a\":\"q\",\"b\":{\"p\":\"/servers/" + serverId +  "/commands/" + secret + "\",\"h\":\"\"}}}");
            }

            @Override
            public void onTextMessage(WebSocket websocket, String message) throws ParseException, org.json.simple.parser.ParseException {
                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(message);
                JSONObject json_data = (JSONObject) parser.parse(json.get("d").toString());
                json_data = (JSONObject) parser.parse(json_data.get("b").toString());
                if (json_data.get("d") != null){
                    json_data = (JSONObject) parser.parse(json_data.get("d").toString());
                    for (Object commandId : json_data.keySet()) {
                        String command = json_data.get(commandId).toString();
                        getLogger().info(command);
                        ws.sendText("{ \"t\": \"d\", \"d\": { \"r\": 1, \"a\": \"p\", \"b\": { \"p\": \"/servers/" + serverId + "/commands/" + secret + "/" + commandId + "\", \"d\": null } } }");
                        getScheduler().runTask((Plugin) plugin, () -> getServer().dispatchCommand(getServer().getConsoleSender(), command));
                    }
                }else{
                    Bukkit.getLogger().warning("Command stack is empty!");
                }

            }
        });
        ws.connect();

        ComponentSerializer<Component, Component, String> serializer = Serializers.valueOf(Settings.IMP.SERIALIZER).getSerializer();
        if (serializer == null) {
            this.getLogger().info("The specified serializer could not be founded, using default. (LEGACY_AMPERSAND)");
            setSerializer(new Serializer(Objects.requireNonNull(Serializers.LEGACY_AMPERSAND.getSerializer())));
        } else {
            setSerializer(new Serializer(serializer));
        }

    }
    @Override
    public void onDisable() {
        try {
            ws.disconnect();
            getLogger().warning("Disconnected!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void reloadPlugin() {
        Settings.IMP.reload(new File(this.getDataFolder().toPath().toFile().getAbsoluteFile(), "config.yml"));
    }
    private static void setSerializer(Serializer serializer) {
        Itemszop.serializer = serializer;
    }
    public static Serializer getSerializer() {
        return serializer;
    }

}