package org.rentic.rentic_javaee.service;

import org.rentic.rentic_javaee.model.Conversa;
import org.rentic.rentic_javaee.model.Missatge;
import org.rentic.rentic_javaee.model.User;
import org.rentic.rentic_javaee.rest.UserRESTService;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Jony Lucena.
 */
@Stateless
@LocalBean
public class UserService {
    @Inject
    ConversaService conversaService;

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

    public User getUser(Long id) {
        return em.find(User.class, id);
    }

    public List<Conversa> getChats(Long id) {

        User u=em.find(User.class, id);
        List<Conversa> c= (List<Conversa>) u.getConverses();
        List<Conversa> auxC= new ArrayList<>();
        for(int i = 0; i<c.size(); i++){
            List<Missatge> missatges =(List<Missatge>) conversaService.obtenirMissatgesNoRebuts(id, c.get(i).getId());
            Conversa con=new Conversa();
            con.setId(c.get(i).getId());
            con.setObjecte(c.get(i).getObjecte());
            con.setUsers(c.get(i).getUsers());
            con.setMissatges(missatges);
            auxC.add(con);
            conversaService.canviarEstatMissatges(missatges);
        }

        return auxC;
    }

}
