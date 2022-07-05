package com.github.michaljaz.itemszop;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Main extends JavaPlugin {
    String firebaseWebsocketUrl;
    String serverId;
    String secret;
    Main plugin;
    WebSocket ws;

    @Override
    public void onEnable() {
        plugin = this;

        // config file
        FileConfiguration config = this.getConfig();
        config.addDefault("key", "");
        config.options().copyDefaults(true);
        saveConfig();
        String key = config.getString("key");

        // decode config key
        byte[] decoded = Base64.getDecoder().decode(key);
        String decodedStr = new String(decoded, StandardCharsets.UTF_8);
        String[] stringList = decodedStr.split("@");
        secret = stringList[0];
        firebaseWebsocketUrl = stringList[1];
        serverId = stringList[2];

        // cut websocket url param
        int index = firebaseWebsocketUrl.indexOf("&s=");
        if (index != -1) {
            String[] urlList = firebaseWebsocketUrl.split("&");
            firebaseWebsocketUrl = urlList[0] + "&" + urlList[2];
            System.out.println(firebaseWebsocketUrl);
        }
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
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        try {
                            ws = new WebSocket(plugin, new URI(firebaseWebsocketUrl));
                            ws.setConnectionLostTimeout( 0 );
                            ws.connect();
                            setupTasks();
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                },
                3000
        );

    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
        ws.close();
    }

    private void setupTasks () {
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (ws.isOpen()) {
                ws.send("");
            }
        }, 0L, (45 * 20));
    }
}

