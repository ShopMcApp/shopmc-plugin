package tk.itemszop.itemszopbungeecord;

import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

public final class Itemszop extends Plugin {
    private static Itemszop instance;
    public static WebSocket socket;
    public static String firebaseWebsocketUrl;
    String serverId;
    String secret;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        getLogger().info("\n _                                         \n" +
                "| |  _                                     \n" +
                "| |_| |_ _____ ____   ___ _____ ___  ____  \n" +
                "| (_   _) ___ |    \\ /___|___  ) _ \\|  _ \\ \n" +
                "| | | |_| ____| | | |___ |/ __/ |_| | |_| |\n" +
                "|_|  \\__)_____)_|_|_(___/(_____)___/|  __/ \n" +
                "                                    |_|  " + this.getDescription().getVersion() + "\n" +
                this.getDescription().getName() + " by " + this.getDescription().getAuthor() + "\n");
        Settings.IMP.reload(new File(this.getDataFolder(), "config.yml"));

        if (Settings.IMP.KEY == null || Settings.IMP.KEY.equals("")) {
            getLogger().warning("You have to enter the key in the config file for the plugin to work.");
        } else {
            try {
                // decode config key
                byte[] decoded = Base64.getDecoder().decode(Settings.IMP.KEY);
                String decodedStr = new String(decoded, StandardCharsets.UTF_8);
                String[] stringList = decodedStr.split("@");
                secret = stringList[0];
                firebaseWebsocketUrl = stringList[1];
                serverId = stringList[2];
                // cut websocket url param
                int index = firebaseWebsocketUrl.indexOf("&s=");
                if (index != -1) {
                    String[] urlList = firebaseWebsocketUrl.split("&");
                    firebaseWebsocketUrl = urlList[0] + "&" + urlList[2];
                    if (Settings.IMP.DEBUG) {
                        getLogger().info(firebaseWebsocketUrl);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            WebSocketConnect();
            getProxy().getScheduler().schedule(this, new WebSocketReconnectTask(), 1, Settings.IMP.CHECK_TIME, TimeUnit.SECONDS);
        }
        if (Settings.IMP.DEBUG) { getLogger().info("Key: " + secret + "\nWebsocket URL: " + firebaseWebsocketUrl + "\nServer ID: " + serverId); }
        }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (socket != null) {
            socket.close();
        }
    }
    private static void WebSocketConnect() {
        try {
            socket = new WebSocket(new URI(firebaseWebsocketUrl));
            socket.connect();
            socket.setConnectionLostTimeout(0);
            WebSocketReconnectTask.disabled = false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Itemszop getInstance() {
        return instance;
    }
}
