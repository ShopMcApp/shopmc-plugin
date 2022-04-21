package com.github.michaljaz.itemszop;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Set;

public class Main extends JavaPlugin {
    private String serverId;
    private String databaseUrl;
    private final HttpClient client = HttpClient.newHttpClient();
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

        //config file
        FileConfiguration config = this.getConfig();
        config.addDefault("serverId", "");
        config.addDefault("databaseUrl", "https://sklepmc-c7516-default-rtdb.europe-west1.firebasedatabase.app");
        config.options().copyDefaults(true);
        saveConfig();
        serverId = config.getString("serverId");
        databaseUrl = config.getString("databaseUrl");

        getLogger().info("Server ID: " + serverId);

        //sync loop
        getServer().getScheduler().scheduleSyncRepeatingTask(this, this::syncWithFirebase, 40L, 40L);
    }

    private void syncWithFirebase(){
        try {
            JSONObject commands = getCommands();
            if(commands!=null){
                Set<?> keys = commands.keySet();
                keys.forEach((key) -> {
                    String commandId = key.toString();
                    String command = commands.get(key.toString()).toString();
                    System.out.println(command);

                    getServer().dispatchCommand(getServer().getConsoleSender(), command);
                    deleteCommand(commandId);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JSONObject getCommands() throws Exception{
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(databaseUrl + "/servers/" + serverId + "/commands.json"))
                .header("Accept", "application/json")
                .build();
        String response = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(response);
    }

    private void deleteCommand(String commandId){
        try{
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(databaseUrl + "/servers/" + serverId + "/commands/" + commandId + ".json"))
                    .DELETE()
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}