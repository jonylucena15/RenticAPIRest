package org.rentic.rentic_javaee.service;

import org.rentic.rentic_javaee.model.User;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
@LocalBean
public class UserService {

    @PersistenceContext
    private EntityManager em;

    public User matchPassword(String username, String password) {
        Query q = em.createQuery("select u from User u where u.username=:username");
        q.setParameter("username", username);
        try {
            User u = (User) q.getSingleResult();
            return u.getPassword().equals(password) ? u : null;
        } catch (Exception e) {
            return null;
        }
    }

    public int register(User nu) {
        Query q = em.createQuery("select u from User u where u.email=:email");
        q.setParameter("email", nu.getEmail());

        List results = q.getResultList();
        if (results.isEmpty()) {
            q = em.createQuery("select u from User u where u.username=:username");
            q.setParameter("username", nu.getUsername());
            results = q.getResultList();

            if (results.isEmpty()) {
                em.persist(nu);
                return 0;
            }else
                return 1;
        }else
            return 2;

    }

    public User getUser(long id) {
        return em.find(User.class, id);
    }

}
