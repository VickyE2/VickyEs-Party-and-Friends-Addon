package org.vicky.vicky.utilities.DBTemplates;

import jakarta.persistence.*;
import org.vicky.utilities.DatabaseTemplate;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "parties")
public class Party implements DatabaseTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // A friendly name for the party
    @Column(name = "party_name", nullable = false)
    private String name;

    // The party leader is one of the players (a FriendPlayer)
    @ManyToOne
    @JoinColumn(name = "leader_id", nullable = false)
    private FriendPlayer leader;

    // Party members: a many-to-many relationship with FriendPlayer
    @ManyToMany
    @JoinTable(
            name = "party_members",
            joinColumns = @JoinColumn(name = "party_id"),
            inverseJoinColumns = @JoinColumn(name = "player_id")
    )
    private List<FriendPlayer> members = new ArrayList<>();

    // Constructors
    public Party() {
    }

    public Party(String name, FriendPlayer leader) {
        this.name = name;
        this.leader = leader;
        addMember(leader); // Optionally, the leader is automatically a member.
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public FriendPlayer getLeader() {
        return leader;
    }

    public List<FriendPlayer> getMembers() {
        return members;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLeader(FriendPlayer leader) {
        this.leader = leader;
    }

    // Helper methods to manage party members
    public void addMember(FriendPlayer player) {
        if (!members.contains(player)) {
            members.add(player);
        }
    }

    public void removeMember(FriendPlayer player) {
        members.remove(player);
    }
}