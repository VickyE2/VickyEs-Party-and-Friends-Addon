package org.vicky.vicky.utilities.database.dao_s;

import org.vicky.utilities.DatabaseManager.HibernateUtil;
import org.vicky.vicky.utilities.DBTemplates.FriendPlayer;

import jakarta.persistence.EntityManager;
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
                // Use merge for updating an existing managed entity
                em.merge(player);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
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
                em.merge(player);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
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
        } finally {
            em.close();
        }
    }
}
