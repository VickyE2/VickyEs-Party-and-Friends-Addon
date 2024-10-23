package org.vicky.vicky.listeners;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.vicky.vicky.playerdata.FriendManager;

public class AddFriendListener implements Listener {

    private final JavaPlugin plugin;

    public AddFriendListener(JavaPlugin plugin){
        this.plugin = plugin;
        onFriendCommand();
    }

    public void onFriendCommand(){
        FriendManager manager = new FriendManager(plugin);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
                manager.refresh(player.getUniqueId());
            }
        });
    }
}
