package com.github.michaljaz.itemszop;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ItemszopUpdate implements CommandExecutor {
    Main plugin;
    public ItemszopUpdate(Main plugin){
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        sender.sendMessage("Itemszop commands updated");
        plugin.sync.syncWithFirebase();
        return true;
    }
}
