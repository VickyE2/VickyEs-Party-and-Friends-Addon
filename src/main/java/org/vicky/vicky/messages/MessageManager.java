package org.vicky.vicky.messages;

import com.diffplug.spotless.maven.java.Java;
import dev.lone.LoneLibs.S;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.configurate.ConfigurationNode;
import org.v_utls.utilities.JsonConfigManager;
import org.v_utls.utilities.XmlConfigManager;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MessageManager {

    private JsonConfigManager jmanager;
    private XmlConfigManager xmlmanager;
    private JavaPlugin plugin;
    private static final DateTimeFormatter dateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public MessageManager(JavaPlugin plugin){
        this.plugin = plugin;
        this.jmanager = new JsonConfigManager(plugin);
        this.xmlmanager = new XmlConfigManager(plugin);
    }

    public void createChatLog(OfflinePlayer player1, OfflinePlayer player2){

        String UUID1 = player1.getUniqueId().toString();
        String UUID2 = player2.getUniqueId().toString();
        String USER1 = player1.getName();
        String USER2 = player2.getName();

        String id = UUID1 + "_" + UUID2;

        // Define the participant details
        Map<String, Object> participant1 = new HashMap<>();
        participant1.put("userId", UUID1);
        participant1.put("username", USER1);
        participant1.put("lastSeen", "");

        Map<String, Object> participant2 = new HashMap<>();
        participant2.put("userId", UUID2);
        participant2.put("username", USER2);
        participant2.put("lastSeen", "");

        // Add participants to a list
        List<Map<String, Object>> participants = new ArrayList<>();
        participants.add(participant1);
        participants.add(participant2);

        jmanager.createConfig("messages/" + id.toLowerCase());
        jmanager.setConfigValue("chatId", id.toLowerCase());
        jmanager.setConfigValue("participants", participants);
        jmanager.loadConfigValues();

        updateLastSeen(UUID1);
        updateLastSeen(UUID2);

    }

    public void loadChatLog(OfflinePlayer player1, OfflinePlayer player2){

        String UUID1 = player1.getUniqueId().toString();
        String UUID2 = player2.getUniqueId().toString();

        String id = UUID1 + "_" + UUID2;
        jmanager.createConfig("messages/" + id.toLowerCase());
        jmanager.loadConfigValues();
    }

    public List<Map<String, Object>> getMessages(int amount){

        Object messagesObj = jmanager.getConfigValue("messages");

        if (messagesObj instanceof List<?>) {
            List<Map<String, Object>> messages = (List<Map<String, Object>>) messagesObj;

            // Determine the starting index for the last 'n' messages
            int startIndex = Math.max(messages.size() - amount, 0); // Ensures no negative index

            // Return the sublist of the last 'n' messages
            return messages.subList(startIndex, messages.size());
        }

        // Return an empty list if there are no messages
        return new java.util.ArrayList<>();
    }

    public void addMessage(String senderId, String content) {
        // Retrieve the messages list
        Object messagesObj = jmanager.getConfigValue("messages");

        List<Map<String, Object>> messages;
        if (messagesObj instanceof List<?>) {
            messages = (List<Map<String, Object>>) messagesObj;
        } else {
            messages = new java.util.ArrayList<>();
        }

        // Create a new message map
        Map<String, Object> newMessage = new HashMap<>();
        newMessage.put("messageId", getNextMessageId());
        newMessage.put("senderId", senderId);
        newMessage.put("timestamp", getCurrentTimestamp());
        newMessage.put("content", content);
        newMessage.put("isRead", false);

        // Add the new message to the list
        messages.add(newMessage);

        // Save the updated messages list back to the config
        jmanager.setConfigValue("messages", messages);
    }



    public boolean hasValidMessageRequest(OfflinePlayer player) {
        // Load the player's message requests
        xmlmanager.createConfig("Users/" + player.getUniqueId() + "/message_requests.xml");
        xmlmanager.loadConfigValues();

        // Retrieve the requests from the config
        Object requestsObj = xmlmanager.getConfigValue("message.requests.request");

        // If there are no requests, return false
        if (requestsObj == null || !(requestsObj instanceof List<?>)) {
            return false;
        }

        List<Map<String, Object>> requests = (List<Map<String, Object>>) requestsObj;

        // Loop through the requests to check if any are valid (not expired and not accepted)
        for (Map<String, Object> request : requests) {
            boolean isAccepted = Boolean.parseBoolean((String) request.get("isAccepted"));
            boolean isExpired = Boolean.parseBoolean((String) request.get("isExpired"));

            // Check if the request is not accepted and not expired
            if (!isAccepted && !isExpired) {
                return true;
            }
        }

        // No valid request found
        return false;
    }


    public void addMessageRequest(OfflinePlayer player1, OfflinePlayer player2, int duration, Date dateCreated) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        String formattedDate = sdf.format(dateCreated);

        // Create the message request for both players
        createAndSaveMessageRequest(player1, player2, duration, formattedDate);
        createAndSaveMessageRequest(player2, player1, duration, formattedDate);
    }

    private void createAndSaveMessageRequest(OfflinePlayer sender, OfflinePlayer recipient, int duration, String formattedDate) {
        // Define the request ID
        String requestId = sender.getUniqueId() + "_to_" + recipient.getUniqueId();

        // Create or load the config file for the sender
        xmlmanager.createConfig("Users/" + sender.getUniqueId() + "/message_requests.xml");

        // Attributes for the message request
        Map<String, String> attributes = new HashMap<>();
        attributes.put("requestID", requestId);
        attributes.put("isAccepted", Boolean.toString(false));
        attributes.put("isExpired", Boolean.toString(false));
        attributes.put("requestDuration", Integer.toString(duration));

        // Add the request to the config with the formatted date as a comment
        xmlmanager.setConfigValue("message.requests.request", "", "Request was made on: " + formattedDate, attributes);

        // Save the config
        xmlmanager.saveConfig();

        // Optionally, print the request details
        printMessageRequest(requestId, formattedDate);
    }



    // Method to retrieve a message request and print its details
    public void printMessageRequest(String requestId, String dateCreated) {

        plugin.getLogger().info("Message Request ID: " + requestId);
        plugin.getLogger().info("Created on: " + dateCreated);
    }

    public String getNextMessageId() {
        // Retrieve the messages list
        Object messagesObj = jmanager.getConfigValue("messages");

        if (messagesObj instanceof List<?>) {
            List<Map<String, Object>> messages = (List<Map<String, Object>>) messagesObj;

            // Get the last message ID if there are any messages
            if (!messages.isEmpty()) {
                String lastMessageId = (String) messages.get(messages.size() - 1).get("messageId");

                // Extract the numeric part of the last message ID (e.g., "001" from "msg001")
                String numericPart = lastMessageId.replaceAll("\\D+", ""); // Removes non-numeric characters

                if (!numericPart.isEmpty()) {
                    // Increment the numeric part
                    int nextId = Integer.parseInt(numericPart) + 1;

                    // Construct the new message ID (e.g., "msg002")
                    return "msg_" + String.format("%03d", nextId); // Pads with leading zeros
                }
            }
        }

        // Default to msg001 if there are no previous messages
        return "msg_001";
    }

    public String getLastMessageId(){
        Object messagesObj = jmanager.getConfigValue("messages");

        if (messagesObj instanceof List<?>) {
            List<Map<String, Object>> messages = (List<Map<String, Object>>) messagesObj;

            // Get the last message ID if there are any messages
            if (!messages.isEmpty()) {
                return (String) messages.get(messages.size() - 1).get("messageId");
            }
        }

        return "msg_001";
    }

    public void markMessageAsRead(String messageId) {
        // Retrieve the messages list
        Object messagesObj = jmanager.getConfigValue("messages");

        if (messagesObj instanceof List<?>) {
            List<Map<String, Object>> messages = (List<Map<String, Object>>) messagesObj;

            for (Map<String, Object> message : messages) {
                if (message.get("messageId").equals(messageId)) {
                    // Mark the message as read
                    message.put("isRead", true);
                    break;
                }
            }

            // Save the updated messages list back to the config
            jmanager.setConfigValue("messages", messages);
        }
    }


    public void updateLastSeen(String userId) {
        // Retrieve the participants list
        Object participantsObj = jmanager.getConfigValue("participants");

        if (participantsObj instanceof List<?>) {
            List<Map<String, Object>> participants = (List<Map<String, Object>>) participantsObj;

            for (Map<String, Object> participant : participants) {
                if (participant.get("userId").equals(userId)) {
                    // Update lastSeen with the current timestamp
                    participant.put("lastSeen", getCurrentTimestamp());

                }
            }

            // Save the updated participants list back to the config
            jmanager.setConfigValue("participants", participants);
        }
    }

    public String getCurrentTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        return now.format(dateTimeFormatter);
    }
}
