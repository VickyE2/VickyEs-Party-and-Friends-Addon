package org.vicky.vicky.utilities.DBTemplates;

import jakarta.persistence.*;
import org.vicky.utilities.DatabaseTemplate;

import java.time.Instant;

@Entity
@Table(name = "friendships")
public class Friendship implements DatabaseTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("id")
    private FriendPlayer friendA;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("id")
    private FriendPlayer friendB;

    @Column(name = "added_time", nullable = false)
    private Instant addedTime;

    public Friendship() {
    }

    public Friendship(FriendPlayer playerA, FriendPlayer playerB, Instant time) {
        addedTime = time;
        friendA = playerA;
        friendB = playerB;
    }

    public FriendPlayer getFriendA() {
        return friendA;
    }

    public FriendPlayer getFriendB() {
        return friendB;
    }

    public String getId() {
        return id;
    }

    public Instant getAddedTime() {
        return addedTime;
    }
}
