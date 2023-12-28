package app.shopmc.plugin.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitShopMCPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage("WORKING!");
    }

    @Override
    public void onDisable() {

    }
}