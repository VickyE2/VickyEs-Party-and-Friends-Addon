package org.vicky.vicky.utilities.DBTemplates;

import jakarta.persistence.*;
import org.vicky.utilities.DatabaseManager.templates.DatabasePlayer;
import org.vicky.utilities.DatabaseTemplate;
import org.vicky.vicky.utilities.database.dao_s.FriendshipDAO;
import org.vicky.vicky.utilities.enums.FriendSortingStyles;
import org.vicky.vicky.utilities.enums.PossibleStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.vicky.vicky.global.Utils.manager;
import static org.vicky.vicky.utilities.enums.FriendSortingStyles.*;

@Entity
public class FriendPlayer extends DatabasePlayer implements DatabaseTemplate {
    @OneToMany(mappedBy = "friendA", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Friendship> friendshipsAsA = new ArrayList<>();

    @OneToMany(mappedBy = "friendB", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Friendship> friendshipsAsB = new ArrayList<>();
    @Column(name = "allow_notifications")
    private Boolean allowNotifications;

    @Column(name = "friend_requests")
    private Boolean friendRequests = true;

    @Column(name = "allow_private_chat")
    private Boolean allowPrivateChat;

    @Column(name = "allow_teleporting_to")
    private Boolean allowTeleportingTo;

    @Column(name = "hide_online_time")
    private Boolean hideOnlineTime;

    @Column(name = "allow_party_invites")
    private Boolean allowPartyInvites;

    @ManyToOne
    @JoinColumn(name = "party_id")
    private Party currentParty;

    @Column(name = "status")
    private String status;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private PossibleStatus currentState;
    @Column(name = "last_online")
    private Instant lastOnlineTime;

    @Column(name = "chat_name_color")
    private String chatNameColor;

    @Enumerated(EnumType.STRING)
    @Column(name = "sorting_style")
    private FriendSortingStyles sortingStyle = ADDED_TIME;

    // Relationships (Friends and Messaging)
    @JoinTable(
            name = "friends",
            joinColumns = @JoinColumn(name = "player_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<FriendPlayer> friends = new ArrayList<>();

    @OneToMany(mappedBy = "sender", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<MessageEntity> sentMessages = new ArrayList<>();

    @OneToMany(mappedBy = "receiver", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<MessageEntity> receivedMessages = new ArrayList<>();

    // Constructors
    public FriendPlayer() {
    }

    @PrePersist
    public void prePersist() {
        if (allowNotifications == null) {
            allowNotifications = manager.getBooleanValue("PlayerDefaults.AllowNotifications");
        }
        if (friendRequests == null) {
            friendRequests = manager.getBooleanValue("PlayerDefaults.FriendRequests");
        }
        if (allowPrivateChat == null) {
            allowPrivateChat = manager.getBooleanValue("PlayerDefaults.AllowPrivateChats");
        }
        if (allowTeleportingTo == null) {
            allowTeleportingTo = manager.getBooleanValue("PlayerDefaults.AllowJumping");
        }
        if (hideOnlineTime == null) {
            hideOnlineTime = manager.getBooleanValue("PlayerDefaults.HideOnlineTime");
        }
        if (allowPartyInvites == null) {
            allowPartyInvites = manager.getBooleanValue("PlayerDefaults.AllowPartyInvites");
        }
        if (status == null) {
            status = manager.getStringValue("PlayerDefaults.DefaultStatus");
        }
        if (chatNameColor == null) {
            chatNameColor = manager.getStringValue("PlayerDefaults.ChatNameColor");
        }
        if (sortingStyle == null) {
            sortingStyle = FriendSortingStyles.ADDED_TIME; // Default enum value
        }
        if (lastOnlineTime == null || lastOnlineTime.getEpochSecond() < Instant.now().getEpochSecond()) {
            lastOnlineTime = Instant.now(); // Set to current time if not provided
        }
        if (currentState == null) {
            currentState = PossibleStatus.ONLINE; // Set to current time if not provided
        }
    }


    public Boolean getAllowNotifications() {
        return allowNotifications;
    }

    public void setAllowNotifications(Boolean allowNotifications) {
        this.allowNotifications = allowNotifications;
    }

    public Boolean getFriendRequests() {
        return friendRequests;
    }

    public void setFriendRequests(Boolean friendRequests) {
        this.friendRequests = friendRequests;
    }

    public Boolean getAllowPrivateChat() {
        return allowPrivateChat;
    }

    public void setAllowPrivateChat(Boolean allowPrivateChat) {
        this.allowPrivateChat = allowPrivateChat;
    }

    public Boolean getAllowTeleportingTo() {
        return allowTeleportingTo;
    }

    public void setAllowTeleportingTo(Boolean allowTeleportingTo) {
        this.allowTeleportingTo = allowTeleportingTo;
    }

    public Boolean getHideOnlineTime() {
        return hideOnlineTime;
    }

    public void setHideOnlineTime(Boolean hideOnlineTime) {
        this.hideOnlineTime = hideOnlineTime;
    }

    public Boolean getAllowPartyInvites() {
        return allowPartyInvites;
    }

    public void setAllowPartyInvites(Boolean allowPartyInvites) {
        this.allowPartyInvites = allowPartyInvites;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getChatNameColor() {
        return chatNameColor;
    }

    public void setChatNameColor(String chatNameColor) {
        this.chatNameColor = chatNameColor;
    }

    public FriendSortingStyles getSortingStyle() {
        return sortingStyle;
    }

    public void setSortingStyle(FriendSortingStyles sortingStyle) {
        this.sortingStyle = sortingStyle;
    }

    // Getters and setters:
    public Instant getLastOnlineTime() {
        if (hideOnlineTime)
            return Instant.ofEpochSecond(11225);
        return lastOnlineTime;
    }

    public void setLastOnlineTime(Instant lastOnlineTime) {
        this.lastOnlineTime = lastOnlineTime;
    }

    // Methods for managing friends
    public List<FriendPlayer> getFriends() {
        List<FriendPlayer> friends = new ArrayList<>(this.friends); // Assuming you have a list of friends stored

        Comparator<FriendPlayer> comparator = switch (sortingStyle) {
            case LAST_ONLINE, LAST_ONLINE_DESCENDING -> Comparator.comparing(FriendPlayer::getLastOnlineTime);
            case ADDED_TIME, ADDED_TIME_DESCENDING -> {
                List<Friendship> context = new ArrayList<>();
                for (FriendPlayer friendPlayer : friends) {
                    Friendship friendship = new FriendshipDAO().of(this, friendPlayer); // Retrieve the Friendship object
                    context.add(friendship);
                }

                Comparator<Friendship> friendshipComparator = Comparator.comparing(Friendship::getAddedTime);
                if (sortingStyle.isDescending()) {
                    friendshipComparator = friendshipComparator.reversed();
                }

                // Sorting friends based on the sorted friendships
                List<FriendPlayer> contextFriends = new ArrayList<>();
                context.sort(friendshipComparator); // Sort the list of Friendship objects
                for (Friendship friendship : context) {
                    // Add the friend players in the correct order based on sorted friendships
                    if (friendship.getFriendA() != this) {
                        contextFriends.add(friendship.getFriendA());
                    } else {
                        contextFriends.add(friendship.getFriendB());
                    }
                }

                // Returning the comparator for the FriendPlayer objects
                yield Comparator.comparing((FriendPlayer player) -> {
                    int index = contextFriends.indexOf(player);
                    return index == -1 ? Integer.MAX_VALUE : index;
                });
            }
            case RANK, RANK_DESCENDING -> Comparator.comparing(FriendPlayer::getRank);
        };


        if (this.sortingStyle.isDescending()) {
            comparator = comparator.reversed();
        }

        friends.sort(comparator);
        return friends;
    }
    public CompletableFuture<List<FriendPlayer>> getFriendsAsync() {
        return CompletableFuture.supplyAsync(this::getFriends);
    }

    public boolean isAFriend(UUID id) {
        return friends.stream().anyMatch(f -> f.getId().equals(id));
    }

    public boolean isAFriend(DatabasePlayer player) {
        return friends.stream().anyMatch(f -> f.getId().equals(player.getId()));
    }

    // Methods for messaging
    public void sendMessage(FriendPlayer receiver, String content) {
        MessageEntity message = new MessageEntity(this, receiver, content);
        sentMessages.add(message);
        receiver.receivedMessages.add(message);
    }

    public List<MessageEntity> getMessagesWith(FriendPlayer friend) {
        List<MessageEntity> conversation = new ArrayList<>();
        for (MessageEntity msg : sentMessages) {
            if (msg.getReceiver() != null && msg.getReceiver().equals(friend)) {
                conversation.add(msg);
            }
        }
        for (MessageEntity msg : receivedMessages) {
            if (msg.getSender().equals(friend)) {
                conversation.add(msg);
            }
        }
        conversation.sort((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()));
        return conversation;
    }

    public void addFriend(FriendPlayer friend) {
        Friendship friendship = new Friendship(this, friend, Instant.now());
        friendshipsAsA.add(friendship);
        friend.getFriendshipsAsB().add(friendship);
        friends.add(friend);
    }

    public void removeFriend(FriendPlayer friend) {
        new FriendshipDAO().delete(this, friend);
        friends.remove(friend);
    }

    public List<Friendship> getFriendshipsAsA() {
        return friendshipsAsA;
    }

    public List<Friendship> getFriendshipsAsB() {
        return friendshipsAsB;
    }

    // Optional: method for group chat participation (if ChatGroup is defined)
    public void joinGroup(ChatGroup group) {
        group.addMember(this);
    }

    public Party getCurrentParty() {
        return currentParty;
    }

    public void setCurrentParty(Party party) {
        this.currentParty = party;
    }

    public boolean isInParty() {
        return currentParty != null;
    }

    public boolean hasMessages() {
        return false;
    }
}
