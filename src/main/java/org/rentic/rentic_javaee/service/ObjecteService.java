package org.rentic.rentic_javaee.service;

import org.rentic.rentic_javaee.model.Objecte;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
@LocalBean
public class ObjecteService {

    @PersistenceContext
    private EntityManager em;

    public boolean inserir(Objecte o) {
        Query q = em.createQuery("select u from Objecte u where u.id=:id");
        q.setParameter("id", o.getId());

        List results = q.getResultList();
        if (results.isEmpty()) {
            em.persist(o);
            return true;
        }else
            return false;

    }

    public Objecte getObjecte(long id) {
        return em.find(Objecte.class, id);
    }

}
