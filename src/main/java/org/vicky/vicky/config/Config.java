package org.vicky.vicky.config;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import static org.vicky.vicky.global.Utils.manager;

public class Config {

    private final JavaPlugin plugin;

    public Config(JavaPlugin plugin){
        this.plugin = plugin;

    }

    public void registerConfigs() {
        if (!manager.doesPathExist("PlayerDefaults.Theme")) {
            manager.setConfigValue("PlayerDefaults", "Theme", "Light Theme", null);
        }
        if (!manager.doesPathExist("PlayerDefaults.AllowNotifications")) {
            manager.setConfigValue("PlayerDefaults", "AllowNotifications", true, null);
        }
        if (!manager.doesPathExist("PlayerDefaults.FriendRequests")) {
            manager.setConfigValue("PlayerDefaults", "FriendRequests", true, null);
        }
        if (!manager.doesPathExist("PlayerDefaults.AllowPrivateChats")) {
            manager.setConfigValue("PlayerDefaults", "AllowPrivateChats", "friends_Only", null);
        }
        if (!manager.doesPathExist("PlayerDefaults.AllowJumping")) {
            manager.setConfigValue("PlayerDefaults", "AllowJumping", false, null);
        }
        if (!manager.doesPathExist("PlayerDefaults.HideOnlineTime")) {
            manager.setConfigValue("PlayerDefaults", "HideOnlineTime", false, null);
        }
        if (!manager.doesPathExist("PlayerDefaults.AllowPartyInvites")) {
            manager.setConfigValue("PlayerDefaults", "AllowPartyInvites", true, null);
        }
        if (!manager.doesPathExist("PlayerDefaults.DefaultStatus")) {
            manager.setConfigValue("PlayerDefaults", "DefaultStatus", "Just Joined :]", null);
        }
        if (!manager.doesPathExist("PlayerDefaults.ChatNameColor")) {
            manager.setConfigValue("PlayerDefaults", "ChatNameColor", ChatColor.WHITE, null);
        }
    }
}
