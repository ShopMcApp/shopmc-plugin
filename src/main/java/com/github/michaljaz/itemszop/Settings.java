package com.github.michaljaz.itemszop;

import net.elytrium.java.commons.config.YamlConfig;

public class Settings extends YamlConfig {
    @Ignore
    public static final Settings IMP = new Settings();
    public String NO_PERMISSION = "&cNo permission.";
    public String INVALID_ARGUMENT = "&cInvalid argument.";
    @Comment("Change to true if you want to check if the plugin connects to the WebSocket")
    public boolean DEBUG = false;
    @Comment("Interval of checking if the plugin is connected to the WebSocket. The default is 120 seconds or 2 minutes. We recommend that you leave this option at 120.")
    public Integer CHECK_TIME = 120;
    public String CHECK_CONSOLE = "&eCheck the console to see the connection status with the store.";
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