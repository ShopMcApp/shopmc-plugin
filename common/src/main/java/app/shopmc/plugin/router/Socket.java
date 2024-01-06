package app.shopmc.plugin.router;

import org.java_websocket.client.WebSocketClient;

import java.net.URI;

public abstract class Socket extends WebSocketClient {

    public abstract void onCommand(String command);

    public Socket(String key) {
        super(URI.create("wss://router.shopmc.app/" + key));
    }

    @Override
    public void onMessage(String commands) {
        for (String command : commands.split("\n")) {
            onCommand(command);
        }
    }
}
