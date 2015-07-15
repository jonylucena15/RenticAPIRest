package org.rentic.rentic_javaee.service;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.rentic.rentic_javaee.model.Lloguer;
import org.rentic.rentic_javaee.model.Objecte;
import org.rentic.rentic_javaee.model.User;
import org.rentic.rentic_javaee.rest.UserRESTService;
import org.rentic.rentic_javaee.util.DateAdapter;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.*;


/**
 * Created by Jony Lucena.
 */
@Stateless
@LocalBean
public class UserService {

    @Inject
    ImageService imageService;

    @PersistenceContext
    private EntityManager em;

    public User matchPassword(String email, String password) {
        Query q = em.createQuery("select u from User u where u.email=:email");
        q.setParameter("email", email);
        try {
            User u = (User) q.getSingleResult();
            return u.getPassword().equals(password) ? u : null;
        } catch (Exception e) {
            return null;
        }
    }

    public User register(UserRESTService.usuari nu) {
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

    public User updateUser(UserRESTService.usuari user, Long userId) throws Exception {

        User u= em.find(User.class, userId);

        em.detach(u);
        if (user.email!=null) u.setEmail(user.email);
        if (user.facebookId!=null)u.setFacebookId(user.facebookId);
        if (user.telefon!=null)u.setTelefon(user.telefon);
        if (user.fotoPerfil!=null)u.setFotoPerfil(user.fotoPerfil);
        if (user.nomComplet!=null) u.setNomComplet(user.nomComplet);
        em.merge(u);

        return u;
    }


    public User updatePassword(String oldPassword, String newPassword, Long userId) throws Exception {

        User u= em.find(User.class, userId);

        if (u.getPassword().equals(oldPassword)) {

            em.detach(u);
            u.setPassword(newPassword);
            em.merge(u);

            return u;
        }

        return null;
    }

    public User getUser(Long id) {
        return em.find(User.class, id);
    }


    public Collection<Lloguer> getLloguersRebuts(Long id, boolean finalitzats) {

        Query q = null;
        List<Objecte> obj =  new ArrayList<Objecte>();
        try {

            q = em.createQuery("select obj from  Objecte obj where obj.userId=:idUsers");
            q.setParameter("idUsers", id);
            obj = q.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        DateAdapter d = new DateAdapter();
        Date dataSistema = new Date();
        String dSistema= null;
        try {
            dSistema = d.marshal(dataSistema);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Lloguer> resultat = new ArrayList<Lloguer>();
        for (int i = 0; i < obj.size(); i++) {
            List<Lloguer> l = (List<Lloguer>) obj.get(i).getLloguers();

            for (int j = 0; j < l.size(); j++) {
                try {

                    if (!finalitzats && (l.get(j).getDataFi().equals(dSistema) || !d.compararDate(l.get(j).getDataFi(), dSistema).equals(l.get(j).getDataFi())))
                        resultat.add(l.get(j));
                    else if (finalitzats && (!l.get(j).getDataFi().equals(dSistema) && d.compararDate(l.get(j).getDataFi(), dSistema).equals(l.get(j).getDataFi())))
                        resultat.add(l.get(j));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        return resultat;
    }

    public User updateImage(MultipartFormDataInput input, Long userId) {

        Map<String, List<InputPart>> formParts = input.getFormDataMap();

        List<InputPart> image = formParts.get("file");

        User u= em.find(User.class, userId);

        em.detach(u);
            u.setFotoPerfil(imageService.uploadUserImage(image));
        em.merge(u);

        return u;

    }

    public Collection<Lloguer> getLloguersRealitzats(Long id, boolean finalitzats) {
        User u= em.find(User.class, id);
        DateAdapter d = new DateAdapter();
        Date dataSistema = new Date();
        String dSistema= null;
        try {
            dSistema = d.marshal(dataSistema);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Lloguer> resultat = new ArrayList<Lloguer>();

        List<Lloguer> aux = (List<Lloguer>) u.getLloguers();

        for (int j = 0; j < aux.size(); j++) {
            try {
                if (!finalitzats && (aux.get(j).getDataFi().equals(dSistema) || !d.compararDate(aux.get(j).getDataFi(), dSistema).equals(aux.get(j).getDataFi())))
                    resultat.add(aux.get(j));
                else if (finalitzats && (!aux.get(j).getDataFi().equals(dSistema) && d.compararDate(aux.get(j).getDataFi(), dSistema).equals(aux.get(j).getDataFi())))
                    resultat.add(aux.get(j));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return resultat;
    }
}
