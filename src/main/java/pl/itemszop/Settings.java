package pl.itemszop;

import net.elytrium.java.commons.config.YamlConfig;

public class Settings extends YamlConfig {
    @Ignore
    public static final Settings IMP = new Settings();
    public String NO_PERMISSION = "&cBrak uprawnień.";
    @Comment("Zmień na true jeśli chcesz sprawdzić czy plugin łączy się z WebSocketem")
    public boolean DEBUG = false;
    @Comment("Interwał sprawdzania czy plugin jest połączony z WebSocketem. Domyślna wartość to 120 sekund czyli 2 minuty. Zalecemy pozostawienie tej opcji na 120.")
    public Integer CHECK_TIME = 120;
    public String CHECK_CONSOLE = "&eSprawdź konsolę aby zobaczyć status połączenia ze sklepem.";
    public String KEY = "";
    @Comment({
            "Available serializers:",
            "LEGACY_AMPERSAND - \"&c&lExample &c&9Text\".",
            "LEGACY_SECTION - \"§c§lExample §c§9Text\".",
            "MINIMESSAGE - \"<bold><red>Example</red> <blue>Text</blue></bold>\". (https://webui.adventure.kyori.net/)",
            "GSON - \"[{\"text\":\"Example\",\"bold\":true,\"color\":\"red\"},{\"text\":\" \",\"bold\":true},{\"text\":\"Text\",\"bold\":true,\"color\":\"blue\"}]\". (https://minecraft.tools/en/json_text.php/)",
            "GSON_COLOR_DOWNSAMPLING - Same as GSON, but uses downsampling."
    })
    public String SERIALIZER = "LEGACY_AMPERSAND";
}