package org.vicky.vicky.gui_s;

import dev.lone.LoneLibs.S;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.v_utls.guiparent.BaseGui;
import org.v_utls.guiparent.GuiCreator;
import org.v_utls.utilities.RanksLister;
import org.vicky.vicky.listeners.FriendsMainListener;
import org.vicky.vicky.messages.MessageManager;
import org.vicky.vicky.playerdata.FriendManager;
import org.vicky.vicky.playerdata.PlayerSettingsGenerator;
import org.vicky.vicky.utils.FriendsListSorter;
import org.vicky.vicky.utils.ThemeStorer;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import static org.vicky.vicky.global.Listeners.mainFriendsListener;

public class FriendMainGui extends BaseGui {

    private final GuiCreator guiManager;
    private final ThemeStorer storer;
    private FriendsMainListener listener;
    private final JavaPlugin plugin;

    public FriendMainGui(JavaPlugin plugin) {
        super(plugin, mainFriendsListener);
        this.listener = mainFriendsListener;
        this.plugin = plugin;

        // Initialize other fields
        this.guiManager = new GuiCreator(plugin, mainFriendsListener);
        this.storer = new ThemeStorer(plugin);

    }

    GuiCreator.ItemConfig IASORTBUTTON = null;
    GuiCreator.ItemConfig IALEFTBUTTON = null;
    GuiCreator.ItemConfig IARIGHTBUTTON = null;
    GuiCreator.ItemConfig IAFRIENDREQUEST = null;
    GuiCreator.ItemConfig IAMESSAGEREQUEST = null;
    GuiCreator.ItemConfig IASETTINGS = null;
    GuiCreator.ItemConfig IAPARTYBUTTON_1 = null;
    GuiCreator.ItemConfig IAPARTYBUTTON_2 = null;
    GuiCreator.ItemConfig IAPLAYERHEAD = null;

//.........................................//
    GuiCreator.ItemConfig PLAYER_1 = null;
    GuiCreator.ItemConfig PLAYER_2 = null;
    GuiCreator.ItemConfig PLAYER_3 = null;
    GuiCreator.ItemConfig PLAYER_4 = null;
    GuiCreator.ItemConfig PLAYER_5 = null;
    GuiCreator.ItemConfig PLAYER_6 = null;
    GuiCreator.ItemConfig PLAYER_7 = null;
    GuiCreator.ItemConfig PLAYER_8 = null;
    GuiCreator.ItemConfig PLAYER_9 = null;
    GuiCreator.ItemConfig PLAYER_10 = null;
    GuiCreator.ItemConfig PLAYER_11 = null;
    GuiCreator.ItemConfig PLAYER_12 = null;
    GuiCreator.ItemConfig PLAYER_13 = null;
    GuiCreator.ItemConfig PLAYER_14 = null;
    GuiCreator.ItemConfig PLAYER_15 = null;
    GuiCreator.ItemConfig PLAYER_16 = null;
    GuiCreator.ItemConfig PLAYER_17 = null;
    GuiCreator.ItemConfig PLAYER_18 = null;
    GuiCreator.ItemConfig PLAYER_19 = null;
    GuiCreator.ItemConfig PLAYER_20 = null;
    GuiCreator.ItemConfig PLAYER_21 = null;
 //.........................................//


