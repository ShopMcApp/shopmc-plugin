package pl.itemszop.commands;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import pl.itemszop.Settings;

import static pl.itemszop.Itemszop.*;

public class itemszop_cmd extends CommandBase {

    @Override
    protected boolean onCommand(Player p, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            if (Settings.IMP.SERIALIZER == "MINIMESSAGE"){
                p.sendMessage(getSerializer().deserialize("<color:#b6eb17>This server using Itemszop plugin.</color>\n" +
                        "Designed by " + getInstance().getDescription().getAuthors() + "\n" +
                        "\n" +
                        "Available subcommands:\n" +
                        "⁜ <click:run_command:/itemszop reload>/itemszop reload</click>\n" +
                        "⁜ <click:run_command:/itemszop reconnect>/itemszop reconnect</click>\n" +
                        "⁜ <click:run_command:/itemszop test>/itemszop test</click>"));
            } else {
                p.sendMessage("§aThis server using Itemszop plugin.\n" +
                        "§fDesigned by " + getInstance().getDescription().getAuthors() + "\n" +
                        "\n" +
                        "Available subcommands:\n" +
                        "⁜ /itemszop reload\n" +
                        "⁜ /itemszop reconnect\n" +
                        "⁜ /itemszop test");
            }
            return false;
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
                    getInstance().WebSocketConnect();
                    p.sendMessage(getSerializer().deserialize(Settings.IMP.CHECK_CONSOLE));
                    return true;
                }
            }
            case "test": {
                if (p.hasPermission("itemszop.test")) {
                    if (!socket.isOpen()) {
                        p.sendMessage("Nie jesteś połączony z WebSocketem");
                    } else {
                        p.sendMessage("Jesteś połączony z WebSocketem");
                    }
                }
            }
            default: {
                p.sendMessage(getSerializer().deserialize(Settings.IMP.INVALID_ARGUMENT));
            }
        }
        return false;
    }
}