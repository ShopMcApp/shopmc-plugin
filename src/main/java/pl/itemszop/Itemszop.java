package pl.itemszop;

import net.elytrium.java.commons.mc.serialization.Serializer;
import net.elytrium.java.commons.mc.serialization.Serializers;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.bukkit.plugin.java.JavaPlugin;
import pl.itemszop.commands.itemszop_cmd;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

public class Itemszop extends JavaPlugin {
    WebSocket ws;
    private static Serializer serializer;
    private static Itemszop instance;
    public static Itemszop getInstance() {
        return instance;
    }
    String firebaseWebsocketUrl;
    String serverId;
    String secret;

    @Override
    public void onEnable() {
        Settings.IMP.reload(new File(this.getDataFolder(), "config.yml"), Settings.IMP.PREFIX);
        instance = this;
        try {
            registerCommands();
            connectToFirebase();
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
            if(index != -1){
                String[] urlList = firebaseWebsocketUrl.split("&");
                firebaseWebsocketUrl = urlList[0] + "&" + urlList[2];
                getLogger().info(firebaseWebsocketUrl);
            }
            // Startup message
            getLogger().info(this.getName() + this.getDescription().getVersion() + " by " + this.getDescription().getAuthors() + "\n Plugin do sklepu serwera - https://github.com/michaljaz/itemszop-plugin");

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
    }
    public void connectToFirebase() {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        try {
                            ws = new WebSocket(instance, new URI(firebaseWebsocketUrl));
                            ws.connect();
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                },
                3000
        );
    }
    @Override
    public void onDisable() {
        ws.close();
    }

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

}