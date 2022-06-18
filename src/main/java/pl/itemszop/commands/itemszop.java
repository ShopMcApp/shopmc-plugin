package pl.itemszop.commands;

import com.sun.tools.javac.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.itemszop.FirebaseSync;
import pl.itemszop.Itemszop;

public class itemszop implements CommandExecutor {
    FirebaseSync sync;
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player p = (Player) sender;
        switch(args[0]) {
            case "update" -> {
                sync.syncWithFirebase();
                p.sendTitle("", "Dane zostały zaktualizowane");
            }
            case "reload" -> {
                if (p.hasPermission("tools.reload")) {
                    try {
                        Itemszop.getInstance().reloadPlugin();
                        Itemszop.getInstance().getLogger().info("Reloaded config & messages.");
                        p.sendMessage("Configuration reloaded!");
                    } catch (Exception e) {
                        p.sendMessage("Configuration reloaded failed. Check console to see errors.");
                        e.printStackTrace();
                    }
                } else {
                    p.sendMessage("Brak uprawnień");
                    return false;
                }
            }
            default -> {
                p.sendMessage("Coś poszło nie tak!");
            }
        }
        return false;
    }
}
