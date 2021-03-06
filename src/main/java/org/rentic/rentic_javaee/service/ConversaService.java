package org.rentic.rentic_javaee.service;

import org.rentic.rentic_javaee.model.Conversa;
import org.rentic.rentic_javaee.model.Missatge;
import org.rentic.rentic_javaee.model.Objecte;
import org.rentic.rentic_javaee.model.User;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;


/**
 * Created by Jony Lucena.
 */
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
        if(objecte!=null && user!=null && user2!=null) {
            conversa.addUser(user);
            conversa.addUser(user2);
            conversa.setObjecte(objecte);
            conversa.setObjectId(objecte.getId());

            em.persist(conversa);
            user.addConversa(conversa);
            user2.addConversa(conversa);
            objecte.addConversa(conversa);

            Date data = new Date();
            SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");

            Missatge missatge = new Missatge();
            missatge.setUser(user);
            missatge.setMissatge("Hola, vull llogar el teu objecte des del " + dataInici + " fins el " + dataFi);
            missatge.setEnviat(false);
            missatge.setDataHota(formate.format(data));
            missatge.setConversa(conversa);

            em.persist(missatge);
            conversa.addMissatge(missatge);
            em.flush();

            return conversa;
        }else
            return null;
    }

    public void addMissatge(Long chatId, Long userId, String text, boolean b) throws Exception {

        Conversa conversa= em.find(Conversa.class,chatId);
        User user = em.find(User.class, userId);

        Date data= new Date();
        SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");

        Missatge missatge = new Missatge();
        missatge.setUser(user);
        missatge.setMissatge(text);
        missatge.setEnviat(b);
        missatge.setDataHota(formate.format(data));
        missatge.setConversa(conversa);
        em.persist(missatge);

        em.detach(conversa);
        conversa.addMissatge(missatge);

        em.merge(conversa);

    }

    public List<Conversa> getChats(Long id) {
        User u=em.find(User.class, id);
        List<Conversa> c= (List<Conversa>) u.getConverses();
        for(int i = 0; i<c.size(); i++){
            List<Missatge> missatges =(List<Missatge>) obtenirMissatgesNoRebuts(id, c.get(i).getId());
            canviarEstatMissatges(missatges);
        }

        return c;
    }


    public Collection<Missatge> obtenirMissatgesNoEnviats(Long idUsers,Long chatId) {

        Query q=null;
        q = em.createQuery("select mis from  Missatge mis where mis.userId=:idUsers and mis.conversaId=:chatId and mis.enviat=FALSE");
        q.setParameter("idUsers",idUsers);
        q.setParameter("chatId", chatId);

        List<Missatge> m=q.getResultList();

        return m;
    }

    public Collection<Missatge> obtenirMissatgesNoRebuts(Long idUsers,Long chatId ) {

        Query q=null;
        q = em.createQuery("select mis from  Missatge mis where mis.userId<>:idUsers and mis.conversaId=:chatId and mis.enviat=FALSE");
        q.setParameter("idUsers",idUsers);
        q.setParameter("chatId", chatId);

        List<Missatge> m=q.getResultList();

        return m;
    }

    public void canviarEstatMissatges(List<Missatge> missatges) {
        for(int i=0; i<missatges.size();i++){
            Missatge m=em.find(Missatge.class,missatges.get(i).getId());
            m.setEnviat(true);
            em.persist(m);
        }
    }

    public Boolean eliminarConversa(Long idChat, Long userId) {

        Conversa chat = em.find(Conversa.class, idChat);

        if(chat!=null) {
          List<User> u= (List<User>) chat.getUsers();
            if(userId.intValue()==u.get(0).getId().intValue() || userId.intValue()==u.get(1).getId().intValue()){
                em.detach(chat);
                chat.setMissatges(null);
                Query q = em.createQuery("DELETE FROM Missatge mis WHERE mis.conversaId=:idChat");
                q.setParameter("idChat", idChat).executeUpdate();
                em.merge(chat);

                q = em.createQuery("DELETE FROM Conversa chat WHERE chat.id=:idChat");
                q.setParameter("idChat", idChat).executeUpdate();

                return true;
            }
        }
        return false;
    }
}