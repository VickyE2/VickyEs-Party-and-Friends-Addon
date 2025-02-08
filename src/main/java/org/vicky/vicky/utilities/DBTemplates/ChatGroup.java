package org.vicky.vicky.utilities.DBTemplates;

import jakarta.persistence.*;
import org.vicky.utilities.DatabaseTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "chat_groups")
public class ChatGroup implements DatabaseTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @ManyToMany
    @JoinTable(
            name = "chat_group_members",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "player_id")
    )
    private List<FriendPlayer> members = new ArrayList<>();

    public ChatGroup() {}

    public ChatGroup(String name) {
        this.name = name;
    }

    public void addMember(FriendPlayer player) {
        members.add(player);
    }

    public boolean isMember(UUID playerId) {
        return members.stream().anyMatch(p -> p.getId().equals(playerId));
    }

    // Getters & Setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public List<FriendPlayer> getMembers() { return members; }
}