    @Override
    public void showGui(Player player) {

        PlayerSettingsGenerator settingsGenerator = new PlayerSettingsGenerator(plugin, player);
        FriendsListSorter sorter = new FriendsListSorter(plugin);
        RanksLister lister = new RanksLister();
        MessageManager messageManager = new MessageManager(plugin);
        HashMap<String, GuiCreator.ItemConfig> Items = new HashMap<>();


        boolean iaEnabled = Bukkit.getServer().getPluginManager().getPlugin("ItemsAdder") != null;
        boolean hasFriendRequest = sorter.hasFriendRequest(player);

        if (iaEnabled) {
            String SortingName;
            String theme_id = "";
            String sorttype = settingsGenerator.getConfigValue(player, "Settings.SortingStyle").toString();
            if (storer.isRegisteredTheme(settingsGenerator.getConfigValue(player, "Settings.Theme").toString())) {
                theme_id = storer.getThemeID(settingsGenerator.getConfigValue(player, "Settings.Theme").toString());
            } else {
                plugin.getLogger().severe("Player " + player.getName() + " has an enabled theme: " + storer.isRegisteredTheme(settingsGenerator.getConfigValue(player, "Settings.Themes").toString()) + " which dosent exist");
                theme_id = "lt";
            }

            if (Objects.equals(sorttype, "by_date_ascending")) {
                SortingName = "ʙʏ ᴅᴀᴛᴇ <ᴀsᴄᴇɴᴅɪɴɢ>";
            } else if (Objects.equals(sorttype, "by_date_descending")) {
                SortingName = "ʙʏ ᴅᴀᴛᴇ <ᴅᴇsᴄᴇɴᴅɪɴɢ>";
            } else if (Objects.equals(sorttype, "by_name_ascending")) {
                SortingName = "ʙʏ ɴᴀᴍᴇ <ᴀsᴄᴇɴᴅɪɴɢ>";
            } else if (Objects.equals(sorttype, "by_name_descending")) {
                SortingName = "ʙʏ ɴᴀᴍᴇ <ᴅᴇsᴄᴇɴᴅɪɴɢ>";
            } else if (Objects.equals(sorttype, "by_last_online_ascending")) {
                SortingName = "ʙʏ ʟᴀsᴛ ᴏɴʟɪɴᴇ <ᴀsᴄᴇɴᴅɪɴɢ>";
            } else if (Objects.equals(sorttype, "by_last_online_descending")) {
                SortingName = "ʙʏ ʟᴀsᴛ ᴏɴʟɪɴᴇ <ᴅᴇsᴄᴇɴᴅɪɴɢ>";
            } else {
                SortingName = "";
            }

            Bukkit.getScheduler().runTask(plugin, () -> {
                sorter.sortList(player, sorttype)
                        .thenAccept(friend -> {
                            if (friend == null) {
                                plugin.getLogger().warning("Received null friends list from sorter");
                                return;
                            }

                            if (friend.isEmpty()) {
                                plugin.getLogger().info("Friends list is empty");
                                return;
                            }

                            List<GuiCreator.ItemConfig> friendConfigs = List.of(
                                    PLAYER_1, PLAYER_2, PLAYER_3, PLAYER_4, PLAYER_5, PLAYER_6, PLAYER_7,
                                    PLAYER_8, PLAYER_9, PLAYER_10, PLAYER_11, PLAYER_12, PLAYER_13,
                                    PLAYER_14, PLAYER_15, PLAYER_16, PLAYER_17, PLAYER_18, PLAYER_19, PLAYER_20, PLAYER_21
                            );

                            // Create a sublist for the first 21 friends or fewer if the list is smaller
                            List<UUID> handleFriends = friend.size() > 21 ? friend.subList(0, 21) : new ArrayList<>(friend);

                            // Create friend item configs asynchronously
                            createFriendItemConfigs(handleFriends, friendConfigs).thenAccept(friendItems -> {
                                if (friendItems != null && !friendItems.isEmpty()) {
                                    for (int i = 0; i < friendItems.size(); i++) {
                                        Items.put("PLAYER_" + (i + 1), friendItems.get(i));
                                    }
                                }

                                // You may want to trigger GUI updates here as well
                            }).exceptionally(ex -> {
                                plugin.getLogger().severe("Error creating friend item configs: " + ex.getMessage());
                                ex.printStackTrace();
                                return null;
                            });
                        }).exceptionally(ex -> {
                            plugin.getLogger().severe("Error sorting friends: " + ex.getMessage());
                            ex.printStackTrace();
                            return null;
                        });
            });




            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "ᴄᴜʀʀᴇɴᴛ sᴏʀᴛɪɴɢ ᴏʀᴅᴇʀ: ");
            lore.add("    " + ChatColor.GOLD + SortingName);

            IASORTBUTTON = new GuiCreator.ItemConfig(
                    null,
                    ChatColor.GREEN + "sᴏʀᴛ",
                    "50",
                    true,
                    null,
                    "sort_" + theme_id,
                    lore
            );

            Items.put("IASORTBUTTON", IASORTBUTTON);

            IALEFTBUTTON = new GuiCreator.ItemConfig(
                    null,
                    ChatColor.GREEN + "ᴘʀᴇᴠɪᴏᴜs",
                    "49",
                    true,
                    null,
                    "left_arrow_" + theme_id,
                    null
            );

            IARIGHTBUTTON = new GuiCreator.ItemConfig(
                    null,
                    ChatColor.GREEN + "ɴᴇxᴛ",
                    "51",
                    true,
                    null,
                    "right_arrow_" + theme_id,
                    null
            );

            UUID playerId = player.getUniqueId();

            lister.getUserPrefix(playerId).thenAccept(prefix ->
                    plugin.getLogger().info("prefix is " + prefix)
            );
            lister.getUserSuffix(playerId).thenAccept(suffix ->
                    plugin.getLogger().info("suffix is " + suffix)
            );


            List<String> headLore = new ArrayList<>();
            AtomicReference<String> title = new AtomicReference<>(player.getName());

            lister.getUserPrefix(playerId).thenAccept(prefix -> {
                if (prefix != null && !prefix.isEmpty()) {
                    title.set(prefix + ChatColor.RESET + " | " + title.get());
                }
            }).thenRun(() -> {
                lister.getUserSuffix(playerId).thenAccept(suffix -> {
                    if (suffix != null && !suffix.isEmpty()) {
                        title.set(title.get() + ChatColor.RESET + " | " + suffix);
                    }
                    // Now you can use the complete title
                    plugin.getLogger().info("Final title: " + title.get());
                });
            });

            headLore.add("");
            headLore.add(ChatColor.RESET + "ʏᴏᴜʀ sᴛᴀᴛᴜs");
            headLore.add(ChatColor.YELLOW + "→ " + ChatColor.AQUA + settingsGenerator.getConfigValue(player, "Settings.Status"));

            IAPLAYERHEAD = new GuiCreator.ItemConfig(
                    null,
                    ChatColor.RESET + title.get(),
                    "11",
                    false,
                    null,
                    "player_head_large",
                    headLore
            );

            Items.put("IAPLAYERHEAD", IAPLAYERHEAD);

            String requestF;
            if (hasFriendRequest) {
                requestF = "friend_request_has_" + theme_id;
            } else {
                requestF = "friend_request_empty_" + theme_id;
            }
            IAFRIENDREQUEST = new GuiCreator.ItemConfig(
                    null,
                    ChatColor.GREEN + "ғʀɪᴇɴᴅ ʀᴇQᴜᴇsᴛs",
                    "27",
                    true,
                    null,
                    requestF,
                    null
            );

            Items.put("IAFRIENDREQUEST", IAFRIENDREQUEST);

            String requestM;
            if (messageManager.hasValidMessageRequest(player)) {
                requestM = " message_request_has_" + theme_id;
            } else {
                requestM = "message_request_empty_" + theme_id;
            }
            IAMESSAGEREQUEST = new GuiCreator.ItemConfig(
                    null,
                    ChatColor.GREEN + "ᴍᴇssᴀɢᴇ ʀᴇQᴜᴇsᴛs",
                    "36",
                    true,
                    null,
                    requestM,
                    null
            );

            Items.put("IAMESSAGEREQUEST", IAMESSAGEREQUEST);

            IASETTINGS = new GuiCreator.ItemConfig(
                    null,
                    ChatColor.GREEN + "sᴇᴛᴛɪɴɢs",
                    "45",
                    true,
                    null,
                    "settings_rounded_left_" + theme_id,
                    null
            );

            Items.put("IASETTINGS", IASETTINGS);

            if (sorter.isInParty(player)) {
                IAPARTYBUTTON_1 = new GuiCreator.ItemConfig(
                        null,
                        ChatColor.GREEN + "ᴘᴀʀᴛʏ",
                        "7",
                        true,
                        null,
                        "party_long_button_" + theme_id + "_0",
                        null
                );

                IAPARTYBUTTON_2 = new GuiCreator.ItemConfig(
                        null,
                        ChatColor.GREEN + "ᴘᴀʀᴛʏ",
                        "8",
                        true,
                        null,
                        "party_long_button_" + theme_id + "_1",
                        null
                );

                Items.put("IAPARTYBUTTON_1", IAPARTYBUTTON_1);
                Items.put("IAPARTYBUTTON_2", IAPARTYBUTTON_2);
            }

            guiManager.openGUI(player, 6, 9, "", true, "vicky_themes:friends_gui_main_panel_" + theme_id, -8,
                    Items.values().toArray(new GuiCreator.ItemConfig[0])
            );

        }

    }

    public CompletableFuture<List<GuiCreator.ItemConfig>> createFriendItemConfigs(List<UUID> friends, List<GuiCreator.ItemConfig> itemConfigs) {
        List<GuiCreator.ItemConfig> friendItemConfigs = new ArrayList<>();
        RanksLister lister = new RanksLister();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int i = 0; i < Math.min(friends.size(), 21); i++) {
            UUID friend = friends.get(i);
            OfflinePlayer player = Bukkit.getPlayer(friend);
            PlayerSettingsGenerator settingsGenerator = new PlayerSettingsGenerator(plugin, player);
            OfflinePlayer currentFriend = Bukkit.getOfflinePlayer(friend);
            String friendName = currentFriend.getName();
            AtomicReference<String> title = new AtomicReference<>(friendName); // Using friend's name as default

            int finalI = i;
            CompletableFuture<Void> future = lister.getUserPrefix(friend).thenAccept(prefix -> {
                if (prefix != null && !prefix.isEmpty()) {
                    title.set(prefix + ChatColor.RESET + " | " + title.get());
                }
            }).thenRun(() -> {
                lister.getUserSuffix(friend).thenAccept(suffix -> {
                    if (suffix != null && !suffix.isEmpty()) {
                        title.set(title.get() + ChatColor.RESET + " | " + suffix);
                    }
                    // Now you can use the complete title
                    plugin.getLogger().info("Final title: " + title.get());

                    List<String> lore = new ArrayList<>();
                    lore.add("");
                    lore.add(ChatColor.RESET + "ʏᴏᴜʀ sᴛᴀᴛᴜs");
                    lore.add(ChatColor.YELLOW + "→ " + ChatColor.AQUA + settingsGenerator.getConfigValue(player, "Settings.Status"));

                    // Create ItemConfig and add to the list after fetching title
                    itemConfigs.set(finalI, new GuiCreator.ItemConfig(
                            null, // Or any material you want
                            title.get(),
                            Integer.toString(finalI + 19), // Slot calculation
                            true,
                            null,
                            "player_head_gui_size_1",
                            lore
                    ));
                    friendItemConfigs.add(itemConfigs.get(finalI));
                });
            });
            futures.add(future);
        }

        // Wait for all futures to complete and then return the filled list
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> friendItemConfigs);
    }

}
