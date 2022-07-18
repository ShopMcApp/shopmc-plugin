package pl.itemszop;

import net.elytrium.java.commons.mc.serialization.Serializer;
import org.bukkit.plugin.java.JavaPlugin;
import pl.itemszop.commands.itemszop_cmd;

import java.io.File;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Itemszop extends JavaPlugin {
    private static Serializer serializer;
    private static Itemszop instance;
    public static WebSocket socket;
    public static Itemszop getInstance() {
        return instance;
    }
    public String firebaseWebsocketUrl;
    String serverId;
    String secret;
    @Override
    public void onEnable() {
        // Startup message
        getLogger().info("\n _                                         \n" +
                "| |  _                                     \n" +
                "| |_| |_ _____ ____   ___ _____ ___  ____  \n" +
                "| (_   _) ___ |    \\ /___|___  ) _ \\|  _ \\ \n" +
                "| | | |_| ____| | | |___ |/ __/ |_| | |_| |\n" +
                "|_|  \\__)_____)_|_|_(___/(_____)___/|  __/ \n" +
                "                                    |_|  " + this.getDescription().getVersion() + "\n" +
                this.getName() + " by " + this.getDescription().getAuthors() + "\n");
        Settings.IMP.reload(new File(this.getDataFolder(), "config.yml"), Settings.IMP.NO_PERMISSION);
        instance = this;
        registerCommands();
        if (Settings.IMP.KEY == null) { getLogger().warning("Musisz wpisać klucz w pliku konfiguracyjnym, aby plugin mógł działać."); }
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
                if(Settings.IMP.DEBUG) { getLogger().info(firebaseWebsocketUrl); }
            }
        } catch (Exception e) { e.printStackTrace(); } WebSocketConnect(); }
    @Override
    public void onDisable() { socket.close(); }
    private void registerCommands() { new itemszop_cmd().register(getCommand("itemszop")); }
    private static void setSerializer(Serializer serializer) { Itemszop.serializer = serializer; }
    public static Serializer getSerializer() { return serializer; }
    public void reloadPlugin() { Settings.IMP.reload(new File(this.getDataFolder().toPath().toFile().getAbsoluteFile(), "config.yml")); }
    public void WebSocketConnect() {
        try {
            socket = new WebSocket(new URI(firebaseWebsocketUrl));
            socket.connect();
            socket.setConnectionLostTimeout(0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}