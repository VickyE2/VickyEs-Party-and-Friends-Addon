package org.vicky.vicky.utils;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.v_utls.utilities.ANSIColor;
import org.v_utls.utilities.ConfigManager;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ThemeUnzipper {

    private static JavaPlugin plugin;

    public List<String> requiredGuis = new ArrayList<>();
    public List<String> requiredButtons = new ArrayList<>();
    private final ConfigManager guiManager;
    private final ConfigManager buttonManager;

    private final ThemeStorer storer;
    public Map<String, Integer> buttonVerticalSplits = new HashMap<>();

    public String[] allowedImageFormats = {".png", ".jpeg", ".jpg"};

    public ThemeUnzipper(JavaPlugin plugin) {
        ThemeUnzipper.plugin = plugin;
        File configFile = new File(plugin.getDataFolder().getParentFile().getAbsolutePath(), "/ItemsAdder/contents/vicky_themes/configs/");

        if (!configFile.exists()) {
            configFile.mkdir();
        }

        try {
            Thread.sleep(700);
            this.guiManager = new ConfigManager(plugin);
            guiManager.createPathedConfig(configFile + "/guis.yml");
            guiManager.loadConfigValues();
            guiManager.setConfigValue("info", "namespace", "vicky_themes", null);

            this.buttonManager = new ConfigManager(plugin);
            buttonManager.createPathedConfig(configFile + "/items.yml");
            buttonManager.loadConfigValues();
            buttonManager.setConfigValue("info", "namespace", "vicky_themes", null);

            this.storer = new ThemeStorer(plugin);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void setRequiredImages() {
        requiredGuis = Arrays.asList(
                "friends_gui_change_status_panel",
                "friends_gui_f_profile_panel",
                "friends_gui_main_panel",
                "friends_gui_message_panel",
                "friends_gui_message_request_panel",
                "friends_gui_request_panel",
                "party_gui_main_panel",
                "party_gui_member_panel"
        );

        requiredButtons = Arrays.asList(
                "accept_button_long",
                "accept_button_small",
                "add_favourite_button",
                "back_down_button",
                "back_up_button",
                "cancel_button_small",
                "demote_button",
                "friend_request_empty",
                "friend_request_has",
                "invite_button",
                "jump_button",
                "kick_long_button",
                "leave_button",
                "left_arrow",
                "message_request_empty",
                "message_request_has",
                "party_long_button",
                "promote_button",
                "reject_button_long",
                "remove_favourite_button",
                "right_arrow",
                "settings_rounded_left",
                "settings_rounded",
                "slider_0",
                "slider_1",
                "slider_2",
                "slider_3",
                "sort",
                "trashcan_button",
                "chat_box"
        );

        buttonVerticalSplits.put("accept_button_long", 3);
        buttonVerticalSplits.put("accept_button_small", 0);
        buttonVerticalSplits.put("add_favourite_button", 0);
        buttonVerticalSplits.put("back_down_button", 3);
        buttonVerticalSplits.put("back_up_button", 3);
        buttonVerticalSplits.put("cancel_button_small", 0);
        buttonVerticalSplits.put("chat_box", 7);
        buttonVerticalSplits.put("demote_button", 3);
        buttonVerticalSplits.put("friend_request_empty", 0);
        buttonVerticalSplits.put("friend_request_has", 0);
        buttonVerticalSplits.put("invite_button", 3);
        buttonVerticalSplits.put("jump_button", 3);
        buttonVerticalSplits.put("kick_long_button", 2);
        buttonVerticalSplits.put("leave_button", 0);
        buttonVerticalSplits.put("left_arrow", 0);
        buttonVerticalSplits.put("message_request_empty", 0);
        buttonVerticalSplits.put("message_request_has", 0);
        buttonVerticalSplits.put("party_long_button", 2);
        buttonVerticalSplits.put("promote_button", 3);
        buttonVerticalSplits.put("reject_button_long", 3);
        buttonVerticalSplits.put("remove_favourite_button", 0);
        buttonVerticalSplits.put("right_arrow", 0);
        buttonVerticalSplits.put("settings_rounded_left", 0);
        buttonVerticalSplits.put("settings_rounded", 0);
        buttonVerticalSplits.put("slider_0", 0);
        buttonVerticalSplits.put("slider_1", 0);
        buttonVerticalSplits.put("slider_2", 0);
        buttonVerticalSplits.put("slider_3", 0);
        buttonVerticalSplits.put("sort", 0);
        buttonVerticalSplits.put("trashcan_button", 0);
    }


    public void downloadThemes() throws IOException {

        setRequiredImages();
        Path themesPath = Paths.get(plugin.getDataFolder().getAbsolutePath(), "themes");
        List<Path> zipFiles = getAllZipFiles(themesPath.toString());


        if (!zipFiles.isEmpty()) {
            for (Path zipPath : zipFiles) {
                boolean filenowFound = false;
                boolean fileFound;
                ConfigManager manager = new ConfigManager(plugin);
                try (ZipFile zipFile = new ZipFile(zipPath.toFile())) {
                    ZipEntry requiredEntry = zipFile.getEntry("themes.yml");
                    if (requiredEntry == null || requiredEntry.isDirectory()) {
                        plugin.getLogger().warning("Required file, " + requiredEntry + ", not found in the Theme Pack: " + zipFile.getName());
                        break;
                    }
                }
                manager.loadConfigFromZip(zipPath, "themes.yml");

                try {
                    ConfigurationNode themesNode = manager.rootNode.node("themes");
                    if (!themesNode.virtual()) {
                        for (ConfigurationNode themeNode : themesNode.childrenMap().values()) {
                            String themeName = themeNode.node("theme_name").getString();
                            String themeId = themeNode.node("theme_id").getString();
                            ConfigurationNode guiFolder = themeNode.node("gui_folder");
                            ConfigurationNode buttonsFolder = themeNode.node("buttons_folder");
                            ConfigurationNode textsFolder = themeNode.node("texts_folder");
                            plugin.getLogger().info("Loading theme with id: " + themeId);
                            if (!storer.isRegisteredTheme(themeName)) {

                                if (guiFolder.virtual() || guiFolder.isNull()) {
                                    plugin.getLogger().warning("The theme with id: " + themeId + " fails to provide a gui_folder field");
                                    break;
                                }
                                if (buttonsFolder.virtual() || guiFolder.isNull()) {
                                    plugin.getLogger().warning("The theme with id: " + themeId + " fails to provide a buttons_folder field");
                                    break;
                                }
                                if (textsFolder.virtual() || guiFolder.isNull()) {
                                    plugin.getLogger().warning("The theme with id: " + themeId + " fails to provide a texts_folder field");
                                    break;
                                }
                                for (String currentGui : requiredGuis) {
                                    try (ZipFile zip = new ZipFile(zipPath.toFile())) {
                                        fileFound = false;
                                        Path outputFolderPath = Path.of(plugin.getDataFolder().getParentFile().getAbsolutePath() + "/ItemsAdder/contents/vicky_themes/textures/gui/" + themeId);
                                        for (String format : allowedImageFormats) {
                                            String file = guiFolder.getString() + "/" + currentGui + "_" + themeId + format;
                                            String imagefile = currentGui + "_" + themeId + format;
                                            ZipEntry entry = zip.getEntry(file);
                                            if (entry != null && !entry.isDirectory()) {
                                                fileFound = true;
                                                filenowFound = true;
                                                File outputFile = new File(outputFolderPath.toString(), imagefile);
                                                File parentDir = outputFile.getParentFile();
                                                if (!parentDir.exists()) {
                                                    if (parentDir.mkdirs()) {
                                                        plugin.getLogger().info("Created directories: " + parentDir.getAbsolutePath());
                                                    } else {
                                                        plugin.getLogger().warning("Failed to create directories: " + parentDir.getAbsolutePath());
                                                        continue; // Skip this file if directories cannot be created
                                                    }
                                                }

                                                // Save the image to the output folder
                                                try (InputStream inputStream = zip.getInputStream(entry);
                                                     FileOutputStream fos = new FileOutputStream(outputFile)) {

                                                    byte[] buffer = new byte[1024];
                                                    int bytesRead;
                                                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                                                        fos.write(buffer, 0, bytesRead);
                                                    }

                                                } catch (IOException e) {
                                                    plugin.getLogger().warning("Error saving the image: " + e.getMessage());

                                                    break;
                                                }
                                                int heightdiff = getHeightdiff(currentGui);

                                                Path imagePath = Path.of(outputFile.getPath());// Replace with your image path

                                                // Load the image
                                                Image image = Toolkit.getDefaultToolkit().getImage(imagePath.toString());

                                                // Create a MediaTracker to track the loading of the image
                                                MediaTracker tracker = new MediaTracker(new Canvas());
                                                tracker.addImage(image, 0);

                                                guiManager.setConfigValue("font_images", currentGui + "_" + themeId + ".path", "gui/" + themeId + "/" + currentGui + "_" + themeId + format, null);
                                                guiManager.setConfigValue("font_images", currentGui + "_" + themeId + ".suggest_in_command", false, null);
                                                guiManager.setConfigValue("font_images", currentGui + "_" + themeId + ".y_position", heightdiff, null);

                                                break;
                                            }
                                        }
                                        if (!fileFound) {
                                            filenowFound = false;
                                            for (String format : allowedImageFormats) {
                                                plugin.getLogger().warning("No file found for any allowed format. Using provided value from config if any.");
                                                ConfigurationNode guisNode = themeNode.node("guis");
                                                String guiValue = guisNode.getString(currentGui);
                                                String file = manager.getStringValue(guiFolder.getString() + "/" + guiValue + format);
                                                String imagefile = currentGui + "_" + themeId + format;
                                                plugin.getLogger().info("Checking for file: " + file);
                                                ZipEntry entry = zip.getEntry(file);
                                                if (entry != null && !entry.isDirectory()) {
                                                    filenowFound = true;
                                                    if (!guisNode.virtual()) {
                                                        File outputFile = new File(outputFolderPath.toString(), imagefile);
                                                        File parentDir = outputFile.getParentFile();
                                                        if (!parentDir.exists()) {
                                                            if (parentDir.mkdirs()) {
                                                                plugin.getLogger().info("Created directories: " + parentDir.getAbsolutePath());
                                                            } else {
                                                                plugin.getLogger().warning("Failed to create directories: " + parentDir.getAbsolutePath());
                                                                continue; // Skip this file if directories cannot be created
                                                            }
                                                        }

                                                        try (InputStream inputStream = zip.getInputStream(entry);
                                                             FileOutputStream fos = new FileOutputStream(outputFile)) {

                                                            byte[] buffer = new byte[1024];
                                                            int bytesRead;
                                                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                                                fos.write(buffer, 0, bytesRead);
                                                            }

                                                        } catch (IOException e) {
                                                            plugin.getLogger().warning("Error saving the image: " + e.getMessage());

                                                            break;
                                                        }

                                                        int heightdiff = getHeightdiff(currentGui);

                                                        Path imagePath = Path.of(outputFile.getPath());// Replace with your image path

                                                        // Load the image
                                                        Image image = Toolkit.getDefaultToolkit().getImage(imagePath.toString());

                                                        // Create a MediaTracker to track the loading of the image
                                                        MediaTracker tracker = new MediaTracker(new Canvas());
                                                        tracker.addImage(image, 0);

                                                        guiManager.setConfigValue("font_images", currentGui + "_" + themeId + ".path", "gui/" + themeId + "/" + currentGui + "_" + themeId + format, null);
                                                        guiManager.setConfigValue("font_images", currentGui + "_" + themeId + ".suggest_in_command", false, null);
                                                        guiManager.setConfigValue("font_images", currentGui + "_" + themeId + ".y_position", heightdiff, null);

                                                        plugin.getLogger().info("file_now_found: " + filenowFound);
                                                        break;
                                                    } else {
                                                        plugin.getLogger().warning("No Guis found for theme: " + themeId);
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    } catch (IOException e) {
                                        plugin.getLogger().severe("Failed to get zip file: " + e.getMessage());
                                        e.printStackTrace();
                                        break;
                                    }

                                    if (!filenowFound || !fileFound) {
                                        plugin.getLogger().severe("The theme with id: " + themeId + " fails to provide a gui for '" + currentGui + "_" + themeId + "'");
                                        break;
                                    }
                                }

                                for (String currentButton : requiredButtons) {
                                    try (ZipFile zip = new ZipFile(zipPath.toFile())) {
                                        fileFound = false;
                                        filenowFound = false;
                                        Path outputFolderPath = Path.of(plugin.getDataFolder().getParentFile().getAbsolutePath() + "/ItemsAdder/contents/vicky_themes/textures/items/" + themeId);
                                        for (String format : allowedImageFormats) {
                                            String file = buttonsFolder.getString() + "/" + currentButton + "_" + themeId + format;
                                            String outputImage = buttonsFolder.getString() + "/" + currentButton + "_" + themeId + format;
                                            String unsplitoutputImage = currentButton + "_" + themeId + format;
                                            ZipEntry entry = zip.getEntry(file);
                                            if (entry != null && !entry.isDirectory()) {
                                                fileFound = true;
                                                filenowFound = true;
                                                try {
                                                    Files.createDirectories(outputFolderPath); // This will create all necessary parent directories
                                                } catch (IOException e) {
                                                    plugin.getLogger().severe("Failed to create directory: " + outputFolderPath.toString() + " - " + e.getMessage());
                                                    return; // Exit if the directory can't be created
                                                }

                                                File outputFile = new File(outputFolderPath.toString(), outputImage);
                                                File parentDir = outputFile.getParentFile();
                                                if (!parentDir.exists()) {
                                                    if (parentDir.mkdirs()) {
                                                        plugin.getLogger().info("Created directories: " + parentDir.getAbsolutePath());
                                                    } else {
                                                        plugin.getLogger().warning("Failed to create directories: " + parentDir.getAbsolutePath());
                                                        continue; // Skip this file if directories cannot be created
                                                    }
                                                }

                                                int verticalSplit = buttonVerticalSplits.getOrDefault(currentButton, 0);
                                                if (verticalSplit == 0) {
                                                    // Save the image to the output folder
                                                    File unsplitoutputFile = new File(outputFolderPath.toString(), unsplitoutputImage);
                                                    try (InputStream inputStream = zip.getInputStream(entry);
                                                         FileOutputStream fos = new FileOutputStream(unsplitoutputFile)) {

                                                        byte[] buffer = new byte[1024];
                                                        int bytesRead;
                                                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                                                            fos.write(buffer, 0, bytesRead);
                                                        }
                                                    } catch (IOException e) {
                                                        plugin.getLogger().warning("Error saving the image: " + e.getMessage());
                                                    }

                                                    buttonManager.setConfigValue("items", currentButton + "_" + themeId + ".display_name", currentButton, null);
                                                    buttonManager.setConfigValue("items", currentButton + "_" + themeId + ".resource.material", "PAPER", null);
                                                    List<String> textures = new ArrayList<>();
                                                    textures.add("items/" + themeId + "/" + currentButton + "_" + themeId + format);
                                                    buttonManager.setConfigValue("items", currentButton + "_" + themeId + ".resource.generate", true, null);
                                                    buttonManager.setListConfigValue("items." + currentButton + "_" + themeId + ".resource.textures", textures);

                                                } else if (!currentButton.equalsIgnoreCase("chat_box")) {

                                                    try (InputStream inputStream = zip.getInputStream(entry);
                                                         FileOutputStream fos = new FileOutputStream(outputFile)) {

                                                        byte[] buffer = new byte[1024];
                                                        int bytesRead;
                                                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                                                            fos.write(buffer, 0, bytesRead);
                                                        }

                                                    } catch (IOException e) {
                                                        plugin.getLogger().warning("Error saving the image: " + e.getMessage());
                                                    }

                                                    List<String> images = ImageDivider(null, outputFile.getAbsolutePath(), 0, verticalSplit, currentButton + "_" + themeId, outputFolderPath.toString(), false);
                                                    for (String image : images) {
                                                        buttonManager.setConfigValue("items", image + ".display_name", currentButton, null);
                                                        buttonManager.setConfigValue("items", image + ".resource.material", "PAPER", null);
                                                        List<String> textures = new ArrayList<>();
                                                        textures.add("items/" + themeId + "/" + image + ".png");
                                                        buttonManager.setConfigValue("items", image + ".resource.generate", true, null);
                                                        buttonManager.setListConfigValue("items." + image + ".resource.textures", textures);
                                                    }
                                                } else {
                                                    List<File> files = new ArrayList<>();
                                                    List<String> divided_images;
                                                    Enumeration<? extends ZipEntry> images = zip.entries();

                                                    try (InputStream inputStream = zip.getInputStream(entry);
                                                         FileOutputStream fos = new FileOutputStream(outputFile)) {

                                                        byte[] buffer = new byte[1024];
                                                        int bytesRead;
                                                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                                                            fos.write(buffer, 0, bytesRead);
                                                        }

                                                    } catch (IOException e) {
                                                        plugin.getLogger().warning("Error saving the image: " + e.getMessage());
                                                    }

                                                    while (images.hasMoreElements()) {
                                                        ZipEntry entry1 = images.nextElement();
                                                        String filename = entry1.getName();
                                                        if (filename.startsWith(Objects.requireNonNull(textsFolder.getString())) &&
                                                                (filename.endsWith(".png") || filename.endsWith(".jpeg") || filename.endsWith(".jpg"))) {

                                                            // Save the image to a temp file
                                                            File tempFile = new File(outputFolderPath + "/buttons", new File(filename).getName());
                                                            try (InputStream inputStream = zip.getInputStream(entry1);
                                                                 FileOutputStream fos = new FileOutputStream(tempFile)) {

                                                                byte[] buffer = new byte[1024];
                                                                int bytesRead;
                                                                while ((bytesRead = inputStream.read(buffer)) != -1) {
                                                                    fos.write(buffer, 0, bytesRead);
                                                                }
                                                                files.add(tempFile);  // Add the temp file to the list of files
                                                            } catch (IOException e) {
                                                                plugin.getLogger().warning("Error saving the image: " + e.getMessage());
                                                            }
                                                        }
                                                    }

                                                    // Now process each extracted image file using the ImageDivider method
                                                    for (File currentFile : files) {
                                                        // Call ImageDivider for each file, pass the vertical split amount
                                                        String filename = currentFile.getName().replace(".png", "");
                                                        divided_images = ImageDivider(currentFile.getAbsolutePath(), outputFile.getAbsolutePath(), 0, verticalSplit, filename, outputFolderPath.toString(), true);
                                                        // Update configuration for each split image
                                                        for (String image : divided_images) {
                                                            // Example of setting the display name, material, and texture
                                                            buttonManager.setConfigValue("items", filename + ".display_name", filename, null);
                                                            buttonManager.setConfigValue("items", filename + ".resource.material", "PAPER", null);

                                                            List<String> textures = new ArrayList<>();
                                                            textures.add("items/" + themeId + "/" + image + ".png");

                                                            // Save the texture list into config
                                                            buttonManager.setConfigValue("items", filename + ".resource.generate", true, null);
                                                            buttonManager.setListConfigValue("items." + filename + ".resource.textures", textures);
                                                        }
                                                    }
                                                }
                                                break;
                                            }
                                        }
                                        if (!fileFound) {
                                            for (String format : allowedImageFormats) {
                                                plugin.getLogger().warning("No file found for any allowed format. Using provided value from config if any.");
                                                ConfigurationNode buttonsNode = themeNode.node("buttons");
                                                String buttonsValue;
                                                String file = "";
                                                String imagefile = "";

                                                if (!buttonsNode.empty()) {
                                                    // Retrieve the correct button node using the key (currentButton)
                                                    ConfigurationNode buttonNode = buttonsNode.node(currentButton);
                                                    // Check if the node exists and has a value
                                                    if (!buttonNode.isNull()) {
                                                        buttonsValue = buttonNode.getString();
                                                        file = buttonsFolder.getString() + "/" + buttonsValue + format;
                                                        imagefile = buttonsValue + "_" + themeId + format;

                                                    } else {
                                                        plugin.getLogger().warning("Button node for " + currentButton + " is null or does not exist.");
                                                    }
                                                } else {
                                                    plugin.getLogger().warning("Buttons node is empty or does not exist!");
                                                }
                                                plugin.getLogger().info("Checking for file: " + file);
                                                ZipEntry entry = zip.getEntry(file);
                                                if (entry != null && !entry.isDirectory()) {
                                                    filenowFound = true;
                                                    if (!buttonsNode.virtual()) {
                                                        File outputFile = new File(outputFolderPath.toString(), imagefile);
                                                        File parentDir = outputFile.getParentFile();
                                                        if (!parentDir.exists()) {
                                                            if (parentDir.mkdirs()) {
                                                                plugin.getLogger().info("Created directories: " + parentDir.getAbsolutePath());
                                                            } else {
                                                                plugin.getLogger().warning("Failed to create directories: " + parentDir.getAbsolutePath());
                                                                continue; // Skip this file if directories cannot be created
                                                            }
                                                        }

                                                        try {
                                                            Files.createDirectories(outputFolderPath); // This will create all necessary parent directories
                                                        } catch (IOException e) {
                                                            plugin.getLogger().severe("Failed to create directory: " + outputFolderPath.toString() + " - " + e.getMessage());
                                                            return; // Exit if the directory can't be created
                                                        }

                                                        outputFile = new File(outputFolderPath.toString(), file);
                                                        File unsplitoutputFile = new File(outputFolderPath.toString(), imagefile);
                                                        parentDir = outputFile.getParentFile();
                                                        if (!parentDir.exists()) {
                                                            if (parentDir.mkdirs()) {
                                                                plugin.getLogger().info("Created directories: " + parentDir.getAbsolutePath());
                                                            } else {
                                                                plugin.getLogger().warning("Failed to create directories: " + parentDir.getAbsolutePath());
                                                                continue; // Skip this file if directories cannot be created
                                                            }
                                                        }

                                                        int verticalSplit = buttonVerticalSplits.getOrDefault(currentButton, 0);
                                                        if (verticalSplit == 0) {
                                                            // Save the image to the output folder
                                                            try (InputStream inputStream = zip.getInputStream(entry);
                                                                 FileOutputStream fos = new FileOutputStream(unsplitoutputFile)) {

                                                                byte[] buffer = new byte[1024];
                                                                int bytesRead;
                                                                while ((bytesRead = inputStream.read(buffer)) != -1) {
                                                                    fos.write(buffer, 0, bytesRead);
                                                                }

                                                            } catch (IOException e) {
                                                                plugin.getLogger().warning("Error saving the image: " + e.getMessage());
                                                            }

                                                            buttonManager.setConfigValue("items", currentButton + "_" + themeId + ".display_name", currentButton, null);
                                                            buttonManager.setConfigValue("items", currentButton + "_" + themeId + ".resource.material", "PAPER", null);
                                                            List<String> textures = new ArrayList<>();
                                                            textures.add("items/" + themeId + "/" + currentButton + "_" + themeId + format);
                                                            buttonManager.setConfigValue("items", currentButton + "_" + themeId + ".resource.generate", true, null);
                                                            buttonManager.setListConfigValue("items." + currentButton + "_" + themeId + ".resource.textures", textures);

                                                        } else if (!currentButton.equalsIgnoreCase("chat_box")) {

                                                            try (InputStream inputStream = zip.getInputStream(entry);
                                                                 FileOutputStream fos = new FileOutputStream(outputFile)) {

                                                                byte[] buffer = new byte[1024];
                                                                int bytesRead;
                                                                while ((bytesRead = inputStream.read(buffer)) != -1) {
                                                                    fos.write(buffer, 0, bytesRead);
                                                                }

                                                            } catch (IOException e) {
                                                                plugin.getLogger().warning("Error saving the image: " + e.getMessage());
                                                            }

                                                            List<String> images = ImageDivider(null, outputFile.getAbsolutePath(), 0, verticalSplit, currentButton + "_" + themeId, outputFolderPath.toString(), false);
                                                            for (String image : images) {
                                                                buttonManager.setConfigValue("items", image + ".display_name", currentButton, null);
                                                                buttonManager.setConfigValue("items", image + ".resource.material", "PAPER", null);
                                                                List<String> textures = new ArrayList<>();
                                                                textures.add("items/" + themeId + "/" + image + ".png");
                                                                buttonManager.setConfigValue("items", image + ".resource.generate", true, null);
                                                                buttonManager.setListConfigValue("items." + image + ".resource.textures", textures);
                                                            }
                                                        } else {
                                                            List<File> files = new ArrayList<>();
                                                            List<String> divided_images;
                                                            Enumeration<? extends ZipEntry> images = zip.entries();

                                                            try (InputStream inputStream = zip.getInputStream(entry);
                                                                 FileOutputStream fos = new FileOutputStream(outputFile)) {

                                                                byte[] buffer = new byte[1024];
                                                                int bytesRead;
                                                                while ((bytesRead = inputStream.read(buffer)) != -1) {
                                                                    fos.write(buffer, 0, bytesRead);
                                                                }
                                                            } catch (IOException e) {
                                                                plugin.getLogger().warning("Error saving the image: " + e.getMessage());
                                                            }

                                                            String fileName = "";

                                                            while (images.hasMoreElements()) {
                                                                ZipEntry entry1 = images.nextElement();
                                                                String filename = entry1.getName();
                                                                if (filename.startsWith(Objects.requireNonNull(textsFolder.getString())) &&
                                                                        (filename.endsWith(".png") || filename.endsWith(".jpeg") || filename.endsWith(".jpg"))) {

                                                                    // Save the image to a temp file
                                                                    File tempFile = new File(outputFolderPath + "/buttons", new File(filename).getName());
                                                                    try (InputStream inputStream = zip.getInputStream(entry1);
                                                                         FileOutputStream fos = new FileOutputStream(tempFile)) {

                                                                        byte[] buffer = new byte[1024];
                                                                        int bytesRead;
                                                                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                                                                            fos.write(buffer, 0, bytesRead);
                                                                        }
                                                                        files.add(tempFile);  // Add the temp file to the list of files
                                                                    } catch (IOException e) {
                                                                        plugin.getLogger().warning("Error saving the image: " + e.getMessage());
                                                                    }
                                                                }
                                                            }

                                                            // Now process each extracted image file using the ImageDivider method
                                                            for (File currentFile : files) {
                                                                // Call ImageDivider for each file, pass the vertical split amount
                                                                String filename = currentFile.getName().replace(".png", "");
                                                                divided_images = ImageDivider(currentFile.getAbsolutePath(), outputFile.getAbsolutePath(), verticalSplit, 0, filename, outputFolderPath.toString(), true);

                                                                // Update configuration for each split image
                                                                for (String image : divided_images) {
                                                                    // Example of setting the display name, material, and texture
                                                                    buttonManager.setConfigValue("items", filename + ".display_name", filename, null);
                                                                    buttonManager.setConfigValue("items", filename + ".resource.material", "PAPER", null);

                                                                    List<String> textures = new ArrayList<>();
                                                                    textures.add("items/" + themeId + "/" + image + ".png");

                                                                    // Save the texture list into config
                                                                    buttonManager.setConfigValue("items", filename + ".resource.generate", true, null);
                                                                    buttonManager.setListConfigValue("items." + filename + ".resource.textures", textures);
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    } catch (IOException e) {
                                        plugin.getLogger().severe("Failed to get zip file: " + e.getMessage());
                                        e.printStackTrace();
                                        break;

                                    }

                                    if (!filenowFound) {
                                        plugin.getLogger().severe("The theme with id: " + themeId + " fails to provide a button for '" + currentButton + "_" + themeId + "'");
                                        break;
                                    }
                                }
                                Path directory = Path.of(plugin.getDataFolder().getParentFile().getAbsolutePath() + "/ItemsAdder/contents/vicky_themes/textures/items/" + themeId + "/buttons");
                                Files.walk(directory)
                                        .sorted(Comparator.reverseOrder())
                                        .map(Path::toFile)
                                        .forEach(File::delete);

                                plugin.getLogger().info(ANSIColor.colorize("Theme with id " + ANSIColor.colorize(themeId, ANSIColor.YELLOW_BOLD) + " has successfully been loaded", ANSIColor.PURPLE_BOLD));

                                storer.addTheme(themeId, themeName);
                            }else{
                                break;
                            }
                        }
                    } else {
                        plugin.getLogger().warning("No themes found in the configuration.");
                    }
                } catch (Exception e) {
                    plugin.getLogger().severe("Error while loading themes: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

    }

    public List<String> ImageDivider(String topImageMain, String bottomImageMain, int b_row, int b_col, String mName, String o_path, boolean overlay) {
        List<String> images = new ArrayList<>();
        try {
            // Load the bottom layer image
            BufferedImage bottomImage = ImageIO.read(new File(bottomImageMain));
            // Load the top layer image if overlaying
            BufferedImage topImage = overlay ? ImageIO.read(new File(topImageMain)) : null;
            // Create the output directory if it doesn't exist
            File outputDirectory = new File(o_path);
            if (!outputDirectory.exists()) {
                outputDirectory.mkdirs(); // Ensure directory exists
            }
            // Handle case when no splitting is needed (b_row == 0 && b_col == 0)
            if (b_row == 0 && b_col == 0) {
                // If no split is required, save the entire bottom image directly
                File outputFile = new File(outputDirectory, String.format("%s.png", mName));
                ImageIO.write(bottomImage, "PNG", outputFile);
                images.add(mName);  // Add the unsplit image name
            } else {
                // Split the image into rows and columns as needed
                int sectionWidth = b_col > 0 ? bottomImage.getWidth() / b_col : bottomImage.getWidth();
                int sectionHeight = b_row > 0 ? bottomImage.getHeight() / b_row : bottomImage.getHeight();
                // Loop through each row and column to split the image
                for (int col = 0; col < Math.max(b_col, 1); col++) {
                    // Calculate the x and y position for the current section
                    int x = col * sectionWidth;
                    // Get the current section from the bottom image
                    BufferedImage bottomSection = bottomImage.getSubimage(x, 0, sectionWidth, sectionHeight);

                    // Merge the bottom section with the top image if overlaying
                    Graphics2D g = bottomSection.createGraphics();
                    g.drawImage(bottomSection, 0, 0, null);
                    if (overlay && topImage != null) {
                        BufferedImage topSection = topImage.getSubimage(x, 0, sectionWidth, sectionHeight);
                        g.drawImage(topSection, 0, 0, null);
                    }
                    g.dispose();

                    // Create the output filename with the format "mainName_row_col.png"
                    String outputFileName = String.format("%s_%d.png", mName, col);
                    File outputFile = new File(outputDirectory, outputFileName);
                    // Add the image name (without extension) to the list
                    images.add(String.format("%s_%d", mName, col));
                    // Save the split image
                    ImageIO.write(bottomSection, "PNG", outputFile);
                }
            }
        } catch (IOException e) {
            System.err.println("Error during image processing: " + e.getMessage());
            e.printStackTrace();
        }
        return images;
    }



    private static int getHeightdiff(String currentGui) {
        int heightdiff = 0;
        if (Objects.equals(currentGui, "friends_gui_change_status_panel")) {
            heightdiff = 22; //
        }
        if (Objects.equals(currentGui, "friends_gui_f_profile_panel")) {
            heightdiff = 22;
        }
        if (Objects.equals(currentGui, "friends_gui_main_panel")) {
            heightdiff = 22;
        }
        if (Objects.equals(currentGui, "friends_gui_message_panel")) {
            heightdiff = 22; //
        }
        if (Objects.equals(currentGui, "friends_gui_message_request_panel")) {
            heightdiff = 22;
        }
        if (Objects.equals(currentGui, "friends_gui_request_panel")) {
            heightdiff = 22;
        }
        if (Objects.equals(currentGui, "party_gui_main_panel")) {
            heightdiff = 22;
        }
        if (Objects.equals(currentGui, "party_gui_member_panel")) {
            heightdiff = 22;
        }
        return heightdiff;
    }

    @NotNull
    private static File getFile(String currentGui, Path outputFolderPath) {
        if (!outputFolderPath.toFile().exists()) {
            outputFolderPath.toFile().mkdir();
        }
// Prepare the output file
        File outputFolder = new File(outputFolderPath.toString());
        if (!outputFolder.exists()) {
            outputFolder.mkdirs(); // Create output folder if it doesn't exist
        }
        File outputFile = new File(outputFolder, currentGui);
        return outputFile;
    }

    public static List<Path> getAllZipFiles(String directoryPath) throws IOException {
        Path dirPath = Paths.get(directoryPath);

        // Ensure the directory exists
        if (!Files.exists(dirPath) || !Files.isDirectory(dirPath)) {
            throw new IllegalArgumentException("Invalid directory path: " + directoryPath);
        }

        // Collect all ZIP files in the directory
        return Files.walk(dirPath)
                .filter(path -> path.toString().toLowerCase().endsWith(".zip"))
                .collect(Collectors.toList());
    }


    public static List<String> readImagesFromZip(String zipFilePath, String requiredFileName,
                                                 List<String> targetFolders, List<String> imageExtensions) throws IOException {
        Path zipPath = Paths.get(zipFilePath);
        List<String> imagePaths = new ArrayList<>();

        // Open the ZIP file
        try (ZipFile zipFile = new ZipFile(zipPath.toFile())) {
            // Check if the required file exists in the base directory
            ZipEntry requiredEntry = zipFile.getEntry(requiredFileName);
            if (requiredEntry == null || requiredEntry.isDirectory()) {
                return imagePaths; // Return empty list if required file is not found
            }

            // Iterate through the entries in the ZIP file
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String entryName = entry.getName();

                // Check if the entry is an image in the specified folders
                if (!entry.isDirectory() && isImageFile(entryName, imageExtensions)) {
                    for (String folder : targetFolders) {
                        if (entryName.startsWith(folder)) {
                            // Add the image path to the list
                            imagePaths.add(entryName);
                        }
                    }
                }
            }
        }

        return imagePaths;
    }

    // Check if the entry is an image file based on the given extensions
    private static boolean isImageFile(String entryName, List<String> imageExtensions) {
        for (String extension : imageExtensions) {
            if (entryName.toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }
}