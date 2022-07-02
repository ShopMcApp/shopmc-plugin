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

public class Main extends JavaPlugin {
    String serverId;
    String databaseUrl;
    String dbID = "sklepmc-c7516-default-rtdb";
    String firebaseWebsocketUrl = "s-euw1b-nss-207.europe-west1.firebasedatabase.app";

    void connectToFirebase() throws IOException, WebSocketException {
        System.out.println("Connecting to " + firebaseWebsocketUrl + "...");
        WebSocket ws = new WebSocketFactory().createSocket("wss://" + firebaseWebsocketUrl + "/.ws?v=5&ns=" + dbID);

        ws.addListener(new WebSocketAdapter() {
            @Override
            public void onTextMessage(WebSocket websocket, String message) throws ParseException, IOException, WebSocketException {
                // Received a text message.
                System.out.println(message);
                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(message);
                JSONObject json_data = (JSONObject) parser.parse(json.get("d").toString());
                String data = json_data.get("d").toString();
                boolean requiresUpdate = json_data.get("t").equals("r");
                if(requiresUpdate){
                    ws.disconnect();
                    firebaseWebsocketUrl = data;
                    connectToFirebase();
                }
            }
        });
        ws.connect();
    }

    @Override
    public void onEnable() {
        try {
            connectToFirebase();
        } catch (IOException | WebSocketException e) {
            e.printStackTrace();
        }
        // config file
        FileConfiguration config = this.getConfig();
        config.addDefault("serverId", "");
        config.addDefault("databaseUrl", "https://sklepmc-c7516-default-rtdb.europe-west1.firebasedatabase.app"); // default database
        config.addDefault("triggerPort", 8001);
        config.options().copyDefaults(true);
        saveConfig();
        serverId = config.getString("serverId");
        databaseUrl = config.getString("databaseUrl");

        // intro
        String intro = "\n" +
                ChatColor.BLUE + "(_)| |                                           \n" +
                ChatColor.BLUE + " _ | |_   ___  _ __ ___   ___  ____  ___   _ __  \n" +
                ChatColor.BLUE + "| || __| / _ \\| '_ ` _ \\ / __||_  / / _ \\ | '_ \\ \n" +
                ChatColor.BLUE + "| || |_ |  __/| | | | | |\\__ \\ / / | (_) || |_) |\n" +
                ChatColor.BLUE + "|_| \\__| \\___||_| |_| |_||___//___| \\___/ | .__/ \n" +
                "                          " + ChatColor.AQUA + this.getDescription().getVersion() + "            " + ChatColor.BLUE + "| |    \n" +
                "\n" + ChatColor.DARK_BLUE + "Plugin do sklepu serwera - https://github.com/michaljaz/itemszop-plugin \n" +
                ChatColor.DARK_BLUE + "Autorzy: " + this.getDescription().getAuthors() + "\n" +
                ChatColor.DARK_BLUE + "Id serwera: " + ChatColor.AQUA + serverId + "\n";
        String[] split = intro.split("\n");
        for (String s : split) {
            Bukkit.getConsoleSender().sendMessage(s);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

