package org.rentic.rentic_javaee.service;

import org.rentic.rentic_javaee.model.Conversa;
import org.rentic.rentic_javaee.model.User;
import org.rentic.rentic_javaee.rest.UserRESTService;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Collection;
import java.util.List;


/**
 * Created by Jony Lucena.
 */
@Stateless
@LocalBean
public class UserService {

    @PersistenceContext
    private EntityManager em;

    public User matchPassword(String email, String password) {
        Query q = em.createQuery("select u from User u where u.email=:email");
        q.setParameter("email",email);
        try {
            User u = (User) q.getSingleResult();
            return u.getPassword().equals(password) ? u : null;
        } catch (Exception e) {
            return null;
        }
    }

    public User register(UserRESTService.registre nu) {
        Query q = em.createQuery("select u from User u where u.email=:email");
        q.setParameter("email", nu.email);

        List results = q.getResultList();
        if (results.isEmpty()) {
            User usuari= new User();

            usuari.setEmail(nu.email);
            usuari.setFacebookId(nu.facebookId);
            usuari.setTelefon(nu.telefon);
            usuari.setFotoPerfil(nu.fotoPerfil);
            usuari.setPassword(nu.password);
            usuari.setNomComplet(nu.nomComplet);

            em.persist(usuari);

            return usuari;
        }else
            return null;

    }

    public User getUser(long id) {
        return em.find(User.class, id);
    }

    public Collection<Conversa> getChats(long id) {

        User u=em.find(User.class, id);
        u.getConverses().size();
        return u.getConverses();
    }

}
