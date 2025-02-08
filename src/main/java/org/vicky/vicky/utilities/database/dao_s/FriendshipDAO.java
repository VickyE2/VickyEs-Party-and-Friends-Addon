package org.vicky.vicky.utilities.database.dao_s;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.vicky.utilities.DatabaseManager.HibernateUtil;
import org.vicky.vicky.utilities.DBTemplates.FriendPlayer;
import org.vicky.vicky.utilities.DBTemplates.Friendship;
import java.util.List;
import java.util.Optional;

public class FriendshipDAO {

    public Optional<Friendship> getById(Long id) {
        EntityManager em = HibernateUtil.getEntityManager();
        Friendship friendship = em.find(Friendship.class, id);
        em.close();
        return Optional.ofNullable(friendship);
    }

    public List<Friendship> getAll() {
        EntityManager em = HibernateUtil.getEntityManager();
        TypedQuery<Friendship> query = em.createQuery("SELECT f FROM Friendship f", Friendship.class);
        List<Friendship> friendships = query.getResultList();
        em.close();
        return friendships;
    }

    public void save(Friendship friendship) {
        EntityManager em = HibernateUtil.getEntityManager();
        em.getTransaction().begin();
        em.persist(friendship);
        em.getTransaction().commit();
        em.close();
    }

    public void delete(Friendship friendship) {
        EntityManager em = HibernateUtil.getEntityManager();
        em.getTransaction().begin();
        em.remove(em.contains(friendship) ? friendship : em.merge(friendship));
        em.getTransaction().commit();
        em.close();
    }

    public void delete(FriendPlayer friendA, FriendPlayer friendB) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            TypedQuery<Friendship> query = em.createQuery(
                    "SELECT f FROM Friendship f WHERE (f.friendA = :friendA AND f.friendB = :friendB) " +
                            "OR (f.friendA = :friendB AND f.friendB = :friendA)", Friendship.class);
            query.setParameter("playerAId", friendA.getId().toString());
            query.setParameter("playerBId", friendB.getId().toString());
            Friendship friendship = query.getSingleResult();
            if (friendship != null) {
                em.remove(friendship);
                em.getTransaction().commit();
            }
        }
        finally {
            em.close();
        }
    }

    public Friendship of(FriendPlayer friendA, FriendPlayer friendB) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            TypedQuery<Friendship> query = em.createQuery(
                    "SELECT f FROM Friendship f WHERE (f.friendA = :friendA AND f.friendB = :friendB) " +
                            "OR (f.friendA = :friendB AND f.friendB = :friendA)", Friendship.class);
            query.setParameter("playerAId", friendA.getId().toString());
            query.setParameter("playerBId", friendB.getId().toString());
            Friendship friendship = query.getSingleResult();
            if (friendship != null) {
                return friendship;
            }
        }
        finally {
            em.close();
        }

        return null;
    }

    public List<Friendship> getFriendshipsOfPlayer(Long playerId) {
        EntityManager em = HibernateUtil.getEntityManager();
        TypedQuery<Friendship> query = em.createQuery(
                "SELECT f FROM Friendship f WHERE f.friendA.id = :playerId OR f.friendB.id = :playerId",
                Friendship.class
        );
        query.setParameter("playerId", playerId);
        List<Friendship> friendships = query.getResultList();
        em.close();
        return friendships;
    }
}

