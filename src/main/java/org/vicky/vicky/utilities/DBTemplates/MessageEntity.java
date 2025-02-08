package org.vicky.vicky.utilities.DBTemplates;

import jakarta.persistence.*;
import org.vicky.utilities.DatabaseTemplate;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class MessageEntity implements DatabaseTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private FriendPlayer sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private FriendPlayer receiver; // Null if it's a group message.

    @ManyToOne
    @JoinColumn(name = "group_id")
    private ChatGroup group; // Null if it's a private message.

    @Column(nullable = false)
    private boolean read = false; // New field to track read status

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public MessageEntity() {}

    public MessageEntity(FriendPlayer sender, FriendPlayer receiver, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.read = false; // Initialize as unread
    }

    public MessageEntity(FriendPlayer sender, ChatGroup group, String content) {
        this.sender = sender;
        this.group = group;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }

    // Getters & Setters
    public Long getId() { return id; }
    public FriendPlayer getSender() { return sender; }
    public FriendPlayer getReceiver() { return receiver; }
    public ChatGroup getGroup() { return group; }
    public String getContent() { return content; }
    public LocalDateTime getTimestamp() { return timestamp; }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isRead() {
        return read;
    }
}
