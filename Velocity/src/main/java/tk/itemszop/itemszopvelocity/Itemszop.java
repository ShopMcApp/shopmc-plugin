package tk.itemszop.itemszopvelocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import net.elytrium.java.commons.mc.serialization.Serializer;
import org.slf4j.Logger;
import tk.itemszop.itemszopvelocity.commands.ItemszopCommand;

import java.io.File;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@Plugin(
        id = "itemszopvelocity",
        name = "Itemszop Velocity",
        version = "1.0",
        url = "https://itemszop.tk/",
        authors = {
                "ReferTV",
                "drmike",
                "mrpiesel"
        }
)
public class Itemszop {
    private static Itemszop INSTANCE;
    private static Serializer SERIALIZER;
    private final Path dataDirectory;
    private final File configFile;
    public String firebaseWebsocketUrl;
    private static Logger logger;
    private static ProxyServer server;
    public static WebSocket socket;
    ScheduledTask websocketReconnectTask;
    String serverId;
    String secret;

    @Inject
    public Itemszop(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        Itemszop.server = server;
        Itemszop.logger = logger;
        this.dataDirectory = dataDirectory;

        File dataDirectoryFile = dataDirectory.toFile();
        this.configFile = new File(dataDirectoryFile, "config.yml");
    }
    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {

        CommandManager manager = this.server.getCommandManager();
        manager.unregister("itemszopv");
        manager.register("itemszopv", new ItemszopCommand(this), "itemszopvelocity", "vitemszop");

        INSTANCE = this;
        logger.info("\n _                                         \n" +
                "| |  _                                     \n" +
                "| |_| |_ _____ ____   ___ _____ ___  ____  \n" +
                "| (_   _) ___ |    \\ /___|___  ) _ \\|  _ \\ \n" +
                "| | | |_| ____| | | |___ |/ __/ |_| | |_| |\n" +
                "|_|  \\__)_____)_|_|_(___/(_____)___/|  __/ \n" +
                "                                    |_|  " + "1.0");
        Settings.IMP.reload(new File(dataDirectory.toString(), "config.yml"));
        if (Settings.IMP.KEY == null || Settings.IMP.KEY.equals("")) {
            logger.error("You have to enter the key in the config file for the plugin to work.");
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
                        logger.info(firebaseWebsocketUrl);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            WebSocketConnect();
        }
        if (Settings.IMP.DEBUG) { logger.info("Key: " + secret + "\nWebsocket URL: " + firebaseWebsocketUrl + "\nServer ID: " + serverId); }
    }
    public static Itemszop getInstance() {
        return INSTANCE;
    }
    public static ProxyServer getProxyServer () {
        return server;
    }
    public static Logger getLogger () {
        return logger;
    }
    public void WebSocketReconnectTask() {
       websocketReconnectTask = server.getScheduler()
                .buildTask(INSTANCE, () -> {
                    if (!socket.isOpen()) {
                        if (Settings.IMP.DEBUG) { logger.info("Reconnecting to websocket..."); }
                        socket.reconnect();
                    } else {
                        // TODO cancel task
                        websocketReconnectTask.cancel();
                    }
                })
                .repeat(Settings.IMP.CHECK_TIME, TimeUnit.MINUTES)
                .schedule();
    }
    private static void setSerializer(Serializer serializer) {
        SERIALIZER = serializer;
    }
    public static Serializer getSerializer() {
        return SERIALIZER;
    }
    public void reloadPlugin() { Settings.IMP.reload(new File(configFile, "config.yml")); }
    public void WebSocketConnect() {
        try {
            socket = new WebSocket(new URI(firebaseWebsocketUrl));
            socket.connect();
            socket.setConnectionLostTimeout( 0 );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
