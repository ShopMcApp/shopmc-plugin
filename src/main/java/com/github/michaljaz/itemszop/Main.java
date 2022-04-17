package com.github.michaljaz.itemszop;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        long startTime = System.currentTimeMillis();
        Bukkit.getConsoleSender().sendMessage("\n" +
                "§6(_)| |                                           \n" +
                "§6 _ | |_   ___  _ __ ___   ___  ____  ___   _ __  \n" +
                "§6| || __| / _ \\| '_ ` _ \\ / __||_  / / _ \\ | '_ \\ \n" +
                "§6| || |_ |  __/| | | | | |\\__ \\ / / | (_) || |_) |\n" +
                "§6|_| \\__| \\___||_| |_| |_||___//___| \\___/ | .__/ \n §2" +
                "                          " + this.getDescription().getVersion() + "            §6| |    \n" +
                "\n" + "§fDeveloped by " + this.getDescription().getAuthors() + " dla https://github.com/michaljaz/itemszop §a" + "\n§fPlugin został załadowany w §a" + (System.currentTimeMillis() - startTime) + "ms§7.\n§fWykryty silnik: " + Bukkit.getVersion().split("-")[1]);
        try {
            this.getRequest("https://sklepmc-c7516-default-rtdb.europe-west1.firebasedatabase.app/shops.json");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getRequest(String uri) throws Exception{
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("Accept", "application/json")
                .build();

        String response = client.send(request, HttpResponse.BodyHandlers.ofString()).body();

        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(response);
        JSONObject gitcraft = (JSONObject) json.get("gitcraft");
        String name = gitcraft.get("name").toString();
        System.out.println(name);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}