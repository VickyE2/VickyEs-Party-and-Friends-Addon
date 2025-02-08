package org.vicky.vicky.utilities.DBTemplates;

import jakarta.persistence.*;
import org.vicky.utilities.DatabaseTemplate;
import org.vicky.vicky.utilities.enums.FriendRequestStatus;

@Entity
@Table(name = "friend_requests")
public class FriendRequest implements DatabaseTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private FriendPlayer sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private FriendPlayer receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendRequestStatus status;

    // Constructors, getters, and setters
    public FriendRequest() {}

    public FriendRequest(FriendPlayer sender, FriendPlayer receiver, FriendRequestStatus status) {
        this.sender = sender;
        this.receiver = receiver;
        this.status = status;
    }

    // Getters and setters
    public Long getId() { return id; }
    public FriendPlayer getSender() { return sender; }
    public FriendPlayer getReceiver() { return receiver; }
    public FriendRequestStatus getStatus() { return status; }
    public void setStatus(FriendRequestStatus status) { this.status = status; }
}

