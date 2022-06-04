package com.github.michaljaz.itemszop;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    String serverId;
    String databaseUrl;
    FirebaseSync sync;

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
        sync = new FirebaseSync(this);
        this.getCommand("itemszop_update").setExecutor(new ItemszopUpdate(this));
    }



    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}