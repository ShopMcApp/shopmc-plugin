package pl.itemszop;

import net.elytrium.java.commons.mc.serialization.Serializer;
import org.bukkit.plugin.java.JavaPlugin;
import net.elytrium.java.commons.mc.serialization.Serializers;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import pl.itemszop.commands.itemszop;

import java.io.File;
import java.util.Objects;

public final class Itemszop extends JavaPlugin {

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
