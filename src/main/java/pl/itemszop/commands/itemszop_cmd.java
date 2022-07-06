package pl.itemszop.commands;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import pl.itemszop.Itemszop;
import pl.itemszop.Settings;
import pl.itemszop.WebSocket;

import java.net.URI;
import java.net.URISyntaxException;

public class itemszop_cmd extends CommandBase {

    private static final Itemszop plugin = Itemszop.getInstance();
    WebSocket ws;

    @Override
    protected boolean onCommand(Player p, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            p.sendMessage(Itemszop.getSerializer().deserialize("<color:#b6eb17>This server using Itemszop plugin.</color>\n" +
                    "Designed by " + Itemszop.getInstance().getDescription().getAuthors() + "\n" +
                    "\n" +
                    "Avaliable subcommands:\n" +
                    "⁜ <click:run_command:/itemszop reload>/itemszop reload</click>\n" +
                    "⁜ <click:run_command:/itemszop test>/itemszop test</click>"));
            return false;
        }
        switch(args[0]) {
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
                    p.sendMessage(Itemszop.getInstance().getSerializer().deserialize(Settings.IMP.NO_PERMISSION));
                    return false;
                }
            }
            case "test" -> {
                try {
                    try {
                        ws = new WebSocket(plugin, new URI(Itemszop.getInstance().firebaseWebsocketUrl));
                        ws.setConnectionLostTimeout(0);
                        ws.connect();
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    p.sendMessage(Itemszop.getSerializer().deserialize(Settings.IMP.CHECK_CONSOLE));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}