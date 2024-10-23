package org.vicky.vicky.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.vicky.vicky.gui_s.FriendMainGui;

public class CommandManager implements CommandExecutor {

    private final JavaPlugin plugin;

    public CommandManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (command.getName().equalsIgnoreCase("open_fgui")) {
            if (sender instanceof Player player) {
                FriendMainGui friendMainGui = new FriendMainGui(plugin);
                friendMainGui.showGui(player);
            } else if (sender instanceof ConsoleCommandSender console) {
                console.sendMessage("You can't open a GUI, you dum dum ._.");
            }
        }
        return true;
    }
}