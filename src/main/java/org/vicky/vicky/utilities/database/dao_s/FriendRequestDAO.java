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
        em.getTransaction().begin();
        em.persist(request);
        em.getTransaction().commit();
        em.close();
    }

    public void updateRequestStatus(Long requestId, FriendRequestStatus status) {
        EntityManager em = HibernateUtil.getEntityManager();
        em.getTransaction().begin();
        FriendRequest request = em.find(FriendRequest.class, requestId);
        if (request != null) {
            request.setStatus(status);
            em.getTransaction().commit();
        } else {
            em.getTransaction().rollback();
        }
        em.close();
    }

    public List<FriendRequest> getPendingRequests(FriendPlayer receiver) {
        EntityManager em = HibernateUtil.getEntityManager();
        TypedQuery<FriendRequest> query = em.createQuery(
                "SELECT r FROM FriendRequest r WHERE r.receiver = :receiver AND r.status = :status",
                FriendRequest.class
        );
        query.setParameter("receiver", receiver);
        query.setParameter("status", FriendRequestStatus.PENDING);
        List<FriendRequest> requests = query.getResultList();
        em.close();
        return requests;
    }

    public FriendRequest getRequest(FriendPlayer sender, FriendPlayer receiver) {
        EntityManager em = HibernateUtil.getEntityManager();
        TypedQuery<FriendRequest> query = em.createQuery(
                "SELECT r FROM FriendRequest r WHERE r.sender = :sender AND r.receiver = :receiver",
                FriendRequest.class
        );
        query.setParameter("sender", sender);
        query.setParameter("receiver", receiver);
        List<FriendRequest> requests = query.getResultList();
        em.close();
        return requests.isEmpty() ? null : requests.get(0);
    }

    public FriendRequest getRequest(Long requestId) {
        EntityManager em = HibernateUtil.getEntityManager();
        TypedQuery<FriendRequest> query = em.createQuery(
                "SELECT r FROM FriendRequest r WHERE r.id = :id",
                FriendRequest.class
        );
        query.setParameter("id", requestId);
        FriendRequest request = query.getSingleResult();
        em.close();
        return request;
    }
}