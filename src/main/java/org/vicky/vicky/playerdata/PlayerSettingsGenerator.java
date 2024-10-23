package org.vicky.vicky.playerdata;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.configurate.ConfigurationNode;
import org.v_utls.utilities.JsonConfigManager;
import org.v_utls.utilities.XmlConfigManager;
import org.vicky.vicky.global.Utils;
import org.w3c.dom.NodeList;

import java.io.File;

import static org.vicky.vicky.global.Utils.manager;

public class PlayerSettingsGenerator {


    private final JavaPlugin plugin;
    private XmlConfigManager manager;
    private OfflinePlayer player;

    public PlayerSettingsGenerator(JavaPlugin plugin, OfflinePlayer player){
        this.plugin = plugin;
        this.manager = new XmlConfigManager(plugin);
        this.player = player;
    }

    public void generatePlayerDefaultSettings(OfflinePlayer player){
        XmlConfigManager xmlConfigManager = new XmlConfigManager(plugin);
        xmlConfigManager.createConfig("Users/"+player.getUniqueId()+"/settings.xml");
        xmlConfigManager.setConfigValue("UserData.User-ID", player.getUniqueId(), "This is the UUID of the player in question", null);
        xmlConfigManager.setConfigValue("UserData.User-Name", player.getName(), "This is the UserName of the player in Question", null);
        xmlConfigManager.setConfigValue("Settings.Theme", Utils.manager.getStringValue("PlayerDefaults.Theme"), "Art Theme for Gui the player uses", null);
        xmlConfigManager.setConfigValue("Settings.AllowNotifications", Utils.manager.getStringValue("PlayerDefaults.AllowNotifications"), null, null);
        xmlConfigManager.setConfigValue("Settings.FriendRequests", Utils.manager.getStringValue("PlayerDefaults.FriendRequests"), null, null);
        xmlConfigManager.setConfigValue("Settings.AllowPrivateChat", Utils.manager.getStringValue("PlayerDefaults.AllowPrivateChats"), null, null);
        xmlConfigManager.setConfigValue("Settings.AllowTeleportingTo", Utils.manager.getStringValue("PlayerDefaults.AllowJumping"), null, null);
        xmlConfigManager.setConfigValue("Settings.HideOnlineTime", Utils.manager.getStringValue("PlayerDefaults.HideOnlineTime"), null, null);
        xmlConfigManager.setConfigValue("Settings.AllowPartyInvites", Utils.manager.getStringValue("PlayerDefaults.AllowPartyInvites"), null, null);
        xmlConfigManager.setConfigValue("Settings.Status", Utils.manager.getStringValue("PlayerDefaults.DefaultStatus"), null, null);
        xmlConfigManager.setConfigValue("Settings.AllowNotifications", Utils.manager.getStringValue("PlayerDefaults.Theme"), null, null);
        xmlConfigManager.setConfigValue("Settings.ChatNameColor", Utils.manager.getStringValue("PlayerDefaults.NameColor"), null, null);
        xmlConfigManager.setConfigValue("Settings.SortingStyle", "by_date_ascending", null, null);
        xmlConfigManager.saveConfig();
    }

    public boolean playerSettingsExist(OfflinePlayer player){
        File userSetting = new File(plugin.getDataFolder(), "Users/"+player.getUniqueId()+"/settings.xml");
        return userSetting.exists();
    }

    public Object getConfigValue(OfflinePlayer player, String path){
        if(playerSettingsExist(player)){
            manager.createConfig("Users/"+player.getUniqueId()+"/settings.xml");
            manager.loadConfigValues();

            ConfigurationNode mainNode = manager.rootNode.node("config");
            for (ConfigurationNode themeNode : mainNode.childrenMap().values()){
                plugin.getLogger().info("child under config, path: " + themeNode + " value: " + themeNode.getString());
            }

            return manager.getConfigValue(path);
        }else{
            generatePlayerDefaultSettings(player);

            return null;
        }
    }

    public void reload(PlayerSettingsGenerator generator){
        manager.createConfig("Users/"+player.getUniqueId()+"/settings.xml");
        manager.loadConfigValues();
    }
}
