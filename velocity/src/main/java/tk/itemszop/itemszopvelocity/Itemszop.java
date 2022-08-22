package tk.itemszop.itemszopvelocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;

import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import java.io.File;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Base64;

@Plugin(
        id = "itemszopvelocity",
        name = "Itemszopvelocity",
        version = BuildConstants.VERSION
)
public class Itemszop {
    private static Itemszop INSTANCE;
    private static Itemszop instance;
    //public static WebSocket socket;
    public String firebaseWebsocketUrl;
    String serverId;
    String secret;

    private static Logger logger;
    private static ProxyServer server;
    private final Path dataDirectory;
    public static WebSocket socket;
    @Inject
    public Itemszop(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        INSTANCE = this;
        // TODO: Do zmiany
        logger.info("Itemszop - ladowanie");
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
