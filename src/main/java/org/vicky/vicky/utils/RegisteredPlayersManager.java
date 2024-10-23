package org.vicky.vicky.utils;

import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.v_utls.utilities.XmlConfigManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class RegisteredPlayersManager {

    private final JavaPlugin plugin;

    public RegisteredPlayersManager(JavaPlugin plugin){
        this.plugin = plugin;
    }

    public void createRegister(){
        XmlConfigManager xmlConfigManager = new XmlConfigManager(plugin);
        xmlConfigManager.createConfig("data/registeredUsers.xml");
    }


    public void registerPlayer(OfflinePlayer player){
        XmlConfigManager xmlConfigManager = new XmlConfigManager(plugin);
        xmlConfigManager.createConfig("data/registeredUsers.xml");

        Map<String, String> playerAttributes = Map.of(
                "playerUUID", player.getUniqueId().toString(),
                "Username", player.getName()
        );


        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedDate = currentDate.format(formatter);

        xmlConfigManager.setConfigValue("players.player", "", "Registered on " + formattedDate, playerAttributes);
        xmlConfigManager.saveConfig();
    }

    public boolean isRegisteredPlayer(OfflinePlayer player){
        XmlConfigManager xmlConfigManager = new XmlConfigManager(plugin);
        xmlConfigManager.createConfig("data/registeredUsers.xml");

        String UUID = player.getUniqueId().toString();
        String Username = player.getName();
        String path = "players.player";

        // Check if the player node exists
        if (xmlConfigManager.doesPathExist(path)) {
            // Retrieve the attributes of the player node
            Map<String, String> attributes = xmlConfigManager.getAttributes(path);

            if (attributes != null) {
                // Check if the attributes match the specified playerId, username, and rank
                String configPlayerId = attributes.get("playerUUID");
                String configUsername = attributes.get("Username");

                // Compare the attributes with the provided values
                return configPlayerId.equals(UUID) && configUsername.equals(Username);
            }
        }
        return false;
    }
}
