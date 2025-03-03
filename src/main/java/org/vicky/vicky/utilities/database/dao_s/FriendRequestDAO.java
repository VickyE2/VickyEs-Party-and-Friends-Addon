package org.vicky.vicky.utilities.database.dao_s;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.vicky.utilities.DatabaseManager.HibernateUtil;
import org.vicky.vicky.utilities.DBTemplates.FriendRequest;
import org.vicky.vicky.utilities.DBTemplates.FriendPlayer;
import org.vicky.vicky.utilities.enums.FriendRequestStatus;

import java.util.List;

public class FriendRequestDAO {

    public void saveRequest(FriendRequest request) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(request);
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

    public void updateRequestStatus(Long requestId, FriendRequestStatus status) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            FriendRequest request = em.find(FriendRequest.class, requestId);
            if (request != null) {
                request.setStatus(status);
                em.merge(request);
                em.getTransaction().commit();
            } else {
                em.getTransaction().rollback();
            }
        } catch (Exception e) {
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public List<FriendRequest> getPendingRequests(FriendPlayer receiver) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            TypedQuery<FriendRequest> query = em.createQuery(
                    "SELECT r FROM FriendRequest r WHERE r.receiver = :receiver AND r.status = :status",
                    FriendRequest.class
            );
            query.setParameter("receiver", receiver);
            query.setParameter("status", FriendRequestStatus.PENDING);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public FriendRequest getRequest(FriendPlayer sender, FriendPlayer receiver) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            TypedQuery<FriendRequest> query = em.createQuery(
                    "SELECT r FROM FriendRequest r WHERE r.sender = :sender AND r.receiver = :receiver",
                    FriendRequest.class
            );
            query.setParameter("sender", sender);
            query.setParameter("receiver", receiver);
            List<FriendRequest> requests = query.getResultList();
            return requests.isEmpty() ? null : requests.get(0);
        } finally {
            em.close();
        }
    }

    public FriendRequest getRequest(Long requestId) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            TypedQuery<FriendRequest> query = em.createQuery(
                    "SELECT r FROM FriendRequest r WHERE r.id = :id",
                    FriendRequest.class
            );
            query.setParameter("id", requestId);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }
}
