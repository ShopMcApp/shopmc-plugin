package pl.itemszop;

import net.elytrium.java.commons.config.YamlConfig;

public class Settings extends YamlConfig {

    @Ignore
    public static final Settings IMP = new Settings();

    public String PREFIX = "<green>Itemszop &8»";

    @Comment({
            "Available serializers:",
            "LEGACY_AMPERSAND - \"&c&lExample &c&9Text\".",
            "LEGACY_SECTION - \"§c§lExample §c§9Text\".",
            "MINIMESSAGE - \"<bold><red>Example</red> <blue>Text</blue></bold>\". (https://webui.adventure.kyori.net/)",
            "GSON - \"[{\"text\":\"Example\",\"bold\":true,\"color\":\"red\"},{\"text\":\" \",\"bold\":true},{\"text\":\"Text\",\"bold\":true,\"color\":\"blue\"}]\". (https://minecraft.tools/en/json_text.php/)",
            "GSON_COLOR_DOWNSAMPLING - Same as GSON, but uses downsampling."
    })
    public String SERIALIZER = "MINIMESSAGE";

    @Create
    public OPTIONS OPTIONS;

    public static class OPTIONS {

        public String SERVERID = "gamesmc";
        public String DATABASEURL = "https://gamesmc-1a674-default-rtdb.europe-west1.firebasedatabase.app";
        public String TRIGGERPORT = "8001";
    }

}