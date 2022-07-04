package pl.itemszop.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import pl.itemszop.Itemszop;
import pl.itemszop.Settings;

public class itemszop_cmd extends CommandBase {

    Itemszop plugin;

    @Override
    protected boolean onCommand(Player p, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            p.sendMessage(Itemszop.getSerializer().deserialize("<color:#b6eb17>This server using Itemszop plugin.</color>\n" +
                    "Designed by " + plugin.getDescription().getAuthors() + " \n" +
                    "\n" +
                    "Avaliable subcommands:\n" +
                    "⁜ <click:run_command:/itemszop reload>/reload</click>"));
            return false;
        }
        switch(args[0]) {
            case "reload" -> {
                if (p.hasPermission("itemszop.reload")) {
                    try {
                        plugin.reloadPlugin();
                        plugin.getLogger().info("Reloaded config & messages.");
                        p.sendMessage("Configuration reloaded!");
                    } catch (Exception e) {
                        p.sendMessage("Configuration reloaded failed. Check console to see errors.");
                        e.printStackTrace();
                    }
                } else {
                    p.sendMessage(plugin.getSerializer().deserialize(Settings.IMP.NO_PERMISSION));
                    return false;
                }
            }
        }
        return false;
    }
}