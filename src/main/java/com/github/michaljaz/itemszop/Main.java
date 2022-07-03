package com.github.michaljaz.itemszop;

import com.neovisionaries.ws.client.*;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Base64;

public class Main extends JavaPlugin {
    String key;
    String firebaseWebsocketUrl;
    String serverId;
    String secret;

    @Override
    public void onEnable() {

        // config file
        FileConfiguration config = this.getConfig();
        config.addDefault("key", "");
        config.options().copyDefaults(true);
        saveConfig();
        key = config.getString("key");

        //decode config key
        byte[] decoded = Base64.getDecoder().decode(key);
        String decodedStr = new String(decoded, StandardCharsets.UTF_8);
        String[] stringList = decodedStr.split("@");
        secret = stringList[0];
        firebaseWebsocketUrl = stringList[1];
        serverId = stringList[2];
        System.out.println(secret);
        System.out.println(firebaseWebsocketUrl);
        System.out.println(serverId);

        // intro
        String intro = "\n" +
                ChatColor.BLUE + "(_)| |                                           \n" +
                ChatColor.BLUE + " _ | |_   ___  _ __ ___   ___  ____  ___   _ __  \n" +
                ChatColor.BLUE + "| || __| / _ \\| '_ ` _ \\ / __||_  / / _ \\ | '_ \\ \n" +
                ChatColor.BLUE + "| || |_ |  __/| | | | | |\\__ \\ / / | (_) || |_) |\n" +
                ChatColor.BLUE + "|_| \\__| \\___||_| |_| |_||___//___| \\___/ | .__/ \n" +
                "                          " + ChatColor.AQUA + this.getDescription().getVersion() + "            " + ChatColor.BLUE + " | |    \n" +
                "\n" + ChatColor.DARK_BLUE + "Plugin do sklepu serwera - https://github.com/michaljaz/itemszop-plugin \n" +
                ChatColor.DARK_BLUE + "Autorzy: " + this.getDescription().getAuthors() + "\n" +
                ChatColor.DARK_BLUE + "Id serwera: " + ChatColor.AQUA + serverId + "\n";
        String[] split = intro.split("\n");
        for (String s : split) {
            Bukkit.getConsoleSender().sendMessage(s);
        }

        //connect to firebase
        try {
            connectToFirebase();
        } catch (IOException | WebSocketException e) {
            e.printStackTrace();
        }
    }

    void connectToFirebase() throws IOException, WebSocketException {
        System.out.println("Connecting to " + firebaseWebsocketUrl + "...");
        WebSocket ws = new WebSocketFactory().createSocket(firebaseWebsocketUrl);

        ws.addListener(new WebSocketAdapter() {
            @Override
            public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                super.onConnected(websocket, headers);
                System.out.println("connected!");
            }

            @Override
            public void onTextMessage(WebSocket websocket, String message) throws ParseException, IOException, WebSocketException {
                // Received a text message.
                System.out.println(message);
                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(message);
                JSONObject json_data = (JSONObject) parser.parse(json.get("d").toString());
                String data = json_data.get("d").toString();
            }
        });
        ws.connect();
        ws.sendText("{\"t\":\"d\",\"d\":{\"r\":2,\"a\":\"q\",\"b\":{\"p\":\"/servers/gitcraft/commands\",\"h\":\"\"}}}");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

