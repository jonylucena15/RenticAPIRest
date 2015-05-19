package org.rentic.rentic_javaee.service;

import org.apache.james.mime4j.field.datetime.DateTime;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.rentic.rentic_javaee.model.*;
import org.rentic.rentic_javaee.util.DateAdapter;
import org.rentic.rentic_javaee.util.DistanceComparator;
import org.rentic.rentic_javaee.util.FromJSONObject;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Stateless
@LocalBean
public class ConversaService {

    @PersistenceContext
    private EntityManager em;


    public Conversa addConversa(String dataInici, String dataFi, Long idObjecte, Long idArrendador, Long userId) throws Exception {

        Conversa conversa = new Conversa();
        User user = em.find(User.class, userId);
        Objecte objecte = em.find(Objecte.class, idObjecte);
        User user2 = em.find(User.class, idArrendador);

        Collection<User> usuaris = new ArrayList<>();
        usuaris.add(user);
        usuaris.add(user2);

        conversa.setObjecte(objecte);
        conversa.setObjectId(objecte.getId());
        conversa.setUsuaris(usuaris);

        em.persist(conversa);
        em.detach(conversa);

        Date data= new Date();
        SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");

        Missatge missatge = new Missatge();
        missatge.setUser(user);
        missatge.setUserId(userId);
        missatge.setMissatge("Un usuari vol llogar el teu objecte des del " + dataInici + " fins el " + dataFi);
        missatge.setEnviat(false);
        missatge.setDataHota(formate.format(data));
        missatge.setConversa(conversa);
        missatge.setConversaId(conversa.getId());

        em.persist(missatge);
        conversa.addMissatge(missatge);
        em.merge(conversa);



        return conversa;
    }

    public void addMissatge(String chatId, Long userId, String text, boolean b) throws Exception {

        Conversa conversa= em.find(Conversa.class,chatId);
        User user = em.find(User.class, userId);

        Date data= new Date();
        SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");

        Missatge missatge = new Missatge();
        missatge.setUser(user);
        missatge.setUserId(userId);
        missatge.setMissatge(text);
        missatge.setEnviat(b);
        missatge.setDataHota(formate.format(data));
        missatge.setConversa(conversa);
        em.persist(missatge);

        em.detach(conversa);
        conversa.addMissatge(missatge);


        em.merge(conversa);

    }


}