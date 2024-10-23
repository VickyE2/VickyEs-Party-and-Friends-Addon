package org.vicky.vicky.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.v_utls.guiparent.ButtonAction;
import org.v_utls.guiparent.GuiCreator;
import org.v_utls.listeners.BaseGuiListener;
import org.v_utls.utilities.ANSIColor;

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
        this.plugin = plugin;
    }

    @Override
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime < CLICK_DELAY) {
            event.setCancelled(true); // Cancel the event to prevent processing
            return; // Ignore this click
        }
        lastClickTime = currentTime;
        if (event.getClickedInventory() == null){
            event.setCancelled(false);
            return;
        }

        if (guiInventory == null) {
            Bukkit.getLogger().warning("Attempted to click an inventory, but guiInventory is null! [Main-Bank]");
            event.setCancelled(false);
            return;
        }

        if (event.getClickedInventory() != guiInventory){
            event.setCancelled(false);
            return;
        }

        event.setCancelled(true); // Cancel default click behavior
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();

        // Check if the clicked slot has a registered ButtonAction
        if (buttonActions.containsKey(slot)) {
            ButtonAction action = buttonActions.get(slot);
            action.execute(player, plugin); // Execute the associated ButtonAction
        }
    }

    @Override
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory() == guiInventory) {
            buttonActions.clear();
            setGuiInventory(null); // Consider keeping a flag instead of nullifying if you reuse the GUI
        }
    }

    @Override
    public void setGuiInventory(Inventory inventory) {
        if (guiInventory == null) {
            Bukkit.getLogger().warning("GUI inventory is being set to null! Make sure to initialize it before use.");
        }
        Bukkit.getLogger().info(ANSIColor.colorize("Inventory has been set to: " + guiInventory, ANSIColor.CYAN));
        this.guiInventory = guiInventory;
    }


    private Set<Integer> parseSlots(String slotRange, int width) {
        Set<Integer> slots = new HashSet<>();
        String[] parts = slotRange.split(",");

        for (String part : parts) {
            if (part.contains("-")) {
                String[] range = part.split("-");
                int start = Math.max(0, Integer.parseInt(range[0]) - 1);  // Convert to 0-based index
                int end = Math.min(width * 9 - 1, Integer.parseInt(range[1]) - 1);  // Make sure to not exceed the GUI size
                for (int i = start; i <= end; i++) {
                    slots.add(i);
                }
            } else {
                int slot = Integer.parseInt(part) - 1;  // Convert to 0-based index
                slots.add(slot);
            }
        }

        return slots;
    }

    public void registerButton(ButtonAction action, int GuiWidth, GuiCreator.ItemConfig... itemConfigs) {
        Bukkit.getLogger().info(ANSIColor.colorize("Button has been registered with Action " + action.getActionType() + " and action data: " + action.getActionData(), ANSIColor.CYAN));
        // Iterate over the provided item configurations
        for (GuiCreator.ItemConfig itemConfig : itemConfigs) {
            // Parse the slots from the item configuration
            Set<Integer> slotSet = parseSlots(itemConfig.getSlotRange(), GuiWidth);
            // Register the item and action for each parsed slot
            for (int slot : slotSet) {
                buttonActions.put(slot, action);
            }
        }
    }

}
