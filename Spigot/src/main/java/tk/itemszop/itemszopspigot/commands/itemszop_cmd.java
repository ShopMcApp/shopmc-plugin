package tk.itemszop.itemszopspigot.commands;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import tk.itemszop.itemszopspigot.Settings;

import static tk.itemszop.itemszopspigot.Itemszop.*;

public class itemszop_cmd extends CommandBase {

    @Override
    protected boolean onCommand(Player p, Command cmd, String label, String[] args) {
        if (args.length == 0) {
                p.sendMessage("§aThis server using Itemszop plugin.\n" +
                        "§fDesigned by " + getInstance().getDescription().getAuthors() + "\n" +
                        "\n" +
                        "Available subcommands:\n" +
                        "⁜ /itemszop reload\n" +
                        "⁜ /itemszop reconnect\n" +
                        "⁜ /itemszop test");
        }
        switch(args[0]) {
            case "reload": {
                if (p.hasPermission("itemszop.reload")) {
                    try {
                        getInstance().reloadPlugin();
                        getInstance().getLogger().info("Reloaded config & messages.");
                        p.sendMessage("Configuration reloaded!");
                        return true;
                    } catch (Exception e) {
                        p.sendMessage("Configuration reloaded failed. Check console to see errors.");
                        e.printStackTrace();
                    }
                } else {
                    p.sendMessage(getSerializer().deserialize(Settings.IMP.NO_PERMISSION));
                    return false;
                }
            }
            case "reconnect": {
                if (p.hasPermission("itemszop.reconnect")) {
                    socket.close();
                    p.sendMessage(getSerializer().deserialize(Settings.IMP.CHECK_CONSOLE));
                    getInstance().WebSocketConnect();
                    return true;
                }
            }
            case "test": {
                if (p.hasPermission("itemszop.test")) {
                    if (!socket.isOpen()) {
                        p.sendMessage("Your Websocket is offline");
                    } else {
                        p.sendMessage("Your Websocket is online");
                    }
                    return true;
                }
            }
            default: {
                p.sendMessage(getSerializer().deserialize(Settings.IMP.INVALID_ARGUMENT));
            }
        }
        return false;
    }
}