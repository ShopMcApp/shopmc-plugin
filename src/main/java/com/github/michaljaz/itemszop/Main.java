package com.github.michaljaz.itemszop;

import com.neovisionaries.ws.client.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Main extends JavaPlugin {
    String serverId;
    String databaseUrl;
    FirebaseSync sync;

    @Override
    public void onEnable() {

        try {
            WebSocket ws = new WebSocketFactory().createSocket("wss://s-euw1b-nss-209.europe-west1.firebasedatabase.app/.ws?v=5&ns=sklepmc-c7516-default-rtdb");
            final boolean[] xd = {true};
            ws.addListener(new WebSocketAdapter() {
                @Override
                public void onTextMessage(WebSocket websocket, String message) throws ParseException {
                    // Received a text message.
                    System.out.println(message);
                    JSONParser parser = new JSONParser();
                    JSONObject json = (JSONObject) parser.parse(message);
                    JSONObject json_data = (JSONObject) parser.parse(json.get("d").toString());
                    String data = (String) json_data.get("d");
                    boolean requiresUpdate = json_data.get("t").equals("r");
                    if(requiresUpdate){
                        System.out.println(data);
                    }
//                    if(xd[0]){
//                        ws.sendText("{\"t\":\"d\",\"d\":{\"r\":2,\"a\":\"q\",\"b\":{\"p\":\"/servers/gitcraft/commands\",\"h\":\"\"}}}");
//                        xd[0] =false;
//                    }
                }
            });
            try
            {
                // Connect to the server and perform an opening handshake.
                // This method blocks until the opening handshake is finished.
                ws.connect();
            }
            catch (OpeningHandshakeException e)
            {
                // A violation against the WebSocket protocol was detected
                // during the opening handshake.
            }
            catch (HostnameUnverifiedException e)
            {
                // The certificate of the peer does not match the expected hostname.
            }
            catch (WebSocketException e)
            {
                // Failed to establish a WebSocket connection.
            }
        } catch (IOException e) {
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
                ChatColor.DARK_BLUE + "Id serwera: " + ChatColor.AQUA + serverId + "\n" +
                ChatColor.BLUE + "Zaktualizować komendy ze sklepu możesz na 2 sposoby:\n" +
                ChatColor.BLUE + "-> wpisz komendę " + ChatColor.AQUA + "/itemszop" + ChatColor.BLUE + "\n"+
                ChatColor.BLUE + "-> wejdź na serwer www " + ChatColor.AQUA + "<ip_tego_serwera>: /itemszop_update\n ";
        String[] split = intro.split("\n");
        for (String s : split) {
            Bukkit.getConsoleSender().sendMessage(s);
        }
        sync = new FirebaseSync(this);

        Objects.requireNonNull(this.getCommand("itemszop_update")).setExecutor(new ItemszopUpdate(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    static class ItemszopUpdate implements CommandExecutor {
        Main plugin;
        public ItemszopUpdate(Main plugin){
            this.plugin = plugin;
        }
        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
            sender.sendMessage(ChatColor.BLUE + "Itemszop commands updated [manually]");
            plugin.sync.syncWithFirebase();
            return true;
        }
    }
}

