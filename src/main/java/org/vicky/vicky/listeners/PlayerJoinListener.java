package org.vicky.vicky.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.vicky.vicky.playerdata.PlayerSettingsGenerator;
import org.vicky.vicky.utils.RegisteredPlayersManager;

public class PlayerJoinListener implements Listener {

    private JavaPlugin plugin;

    public PlayerJoinListener(JavaPlugin plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent event){
        RegisteredPlayersManager PManager = new RegisteredPlayersManager(plugin);
        if(!PManager.isRegisteredPlayer(event.getPlayer())){
            PManager.registerPlayer(event.getPlayer());
            PlayerSettingsGenerator generator = new PlayerSettingsGenerator(plugin, event.getPlayer());
            if (!generator.playerSettingsExist(event.getPlayer())) {
                generator.generatePlayerDefaultSettings(event.getPlayer());
            }
        }else{
            plugin.getLogger().info("Player with name: " + event.getPlayer().getName() + " is Already registered");
        }
    }
}
