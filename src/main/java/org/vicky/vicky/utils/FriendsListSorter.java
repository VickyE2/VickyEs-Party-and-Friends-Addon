package org.vicky.vicky.utils;

import de.simonsator.partyandfriends.api.pafplayers.OnlinePAFPlayer;
import de.simonsator.partyandfriends.api.pafplayers.PAFPlayer;
import de.simonsator.partyandfriends.api.pafplayers.PAFPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.vicky.vicky.playerdata.FriendManager;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class FriendsListSorter {

    private JavaPlugin plugin;

    public FriendsListSorter(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public CompletableFuture<List<UUID>> sortList(Player player, String sortType) {
        FriendManager manager = new FriendManager(plugin);
        PAFPlayerManager pafPlayerManager = PAFPlayerManager.getInstance();

        if (pafPlayerManager == null) {
            plugin.getLogger().warning("PAFPlayerManager is not initialized.");
            return CompletableFuture.completedFuture(Collections.emptyList()); // Return an empty list or handle as necessary
        }

        OnlinePAFPlayer mainPlayer = PAFPlayerManager.getInstance().getPlayer(player);
        if (mainPlayer == null) {
            plugin.getLogger().warning("PAFPlayer not found for player: " + player.getUniqueId());
            return CompletableFuture.completedFuture(Collections.emptyList()); // Handle accordingly
        }

        manager.refresh(player.getUniqueId()); // Update the friend list

        List<PAFPlayer> friends = mainPlayer.getFriends();
        List<UUID> friendsUUID = new ArrayList<>();

        switch (sortType) {
            case "by_date_ascending":
                // Return CompletableFuture directly since this is an async operation
                return manager.getFriendsSortedByDate(player.getUniqueId());

            case "by_date_descending":
                // Return CompletableFuture directly since this is an async operation
                return manager.getFriendsSortedByDateDescending(player.getUniqueId());

            case "by_name_ascending":
                friends.sort(Comparator.comparing((PAFPlayer f) -> {
                    OfflinePlayer p = Bukkit.getOfflinePlayer(f.getUniqueId());
                    return (p != null && p.getName() != null) ? p.getName() : ""; // Handle null player or null name
                }));
                break;

            case "by_name_descending":
                friends.sort(Comparator.comparing((PAFPlayer f) -> {
                    OfflinePlayer p = Bukkit.getOfflinePlayer(f.getUniqueId());
                    return (p != null && p.getName() != null) ? p.getName() : ""; // Handle null player or null name
                }).reversed());
                break;


            case "by_last_online_ascending":
                // Synchronous sorting by last online ascending
                friends.sort(Comparator.comparingLong(PAFPlayer::getLastOnline));
                break;

            case "by_last_online_descending":
                // Synchronous sorting by last online descending
                friends.sort(Comparator.comparingLong(PAFPlayer::getLastOnline).reversed());
                break;

            default:
                plugin.getLogger().warning("Unknown sort type: " + sortType);
                return CompletableFuture.completedFuture(Collections.emptyList()); // Handle unknown sort types
        }

        // For synchronous sorts, collect UUIDs and return wrapped in CompletableFuture
        for (PAFPlayer friend : friends) {
            friendsUUID.add(friend.getUniqueId());
        }

        return CompletableFuture.completedFuture(friendsUUID); // Return wrapped in CompletableFuture
    }


    public boolean hasFriendRequest(Player player) {
        OnlinePAFPlayer player1 = PAFPlayerManager.getInstance().getPlayer(player);
        if (player1 == null) {
            plugin.getLogger().warning("PAFPlayer not found for player: " + player.getUniqueId());
            return false; // Handle accordingly
        }

        return !player1.getRequests().isEmpty();
    }

    public boolean isInParty(Player player) {
        OnlinePAFPlayer player1 = PAFPlayerManager.getInstance().getPlayer(player);
        boolean isInParty = player1.getParty() != null;
        plugin.getLogger().info("Player in party: " + isInParty);
        return isInParty;
    }
}
