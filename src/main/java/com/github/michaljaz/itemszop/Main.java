package com.github.michaljaz.itemszop;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info(ChatColor.GREEN + "ENABLED MANHUNT PLUGIN!");
    }

    @Override
    public void onDisable() {
        getLogger().info(ChatColor.RED + "DISABLED MANHUNT PLUGIN");
    }
}