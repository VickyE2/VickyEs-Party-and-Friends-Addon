package org.vicky.vicky.utilities.database.dao_s;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.vicky.utilities.DatabaseManager.HibernateUtil;
import org.vicky.vicky.utilities.DBTemplates.FriendPlayer;
import org.vicky.vicky.utilities.DBTemplates.MessageEntity;

import java.util.List;

public class MessageDAO {
    public void saveMessage(MessageEntity message) {
        EntityManager em = HibernateUtil.getEntityManager();
        em.getTransaction().begin();
        em.persist(message);
        em.getTransaction().commit();
        em.close();
    }

    public List<MessageEntity> getMessagesBetween(FriendPlayer user1, FriendPlayer user2) {
        EntityManager em = HibernateUtil.getEntityManager();
        TypedQuery<MessageEntity> query = em.createQuery(
                "SELECT m FROM Message m WHERE (m.sender = :user1 AND m.receiver = :user2) " +
                        "OR (m.sender = :user2 AND m.receiver = :user1) ORDER BY m.timestamp",
                MessageEntity.class
        );
        query.setParameter("user1", user1);
        query.setParameter("user2", user2);
        List<MessageEntity> messages = query.getResultList();
        em.close();
        return messages;
    }

    public long countUnreadMessages(FriendPlayer receiver) {
        EntityManager em = HibernateUtil.getEntityManager();
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(m) FROM MessageEntity m WHERE m.receiver = :receiver AND m.read = false",
                Long.class
        );
        query.setParameter("receiver", receiver);
        long unreadCount = query.getSingleResult();
        em.close();
        return unreadCount;
    }

    public void markMessagesAsRead(FriendPlayer receiver, FriendPlayer sender) {
        EntityManager em = HibernateUtil.getEntityManager();
        em.getTransaction().begin();
        TypedQuery<MessageEntity> query = em.createQuery(
                "SELECT m FROM MessageEntity m WHERE m.receiver = :receiver AND m.sender = :sender AND m.read = false",
                MessageEntity.class
        );
        query.setParameter("receiver", receiver);
        query.setParameter("sender", sender);
        List<MessageEntity> unreadMessages = query.getResultList();
        for (MessageEntity message : unreadMessages) {
            message.setRead(true);
            em.merge(message);
        }
        em.getTransaction().commit();
        em.close();
    }
}
