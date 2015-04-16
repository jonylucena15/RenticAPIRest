package org.rentic.rentic_javaee.service;

import javafx.util.Pair;
import org.rentic.rentic_javaee.model.Coordenades;
import org.rentic.rentic_javaee.model.Disponibilitat;
import org.rentic.rentic_javaee.model.Objecte;
import org.rentic.rentic_javaee.model.User;
import org.rentic.rentic_javaee.rest.ObjecteRESTService;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Stateless
@LocalBean
public class ObjecteService {

    @PersistenceContext
    private EntityManager em;
/*
    public boolean insert(Objecte o) {
        Query q = em.createQuery("select u from Objecte u where u.id=:id");
        q.setParameter("id", o.getId());

        List results = q.getResultList();
        if (results.isEmpty()) {
            em.persist(o);
            return true;
        }else
            return false;

    }
  */
    public Objecte addObjecte(ObjecteRESTService.objecte i, Long userId) throws Exception {

        Coordenades coor = new Coordenades(i.latitud,i.longitud);



        User user = em.find(User.class, userId);


        Objecte o=new Objecte();
        o.setUser(user);
        o.setNom(i.nom);
        o.setCoordenades(coor);
        o.setDescripcio(i.descripcio);
        o.setPreu(i.preu);
        o.setTags(i.tags);
        o.setDispCapDeSetmana(i.dispCapDeSetmana);
        o.setDispEntreSetmana(i.dispEntreDeSetmana);


        for(Pair<String,String> a : i.dispRang) {
            String pattern = "dd-MM-yyyy";
            SimpleDateFormat fmt = new SimpleDateFormat(pattern);

            Date inici = fmt.parse(a.getKey());
            Date fi = fmt.parse(a.getValue());

            Disponibilitat disp=new Disponibilitat(inici,fi);
            o.addDisponibilitat(disp);
        }


        user.addObjecte(o);

        em.persist(o);
        em.flush();
        return o;
    }
/*
    public Objecte getObjecte(long id) {
        return em.find(Objecte.class, id);
    }
*/
}
