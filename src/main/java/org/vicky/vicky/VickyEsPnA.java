package org.vicky.vicky;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.vicky.utilities.ANSIColor;
import org.vicky.utilities.ConfigManager;
import org.vicky.vicky.command.CommandManager;
import org.vicky.vicky.config.Config;
import org.vicky.vicky.listeners.AddFriendListener;
import org.vicky.vicky.listeners.FriendsMainListener;
import org.vicky.vicky.listeners.PlayerJoinListener;
import org.vicky.vicky.playerdata.FriendManager;
import org.vicky.vicky.utils.RegisteredPlayersManager;
import org.vicky.vicky.utils.ThemeUnzipper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static org.vicky.vicky.global.Listeners.mainFriendsListener;
import static org.vicky.vicky.global.Utils.manager;

public final class VickyEsPnA extends JavaPlugin{

    @Override
    public void onEnable() {
        getLogger().info(ANSIColor.colorize("VickyE's Friend Addon is Being Enabled", ANSIColor.YELLOW_BOLD));
        // Plugin startup logic
        if (Bukkit.getPluginManager().getPlugin("Vicky-s_Utilities") != null && Bukkit.getPluginManager().getPlugin("PartyAndFriends") != null) {

            List<String> folders = new ArrayList<>();
            folders.add("themes");

            for (String folderToExtract : folders) {
                File desti = new File(getDataFolder() + "/themes/");
                if (!desti.exists()){
                    desti.mkdir();
                }
                extractFolderFromJar(folderToExtract, desti);
            }
            try{
                Thread.sleep(2000);

                manager = new ConfigManager(this, "config.yml");
                Config config = new Config(this);
                config.registerConfigs();

                RegisteredPlayersManager manager2 = new RegisteredPlayersManager(this);
                manager2.createRegister();

                ThemeUnzipper themeDownloader = new ThemeUnzipper(this);
                try {
                    themeDownloader.downloadThemes();
                } catch (IOException e) {
                    getLogger().severe("Failed to download themes : " + e.getMessage());
                    e.printStackTrace();
                }
                getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);

                mainFriendsListener = new FriendsMainListener(this);

                try{
                    FriendManager manager = new FriendManager(this);
                    Bukkit.getScheduler().runTaskTimer(this, () -> Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
                            manager.refresh(player.getUniqueId());
                        }
                    }), 0L, 8000L);
                }catch (Exception e){
                    getLogger().severe("Failed to loop for friend differences: " + e);
                    e.printStackTrace();
                    e.getMessage();
                }

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
        }else if (Bukkit.getPluginManager().getPlugin("PartyAndFriends") == null){
            getLogger().severe("Missing Dependency API: Friends API For Party And Friends This is required for.....well friends and parties ig.........get it too");
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void extractFolderFromJar(String folderPath, File destinationFolder) {
        if (!destinationFolder.exists()) {
            destinationFolder.mkdirs(); // Create the destination folder if it doesn't exist
        }

        try {
            getLogger().info("Extracting folder: " + folderPath + " to " + destinationFolder);
            URL jarUrl = getClass().getProtectionDomain().getCodeSource().getLocation();
            JarFile jarFile = new JarFile(new File(jarUrl.toURI()));

            Enumeration<JarEntry> entries = jarFile.entries();
            boolean foundEntries = false;

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();

                // Only process entries that are within the specified folder
                if (entry.getName().startsWith(folderPath + "/") && !entry.isDirectory()) {
                    foundEntries = true; // Mark that we found at least one entry
                    File destFile = new File(destinationFolder, entry.getName().substring(folderPath.length() + 1)); // Adjust index for the slash

                    File parent = destFile.getParentFile();
                    if (!parent.exists()) {
                        parent.mkdirs();  // Create parent directories if needed
                    }

                    // Copy the file from the JAR to the destination
                    try (InputStream is = jarFile.getInputStream(entry);
                         FileOutputStream fos = new FileOutputStream(destFile)) {

                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = is.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
            }

            if (!foundEntries) {
                getLogger().info("No entries found in the JAR file for: " + folderPath);
            }

            jarFile.close();
        } catch (Exception e) {
            getLogger().severe("Failed to extract folder from JAR: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
