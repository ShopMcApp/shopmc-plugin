package tk.itemszop.itemszopvelocity.commands;

import com.google.common.collect.ImmutableList;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.permission.Tristate;
import java.util.List;
import net.kyori.adventure.text.Component;
import tk.itemszop.itemszopvelocity.Itemszop;
import tk.itemszop.itemszopvelocity.Settings;

import static tk.itemszop.itemszopvelocity.Itemszop.*;

public class ItemszopCommand implements SimpleCommand {

    private final Itemszop plugin;

    public ItemszopCommand(Itemszop plugin) {
        this.plugin = plugin;
    }
    @Override
    public List<String> suggest(Invocation invocation) {
        return ImmutableList.of("reload", "test", "reconnect");
    }

    @Override
    public void execute(Invocation invocation) {
        String[] args = invocation.arguments();
        CommandSource source = invocation.source();

        if (args.length < 1) {
            if (Settings.IMP.SERIALIZER.equals("MINIMESSAGE")){
                source.sendMessage(getSerializer().deserialize("<color:#b6eb17>This server using Itemszop plugin.</color>\n" +
                        "\n" +
                        "Available subcommands:\n" +
                        "⁜ <click:run_command:/itemszop reload>/itemszop reload</click>\n" +
                        "⁜ <click:run_command:/itemszop reconnect>/itemszop reconnect</click>\n" +
                        "⁜ <click:run_command:/itemszop test>/itemszop test</click>"));
            } else {
                source.sendMessage(Component.text("§fThis server using Itemszop plugin.\n" +
                        "\n" +
                        "Available subcommands:\n" +
                        "⁜ /itemszop reload\n" +
                        "⁜ /itemszop reconnect\n" +
                        "⁜ /itemszop test"));
            }
            return;
        }

        switch (args[0]) {
            case "reload":
                try {
                    Itemszop.getInstance().reloadPlugin();
                    Itemszop.getLogger().info("Reloaded config & messages.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "test":
                if (!socket.isOpen()) {
                    source.sendMessage(Component.text("Your Websocket is offline"));
                } else {
                    source.sendMessage(Component.text("Your Websocket is online"));
                }
                break;
            case "reconnect":
                socket.close();
                getInstance().WebSocketConnect();
                break;

            default:
                source.sendMessage(Component.text("Niepoprawny argument!"));
        }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().getPermissionValue("itemszop.admin") == Tristate.TRUE;
    }
}