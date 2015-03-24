package org.rentic.rentic_javaee.service;

import org.rentic.rentic_javaee.model.User;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

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

    public User register(String username, String nomComplet, String email, String telefon, String facebookId, String fotoPerfil, String password) {

        Query q = em.createQuery("select u from User u where u.email=:email");
        q.setParameter("email", email);
        User u = (User) q.getSingleResult();
        if (u != null) {
            return null;
        }

        q = em.createQuery("select u from User u where u.username=:username");
        q.setParameter("username", username);
        u = (User) q.getSingleResult();
        if (u != null) {
            return null;
        }

        User nu = new User();
        nu.setUsername(username);
        nu.setEmail(email);
        nu.setFacebookId(facebookId);
        nu.setFotoPerfil(fotoPerfil);
        nu.setNomComplet(nomComplet);
        nu.setTelefon(telefon);
        nu.setPassword(password);
        em.persist(nu);
        return nu;
    }

    public User getUser(long id) {
        return em.find(User.class, id);
    }

}
