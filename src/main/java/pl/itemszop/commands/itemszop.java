package pl.itemszop.commands;

import com.sun.tools.javac.Main;
import org.bukkit.block.data.type.Fire;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.itemszop.FirebaseSync;
import pl.itemszop.Itemszop;

public class itemszop implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        Player p = (Player) sender;

        FirebaseSync sync = new FirebaseSync();

        if (args.length == 0) {
            p.sendMessage("Serwer działa z pluginem Itemszop.");
            return true;
        }

        if (p.hasPermission("itemszop.command")) {
            switch (args[0]) {
                case "update" -> {
                    try {
                        sync.syncWithFirebase();
                        p.sendTitle("", "Dane zostały zaktualizowane");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                case "reload" -> {
                    if (p.hasPermission("itemszop.reload")) {
                        try {
                            Itemszop.getInstance().reloadPlugin();
                            Itemszop.getInstance().getLogger().info("Reloaded config & messages.");
                            p.sendMessage("Configuration reloaded!");
                        } catch (Exception e) {
                            p.sendMessage("Configuration reloaded failed. Check console to see errors.");
                            e.printStackTrace();
                        }
                    } else {
                        p.sendMessage("Nie masz uprawnień do wykonania tego polecenia");
                        return false;
                    }
                }
                default -> {
                    p.sendMessage("Wpisałeś niepoprawny argument!");
                }
            }
            return false;
        }
        return false;
    }
}
