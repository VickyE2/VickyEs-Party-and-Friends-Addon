package org.vicky.vicky.utilities.database.dao_s;

import jakarta.persistence.EntityManager;
import org.vicky.utilities.DatabaseManager.HibernateUtil;
import org.vicky.vicky.utilities.DBTemplates.FriendPlayer;

import java.util.List;
import java.util.UUID;

public class FriendDAO {

    public void addFriend(UUID playerId, UUID friendId) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            FriendPlayer player = em.find(FriendPlayer.class, playerId);
            FriendPlayer friend = em.find(FriendPlayer.class, friendId);

            if (player != null && friend != null) {
                player.addFriend(friend);
                em.persist(player);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void removeFriend(UUID playerId, UUID friendId) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            FriendPlayer player = em.find(FriendPlayer.class, playerId);
            FriendPlayer friend = em.find(FriendPlayer.class, friendId);

            if (player != null && friend != null) {
                player.removeFriend(friend);
                em.persist(player);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public List<FriendPlayer> getFriends(UUID playerId) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            FriendPlayer player = em.find(FriendPlayer.class, playerId);
            return player != null ? player.getFriends() : List.of();
        } finally {
            em.close();
        }
    }

    public FriendPlayer getFriendById(String user1Id) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.find(FriendPlayer.class, user1Id);
        }
        finally {
            em.close();
        }
    }
}