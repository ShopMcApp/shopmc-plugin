package pl.itemszop;

import net.elytrium.java.commons.config.YamlConfig;

public class Settings extends YamlConfig {

    @Ignore
    public static final Settings IMP = new Settings();

    public String PREFIX = "<green>Itemszop <gray>>»";
    public String KEY = "";
    public String FIREBASEWEBSOCKETURL= "";
    public String SERVERID = "";
    public String SECRET = "";

    @Comment({
            "Available serializers:",
            "LEGACY_AMPERSAND - \"&c&lExample &c&9Text\".",
            "LEGACY_SECTION - \"§c§lExample §c§9Text\".",
            "MINIMESSAGE - \"<bold><red>Example</red> <blue>Text</blue></bold>\". (https://webui.adventure.kyori.net/)",
            "GSON - \"[{\"text\":\"Example\",\"bold\":true,\"color\":\"red\"},{\"text\":\" \",\"bold\":true},{\"text\":\"Text\",\"bold\":true,\"color\":\"blue\"}]\". (https://minecraft.tools/en/json_text.php/)",
            "GSON_COLOR_DOWNSAMPLING - Same as GSON, but uses downsampling."
    })
    public String SERIALIZER = "MINIMESSAGE";


}