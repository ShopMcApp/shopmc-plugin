package com.github.michaljaz.itemszop;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Set;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        long startTime = System.currentTimeMillis();
        getLogger().info("\n" +
                "§6(_)| |                                           \n" +
                "§6 _ | |_   ___  _ __ ___   ___  ____  ___   _ __  \n" +
                "§6| || __| / _ \\| '_ ` _ \\ / __||_  / / _ \\ | '_ \\ \n" +
                "§6| || |_ |  __/| | | | | |\\__ \\ / / | (_) || |_) |\n" +
                "§6|_| \\__| \\___||_| |_| |_||___//___| \\___/ | .__/ \n §2" +
                "                          " + this.getDescription().getVersion() + "            §6| |    \n" +
                "\n" + "§fDeveloped by " + this.getDescription().getAuthors() + " dla https://github.com/michaljaz/itemszop §a" + "\n§fPlugin został załadowany w §a" + (System.currentTimeMillis() - startTime) + "ms§7.\n§fWykryty silnik: " + Bukkit.getVersion().split("-")[1]);

        FileConfiguration config = this.getConfig();
        config.addDefault("serverId", "");
        config.options().copyDefaults(true);
        saveConfig();
        String serverId = config.getString("serverId");
        getLogger().info("Server ID: " + serverId);

        String dbURL="https://sklepmc-c7516-default-rtdb.europe-west1.firebasedatabase.app";

        getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
            try {
                //run commands
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(dbURL + "/servers/" + serverId + "/commands.json"))
                        .header("Accept", "application/json")
                        .build();
                String response = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(response);
                if(json!=null){
                    Set keys=json.keySet();
                    keys.forEach((e) -> {
                        String command=json.get(e.toString()).toString();
                        getServer().dispatchCommand(getServer().getConsoleSender(),command);
                        //remove commands
                        try {
                            client.send(HttpRequest.newBuilder()
                                    .uri(URI.create(dbURL + "/servers/" + serverId + "/commands/"+e+".json"))
                                    .DELETE()
                                    .build(), HttpResponse.BodyHandlers.ofString()).body();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        System.out.println(command);
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 40L, 40L);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}