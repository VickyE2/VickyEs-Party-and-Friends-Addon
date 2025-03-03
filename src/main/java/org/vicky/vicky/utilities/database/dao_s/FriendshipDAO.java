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
        try {
            Friendship friendship = em.find(Friendship.class, id);
            return Optional.ofNullable(friendship);
        } finally {
            em.close();
        }
    }

    public List<Friendship> getAll() {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            TypedQuery<Friendship> query = em.createQuery("SELECT f FROM Friendship f", Friendship.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public void save(Friendship friendship) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(friendship);
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

    public void delete(Friendship friendship) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.remove(em.contains(friendship) ? friendship : em.merge(friendship));
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

    public void delete(FriendPlayer friendA, FriendPlayer friendB) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            TypedQuery<Friendship> query = em.createQuery(
                    "SELECT f FROM Friendship f WHERE (f.friendA.id = :friendAId AND f.friendB.id = :friendBId) " +
                            "OR (f.friendA.id = :friendBId AND f.friendB.id = :friendAId)", Friendship.class);
            query.setParameter("friendAId", friendA.getId().toString());
            query.setParameter("friendBId", friendB.getId().toString());
            Friendship friendship = query.getSingleResult();
            if (friendship != null) {
                em.remove(friendship);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public Friendship of(FriendPlayer friendA, FriendPlayer friendB) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            TypedQuery<Friendship> query = em.createQuery(
                    "SELECT f FROM Friendship f WHERE (f.friendA.id = :friendAId AND f.friendB.id = :friendBId) " +
                            "OR (f.friendA.id = :friendBId AND f.friendB.id = :friendAId)", Friendship.class);
            query.setParameter("friendAId", friendA.getId().toString());
            query.setParameter("friendBId", friendB.getId().toString());
            return query.getSingleResult();
        } catch (Exception e) {
            // Return null if no result or any exception occurs
            return null;
        } finally {
            em.close();
        }
    }

    public List<Friendship> getFriendshipsOfPlayer(Long playerId) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            TypedQuery<Friendship> query = em.createQuery(
                    "SELECT f FROM Friendship f WHERE f.friendA.id = :playerId OR f.friendB.id = :playerId",
                    Friendship.class
            );
            query.setParameter("playerId", playerId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
