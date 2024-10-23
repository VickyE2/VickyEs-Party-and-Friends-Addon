package org.vicky.vicky.utils;

import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.v_utls.utilities.XmlConfigManager;

import java.util.HashMap;
import java.util.Map;

public class ThemeStorer {

    private XmlConfigManager manager;
    private JavaPlugin plugin;

    public ThemeStorer(JavaPlugin plugin){
        this.plugin = plugin;
        manager = new XmlConfigManager(plugin);
        createXml();
    }

    public void createXml(){
        manager.createConfig("themes/RegisteredThemes.xml");
    }

    public void addTheme(String themeId, String themeName){
        if (!isRegisteredTheme(themeName)) {
            Map<String, String> attributes = new HashMap<>();
            attributes.put("theme-name", themeName);
            attributes.put("theme-id", themeId);
            manager.setConfigValue("themes.theme", "", null, attributes);
            plugin.getLogger().info("Added Theme with id: " + themeId);
        }
    }

    public boolean isRegisteredTheme(String themeName){
        manager.loadConfigValues();

        String path = "themes.theme";

        // Check if the player node exists
        if (manager.doesPathExist(path)) {
            // Retrieve the attributes of the player node
            Map<String, String> attributes = manager.getAttributes(path);

            if (attributes != null) {
                String derivedName = attributes.get("theme-name");

                // Compare the attributes with the provided values
                return derivedName.equals(themeName);
            }
        }
        return false;
    }

    public String getThemeID(String themeName){
        manager.loadConfigValues();

        String path = "themes.theme";

        if (manager.doesPathExist(path)){
            Map<String, String> attributes = manager.getAttributes(path);

            if (attributes != null && attributes.get("theme-name").equals(themeName)) {
                return attributes.get("theme-id");
            }
        }
        return null;
    }
}
