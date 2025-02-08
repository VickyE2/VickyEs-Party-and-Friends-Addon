package org.vicky.vicky.utilities.database.dao_s;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.vicky.utilities.DatabaseManager.HibernateUtil;
import org.vicky.vicky.utilities.DBTemplates.Party;
import org.vicky.vicky.utilities.DBTemplates.FriendPlayer;

public class PartyDAO {

    public Party createParty(String partyName, FriendPlayer leader) {
        EntityManager em = HibernateUtil.getEntityManager();
        Party party = new Party(partyName, leader);
        try {
            em.getTransaction().begin();
            em.persist(party);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return party;
    }

    public void addMember(Long partyId, FriendPlayer player) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Party party = em.find(Party.class, partyId);
            if (party != null) {
                party.addMember(player);
                em.merge(party);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void removeMember(Long partyId, FriendPlayer player) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Party party = em.find(Party.class, partyId);
            if (party != null) {
                player.setCurrentParty(null);
                party.removeMember(player);
                em.merge(party);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public Party getPartyById(Long partyId) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.find(Party.class, partyId);
        } finally {
            em.close();
        }
    }
    public void deleteParty(Long partyId) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Party party = em.find(Party.class, partyId);
            if (party != null) {
                for (FriendPlayer player : party.getMembers()) {
                    player.setCurrentParty(null);
                }
                em.remove(party);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw ex;  // Consider handling or logging the exception
        } finally {
            em.close();
        }
    }

    public Party getPartyByMember(FriendPlayer player) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT p FROM Party p JOIN p.members m WHERE m = :player", Party.class)
                    .setParameter("player", player)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }
}
