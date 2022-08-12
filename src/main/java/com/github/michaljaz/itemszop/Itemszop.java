package com.github.michaljaz.itemszop;

import net.elytrium.java.commons.mc.serialization.Serializer;
import net.elytrium.java.commons.mc.serialization.Serializers;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.bukkit.plugin.java.JavaPlugin;
import com.github.michaljaz.itemszop.commands.itemszop_cmd;

import java.io.File;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Locale;
import java.util.Objects;

public class Itemszop extends JavaPlugin {

    private static Serializer SERIALIZER;
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
        instance = this;
        getLogger().info("\n _                                         \n" +
                "| |  _                                     \n" +
                "| |_| |_ _____ ____   ___ _____ ___  ____  \n" +
                "| (_   _) ___ |    \\ /___|___  ) _ \\|  _ \\ \n" +
                "| | | |_| ____| | | |___ |/ __/ |_| | |_| |\n" +
                "|_|  \\__)_____)_|_|_(___/(_____)___/|  __/ \n" +
                "                                    |_|  " + this.getDescription().getVersion() + "\n" +
                this.getName() + " by " + this.getDescription().getAuthors() + "\n");
        if (Settings.IMP.DEBUG) { getLogger().info("Klucz to " + secret + "\nWebsocket URL to " + firebaseWebsocketUrl + "\nSerwer ID to " + serverId); }
        Settings.IMP.reload(new File(this.getDataFolder(), "config.yml"), Settings.IMP.NO_PERMISSION);
        registerCommands();
        ComponentSerializer<Component, Component, String> serializer = Serializers.valueOf(Settings.IMP.SERIALIZER.toUpperCase(Locale.ROOT)).getSerializer();
        if (serializer == null) {
            getLogger().warning("The specified serializer could not be founded, using default. (LEGACY_AMPERSAND)");
            setSerializer(new Serializer(Serializers.LEGACY_AMPERSAND.getSerializer() != null ? Serializers.LEGACY_AMPERSAND.getSerializer() : null));
        } else {
            setSerializer(new Serializer(serializer));
        }
        if (Settings.IMP.KEY == null || Settings.IMP.KEY.equals("")) {
            getLogger().warning("Musisz wpisać klucz w pliku konfiguracyjnym, aby plugin mógł działać.");
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
        }
    }
    @Override
    public void onDisable() { socket.close(); }
    private void registerCommands() { new itemszop_cmd().register(getCommand("itemszop")); }
    private static void setSerializer(Serializer serializer) {
        SERIALIZER = serializer;
    }
    public static Serializer getSerializer() {
        return SERIALIZER;
    }
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