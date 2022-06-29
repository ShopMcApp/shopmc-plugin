package pl.itemszop;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import net.elytrium.java.commons.mc.serialization.Serializer;
import org.bukkit.plugin.java.JavaPlugin;
import net.elytrium.java.commons.mc.serialization.Serializers;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import pl.itemszop.commands.itemszop;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public final class Itemszop extends JavaPlugin {

    HttpServer triggerServer;
    static FirebaseSync sync = new FirebaseSync();

    private static Itemszop instance;
    private static Serializer serializer;

    public static Itemszop getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        Settings.IMP.reload(new File(this.getDataFolder(), "config.yml"), Settings.IMP.PREFIX);

        instance = this;

        this.getLogger().info(this.getName() + this.getDescription().getVersion() + " by " + this.getDescription().getAuthors());
        this.registerCommands();

        ComponentSerializer<Component, Component, String> serializer = Serializers.valueOf(Settings.IMP.SERIALIZER).getSerializer();
        if (serializer == null) {
            this.getLogger().info("The specified serializer could not be founded, using default. (LEGACY_AMPERSAND)");
            setSerializer(new Serializer(Objects.requireNonNull(Serializers.LEGACY_AMPERSAND.getSerializer())));
        } else {
            setSerializer(new Serializer(serializer));
        }

        try {
            triggerServer = HttpServer.create(new InetSocketAddress("localhost", Integer.parseInt(Settings.IMP.OPTIONS.TRIGGERPORT)), 0);
            triggerServer.createContext("/itemszop_update", new MyHandler());
            ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
            triggerServer.setExecutor(threadPoolExecutor);
            triggerServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "OK";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
            sync.syncWithFirebase();
            getLogger().info("Itemszop commands updated [web-trigger]");
        }
    }

    @Override
    public void onDisable() {
    }

    private void registerCommands() {
        this.getCommand("itemszop").setExecutor(new itemszop());
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
