package org.vicky.vicky.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.vicky.guiparent.ButtonAction;
import org.vicky.guiparent.GuiCreator;
import org.vicky.listeners.BaseGuiListener;
import org.vicky.utilities.ANSIColor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FriendsMainListener extends BaseGuiListener {


    private final Map<Integer, ButtonAction> buttonActions = new HashMap<>();
    private Inventory guiInventory = null;
    private JavaPlugin plugin;
    private long lastClickTime = 0;
    private static final long CLICK_DELAY = 200; // Milliseconds

    public FriendsMainListener(JavaPlugin plugin){
        super(plugin);
        this.plugin = plugin;
    }
}
