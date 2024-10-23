package org.vicky.vicky.playerdata;

import de.simonsator.partyandfriends.api.pafplayers.PAFPlayer;
import de.simonsator.partyandfriends.api.pafplayers.PAFPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.v_utls.utilities.JsonConfigManager;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class FriendManager {
    private final JsonConfigManager configManager;
    private final JavaPlugin plugin;

    public FriendManager(JavaPlugin plugin) {
        this.configManager = new JsonConfigManager(plugin);
        this.plugin = plugin;
    }

    // Refresh the friends list
    public void refresh(UUID playerUUID) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            JsonConfigManager manager = new JsonConfigManager(plugin);
            manager.createConfigAsync("Users/" + playerUUID + "/friends.json").thenRun(() -> {
                PAFPlayer mainPlayer = PAFPlayerManager.getInstance().getPlayer(playerUUID);
                List<PAFPlayer> friends = mainPlayer.getFriends();
                Set<UUID> currentFriendsUUIDs = new HashSet<>();

                // Collect UUIDs of current friends
                for (PAFPlayer friend : friends) {
                    currentFriendsUUIDs.add(friend.getUniqueId());
                }

                // Load friends from config
                manager.getConfigValueAsync("friends").thenAccept(storedFriends -> {
                    Map<String, Object> storedFriendsMap = (Map<String, Object>) storedFriends;
                    updateFriends(playerUUID, currentFriendsUUIDs, storedFriendsMap);
                });
            });
        });
    }

    // Update friends by adding/removing based on current state
    private void updateFriends(UUID playerUUID, Set<UUID> currentFriendsUUIDs, Map<String, Object> storedFriends) {
        Set<String> storedUUIDs = new HashSet<>();
        if (storedFriends != null) {
            for (Object value : storedFriends.values()) {
                storedUUIDs.add((String) value);
            }
        }

        // Add new friends
        for (UUID uuid : currentFriendsUUIDs) {
            if (!storedUUIDs.contains(uuid.toString())) {
                addFriend(uuid, playerUUID);
            }
        }

        // Remove friends that no longer exist
        for (String storedUUID : storedUUIDs) {
            if (!currentFriendsUUIDs.contains(UUID.fromString(storedUUID))) {
                removeFriend(UUID.fromString(storedUUID), playerUUID);
            }
        }
    }

    // Add a friend to the list
    // Add a friend to the list
    public void addFriend(UUID friendUUID, UUID playerUUID) {
        getNextAvailableId(playerUUID).thenAccept(nextId -> {
            JsonConfigManager manager = new JsonConfigManager(plugin);
            manager.createConfigAsync("Users/" + playerUUID + "/friends.json")
                    .thenCompose(v -> manager.loadConfigValuesAsync()) // Ensure config is loaded
                    .thenCompose(v -> manager.setConfigValueAsync("friends." + nextId + ".uuid", friendUUID)) // Set value after load
                    .thenRun(manager::saveConfigAsync) // Save config after setting the friend
                    .exceptionally(ex -> {
                        plugin.getLogger().severe("Failed to add friend: " + ex.getMessage());
                        ex.printStackTrace();
                        return null;
                    });
        });
    }


    // Remove a friend from the list and adjust IDs
    public void removeFriend(UUID friendUUID, UUID playerUUID) {
        JsonConfigManager manager = new JsonConfigManager(plugin);

        manager.createConfigAsync("Users/" + playerUUID + "/friends.json").thenRun(() -> {
            manager.getConfigValueAsync("friends").thenAccept(friends -> {
                if (friends == null) return;

                // Find the friend by UUID
                AtomicReference<String> targetKey = null;
                Map<String, Object> friendsMap = (Map<String, Object>) friends;
                for (Map.Entry<String, Object> entry : friendsMap.entrySet()) {
                    String friendPath = "friends." + entry.getKey() + ".uuid";
                    manager.getStringValueAsync(friendPath).thenAccept(storedUUID -> {
                        if (storedUUID != null && storedUUID.equals(friendUUID.toString())) {
                            targetKey.set(entry.getKey());
                        }
                    }).join(); // Ensuring it's checked synchronously
                }

                // If found, remove and adjust remaining IDs
                if (targetKey.get() != null) {
                    String finalTargetKey = targetKey.get();
                    manager.setConfigValueAsync("friends." + targetKey, null).thenRun(() -> {
                        adjustFriendIds(Integer.parseInt(finalTargetKey), playerUUID); // Adjust IDs
                    });
                }
            });
        });
    }

    // Get the next available ID for a new friend
    // Get the next available ID for a new friend
    private CompletableFuture<Integer> getNextAvailableId(UUID playerUUID) {
        JsonConfigManager manager = new JsonConfigManager(plugin);
        return manager.createConfigAsync("Users/" + playerUUID + "/friends.json")
                .thenCompose(v -> manager.getConfigValueAsync("friends"))
                .thenApply(friends -> {
                    Map<String, Object> friendsMap = (Map<String, Object>) friends;
                    if (friendsMap == null || friendsMap.isEmpty()) {
                        plugin.getLogger().info("friend map is null or empty");
                        return 1; // Start from ID 1 if no friends exist
                    }

                    // Create a sorted set of existing friend IDs
                    Set<Integer> ids = new TreeSet<>();
                    for (String key : friendsMap.keySet()) {
                        ids.add(Integer.parseInt(key));
                    }

                    // Find the next available ID (smallest missing number)
                    int nextId = 1;
                    for (int id : ids) {
                        if (id != nextId) {
                            break; // Found a gap, return the next available ID
                        }
                        nextId++;
                    }

                    return nextId; // Return the next available ID
                });
    }


    // Adjust friend IDs after removing a friend
    private void adjustFriendIds(int removedId, UUID playerUUID) {
        JsonConfigManager manager = new JsonConfigManager(plugin);

        manager.createConfigAsync("Users/" + playerUUID + "/friends.json").thenRun(() -> {
            manager.getConfigValueAsync("friends").thenAccept(friends -> {
                if (friends == null) return;

                Map<String, Object> friendsMap = (Map<String, Object>) friends;
                // Reassign IDs for friends with higher IDs than the removed one
                for (Map.Entry<String, Object> entry : friendsMap.entrySet()) {
                    int currentId = Integer.parseInt(entry.getKey());
                    if (currentId > removedId) {
                        String newPath = "friends." + (currentId - 1);
                        String oldPath = "friends." + currentId;

                        manager.getStringValueAsync(oldPath + ".uuid").thenAccept(uuid -> {
                            manager.setConfigValueAsync(newPath + ".uuid", uuid);
                            manager.setConfigValueAsync(oldPath, null);
                        });
                    }
                }
            });
        });
    }

    // Get friends sorted by their ID (oldest first)
    public CompletableFuture<List<UUID>> getFriendsSortedByDate(UUID playerUUID) {
        JsonConfigManager manager = new JsonConfigManager(plugin);
        List<UUID> sortedFriends = new ArrayList<>();

        return manager.createConfigAsync("Users/" + playerUUID + "/friends.json")
                .thenCompose(v -> manager.getConfigValueAsync("friends")).thenCompose(friends -> {
                    if (friends == null) {
                        plugin.getLogger().warning("friends data is null");
                        return CompletableFuture.completedFuture(Collections.emptyList());
                    }

                    if (friends instanceof List) {
                        plugin.getLogger().info("Friends data type: " + friends.getClass().getName());
                        List<String> friendList = (List<String>) friends;
                        List<CompletableFuture<Void>> futures = friendList.stream()
                                .map(friendKey -> manager.getStringValueAsync("friends." + friendKey + ".uuid")
                                        .thenAccept(uuid -> {
                                            if (uuid != null) {
                                                sortedFriends.add(UUID.fromString(uuid));
                                            }
                                        })
                                ).collect(Collectors.toList());

                        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                                .thenApply(v2 -> sortedFriends);

                    } else if (friends instanceof Map) {
                        Map<String, Object> friendsMap = (Map<String, Object>) friends;

                        List<CompletableFuture<Void>> futures = friendsMap.entrySet().stream()
                                .sorted(Map.Entry.comparingByKey())
                                .map(entry -> manager.getStringValueAsync("friends." + entry.getKey() + ".uuid")
                                        .thenAccept(uuid -> {
                                            if (uuid != null) {
                                                sortedFriends.add(UUID.fromString(uuid));
                                            }
                                        })
                                ).collect(Collectors.toList());

                        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                                .thenApply(v2 -> sortedFriends);
                    } else {
                        plugin.getLogger().severe("Unexpected type for friends data: " + friends.getClass().getName());
                        return CompletableFuture.completedFuture(Collections.emptyList());
                    }

                });

    }


    // Get friends sorted by their ID (newest first)
    public CompletableFuture<List<UUID>> getFriendsSortedByDateDescending(UUID playerUUID) {
        JsonConfigManager manager = new JsonConfigManager(plugin);
        return manager.createConfigAsync("Users/" + playerUUID + "/friends.json")
                .thenCompose(v -> manager.getConfigValueAsync("friends"))
                .thenApply(friends -> {
                    List<UUID> sortedFriends = new ArrayList<>();
                    Map<String, Object> friendsMap = (Map<String, Object>) friends;

                    if (friendsMap != null) {
                        friendsMap.entrySet().stream()
                                .sorted(Map.Entry.<String, Object>comparingByKey().reversed())
                                .forEachOrdered(entry -> {
                                    manager.getStringValueAsync("friends." + entry.getKey() + ".uuid").thenAccept(uuid -> {
                                        sortedFriends.add(UUID.fromString(uuid));
                                    }).join();
                                });
                    }

                    return sortedFriends;
                });
    }
}
