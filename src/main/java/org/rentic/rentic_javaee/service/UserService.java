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
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
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

    public static String Username = "rentic.authinfo";
    public static String PassWord = "Rentic123zxc";


    String Subject = "Bienvenido a Rentic";

    public void SendMail( User nu) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        String Mensage = "Estimad@"+nu.getNomComplet()+"\n" +
                "\nBienvenid@ a Rentic,\n"+
                "Un lugar donde podrá disfrutar de las mejores ofertas en el alquiler de productos de todo tipo.\n" +
                "\n" +
                "A partir de ahora, además, disfrutará de todas las ventajas y promociones que iremos ofreciendo.\n\n" +

                "Información de su Cuenta en Rentic:\n" +
                "\n" +
                "Nombre: \t"+nu.getNomComplet()+"\n" +
                "Email: \t\t"+nu.getEmail()+"\n" +
                "Password: \t"+nu.getPassword()+"\n" +
                "\n" +
                "\n" +
                "Si tiene cualquier duda o comentario, contacte con nosotros en el email: rentic.authinfo@gmail.com \n" +
                "\n" +
                "Un saludo,\n" +
                "El equipo de Rentic.";



        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(Username, PassWord);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(Username));
            message.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(nu.getEmail()));
            message.setSubject(Subject);
            message.setText(Mensage);

            Transport.send(message);

            message = new MimeMessage(session);
            message.setFrom(new InternetAddress(Username));
            message.addRecipient(Message.RecipientType.TO,
                    new InternetAddress("rentic.authinfo@gmail.com"));
            message.setSubject("Nou client");
            Mensage = "Tenim un nou client!\n\n"+"Informació del client:\n"+"Nombre: \t"+nu.getNomComplet()+"\nEmail:"+" \t\t"+nu.getEmail()+"\n\n";
            message.setText(Mensage);

            Transport.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
