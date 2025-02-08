package org.vicky.vicky;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.vicky.utilities.ANSIColor;
import org.vicky.utilities.ConfigManager;
import org.vicky.vicky.command.CommandManager;
import org.vicky.vicky.config.Config;
import org.vicky.vicky.listeners.FriendsMainListener;
import org.vicky.vicky.utilities.DBTemplates.*;
import org.vicky.vicky_utils;

import java.util.Objects;

import static org.vicky.vicky.global.Listeners.mainFriendsListener;
import static org.vicky.vicky.global.Utils.*;

public final class VickyEsPnA extends JavaPlugin{

    public vicky_utils utils = (vicky_utils) getServer().getPluginManager().getPlugin("Vicky-s_Utilities");
    @Override
    public void onLoad() {
        if (utils != null) {
            vicky_utils.registerTemplatePackage("VickyEs_PnA-0.0.1-MIKA", "org.vicky.vicky.utilities.DBTemplates");
            vicky_utils.registerTemplateUtilityPackage("VickyEs_PnA-0.0.1-MIKA", "org.vicky.vicky.utilities.enums");
        }
    }

    @Override
    public void onEnable() {
        getLogger().info(ANSIColor.colorize("VickyE's Friend Addon is Being Enabled", ANSIColor.YELLOW_BOLD));
        // Plugin startup logic
        if (Bukkit.getPluginManager().getPlugin("Vicky-s_Utilities") != null) {
            if (utils != null) {
                sqlManager = utils.getSQLManager();
                databaseManager = utils.getDatabaseManager();
            }else {
                throw new RuntimeException("Plugin Vicky Utilities cannot be found on motherboard?");
            }
            try{
                Thread.sleep(2000);

                manager = new ConfigManager(this, "config.yml");
                Config config = new Config(this);
                config.registerConfigs();

                mainFriendsListener = new FriendsMainListener(this);

                try {
                    if (getCommand("open_fgui") == null) {
                        getLogger().severe("The command /open_fgui is not registered! Check your plugin.yml");
                    } else {
                        Objects.requireNonNull(getCommand("open_fgui")).setExecutor(new CommandManager(this));
                        getLogger().info("/open_fgui command registered.");
                    }
                } catch (Exception e) {
                    getLogger().severe("An error occurred: " + e.getMessage());
                    e.printStackTrace();
                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }else if (Bukkit.getPluginManager().getPlugin("Vicky-s_Utilities") == null){
            getLogger().severe("Missing Dependency Plugin: Vicky's Utilities. This is required for.....EVERYTHING. get it...");
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


}
