package pl.itemszop;

import net.elytrium.java.commons.mc.serialization.Serializer;
import net.elytrium.java.commons.mc.serialization.Serializers;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.bukkit.plugin.java.JavaPlugin;
import pl.itemszop.commands.itemszop_cmd;

import java.io.File;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

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
        getLogger().info(" _                                         \n" +
                "| |  _                                     \n" +
                "| |_| |_ _____ ____   ___ _____ ___  ____  \n" +
                "| (_   _) ___ |    \\ /___|___  ) _ \\|  _ \\ \n" +
                "| | | |_| ____| | | |___ |/ __/ |_| | |_| |\n" +
                "|_|  \\__)_____)_|_|_(___/(_____)___/|  __/ \n" +
                "                                    |_|  " + this.getDescription().getVersion() + "\n" +
                this.getName() + " by " + this.getDescription().getAuthors() + "\n" +
                "Id serwera " + serverId);
        Settings.IMP.reload(new File(this.getDataFolder(), "config.yml"), Settings.IMP.PREFIX);
        instance = this;
        try {
            registerCommands();
            // decode config key
            byte[] decoded = Base64.getDecoder().decode(Settings.IMP.KEY);
            String decodedStr = new String(decoded, StandardCharsets.UTF_8);
            String[] stringList = decodedStr.split("@");
            secret = stringList[0];
            firebaseWebsocketUrl = stringList[1];
            serverId = stringList[2];
            if (Settings.IMP.KEY == null) {
                getLogger().warning("Musisz wpisać klucz w pliku konfiguracyjnym, aby plugin mógł działać.");
            }
            // cut websocket url param
            int index = firebaseWebsocketUrl.indexOf("&s=");
            if (index != -1) {
                String[] urlList = firebaseWebsocketUrl.split("&");
                firebaseWebsocketUrl = urlList[0] + "&" + urlList[2];
                getLogger().info(firebaseWebsocketUrl);
            }
            ComponentSerializer<Component, Component, String> serializer = Serializers.valueOf(Settings.IMP.SERIALIZER).getSerializer();
            if (serializer == null) {
                this.getLogger().info("The specified serializer could not be founded, using default. (LEGACY_AMPERSAND)");
                setSerializer(new Serializer(Objects.requireNonNull(Serializers.LEGACY_AMPERSAND.getSerializer())));
            } else {
                setSerializer(new Serializer(serializer));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        WebSocketConnect();
    }

    @Override
    public void onDisable() { socket.close(); }

    private void registerCommands() {
        new itemszop_cmd().register(getCommand("itemszop"));
    }

    public void reloadPlugin() {
        Settings.IMP.reload(new File(this.getDataFolder().toPath().toFile().getAbsoluteFile(), "config.yml"));
    }
    private static void setSerializer(Serializer serializer) {
        Itemszop.serializer = serializer;
    }
    public static Serializer getSerializer() {
        return serializer;
    }

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