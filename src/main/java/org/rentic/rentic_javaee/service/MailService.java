package org.rentic.rentic_javaee.service;

import org.rentic.rentic_javaee.model.Lloguer;
import org.rentic.rentic_javaee.model.User;
import org.rentic.rentic_javaee.util.DateAdapter;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Created by jlucena on 22/07/2015.
 */

@Stateless
@LocalBean
public class MailService {


    private static String Username = "rentic.authinfo";
    private static String PassWord = "Rentic123zxc";




    public void SendMailUser( User nu, int n,int nRentic, String comentari) {
        List<String> Subject = Arrays.asList("Nou client","avis1","avis2", "avis3","avist4","Bienvenido a Rentic", "Contraseña canviada correctament", "sup4");

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

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
            message.setSubject(Subject.get(n));
            message.setText(getBodyTextUser(nu, n,comentari));

            Transport.send(message);

            if(nRentic==0 || nRentic==1 || nRentic==2 || nRentic==3 || nRentic==4) {
                message = new MimeMessage(session);
                message.setFrom(new InternetAddress(Username));
                message.addRecipient(Message.RecipientType.TO,
                        new InternetAddress("rentic.authinfo@gmail.com"));
                message.setSubject(Subject.get(nRentic));

                message.setText(getBodyTextUser(nu, nRentic,comentari));

                Transport.send(message);
            }

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void SendMailLloguer( Lloguer llog, int n,int nRentic, String comentari) {
        List<String> Subject = Arrays.asList("Lloguer eliminat","avis1","avis2", "avis3","avist4","Peticion de alquiler aceptada", "Peticion de alquiler rechazada", "sup4");

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

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
                    new InternetAddress(llog.getUser().getEmail()));
            message.setSubject(Subject.get(n));
            message.setText(getBodyTextLloguer(llog, n,comentari));

            Transport.send(message);

            if(nRentic==0 || nRentic==1 || nRentic==2 || nRentic==3 || nRentic==4) {
                message = new MimeMessage(session);
                message.setFrom(new InternetAddress(Username));
                message.addRecipient(Message.RecipientType.TO,
                        new InternetAddress("rentic.authinfo@gmail.com"));
                message.setSubject(Subject.get(nRentic));

                message.setText(getBodyTextLloguer(llog, nRentic,comentari));

                Transport.send(message);
            }

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getBodyTextUser(User nu, int n, String comentari) {


        List<String> text = new ArrayList<>();

        String bodyText0 = "Tenim un nou client!\n\n" + "Informació del client:\n" + "Nombre: \t" + nu.getNomComplet() + "\nEmail:" + " \t\t" + nu.getEmail() + "\n\n";
        text.add(bodyText0);

        String bodyText1 = "";
        text.add(bodyText1);

        String bodyText2 = "";
        text.add(bodyText2);

        String bodyText3 = "";
        text.add(bodyText3);

        String bodyText4 = "";
        text.add(bodyText4);

        String bodyText5 = "Estimad@ " + nu.getNomComplet() + "\n" +
                "\nBienvenid@ a Rentic,\n" +
                "Un lugar donde podrá disfrutar de las mejores ofertas en el alquiler de productos de todo tipo.\n" +
                "\nA partir de ahora, además, disfrutará de todas las ventajas y promociones que iremos ofreciendo.\n\n" +
                "Información de su cuenta en Rentic:\n\n" +
                "Nombre: \t" + nu.getNomComplet() + "\n" +
                "Email: \t\t" + nu.getEmail() + "\n" +
                "Password: \t" + nu.getPassword() + "\n" +
                "\n\nSi tiene cualquier duda o comentario, contacte con nosotros en el email: rentic.authinfo@gmail.com \n\n" +
                "Un saludo,\n" +
                "El equipo de Rentic.";
        text.add(bodyText5);

        String bodyText6 = "Estimad@ " + nu.getNomComplet() + "\n" +
                "\nSu contraseña ha sido canviada correctamente,\n" +
                "La nueva informacion de su cuenta en Rentic es:\n\n" +
                "Nombre: \t" + nu.getNomComplet() + "\n" +
                "Email: \t\t" + nu.getEmail() + "\n" +
                "Password: \t" + nu.getPassword() + "\n" +
                "\n\nSi tiene cualquier duda o comentario, contacte con nosotros en el email: rentic.authinfo@gmail.com \n\n" +
                "Un saludo,\n" +
                "El equipo de Rentic.";
        text.add(bodyText6);

        String bodyText7 = "";
        text.add(bodyText7);


        return text.get(n);
    }

    private String getBodyTextLloguer(Lloguer llog, int n, String comentari) throws Exception {
        DateAdapter d = new DateAdapter();
        List<String> text= new ArrayList<>();

        String bodyText0 = "El lloguer" + llog.getId() + " ha sigut eliminat per el propietari.\n" + "Comentari del propietari:\n" + " \t\t" + comentari+ "\n\n";
        text.add(bodyText0);

        String bodyText1 = "";
        text.add(bodyText1);

        String bodyText2 = "";
        text.add(bodyText2);

        String bodyText3 = "";
        text.add(bodyText3);

        String bodyText4 = "";
        text.add(bodyText4);

        String bodyText5 = "Estimad@ " + llog.getUser().getNomComplet() + "\n" +
                "\nFELICIDADES, su peticion de alquiler ha sido aceptada por el propietario:\n\n"+
                "Información del alquiler:\n\n" +

                "Datos del producto:\n"+
                "\t- Nombre del producto: \t\t" + llog.getObjecte().getNom() + "\n" +
                "\t- Nombre del propietario: \t" + llog.getUser().getNomComplet() + "\n" +
                "\t- Telefono contacto: \t\t" + llog.getUser().getTelefon() + "\n\n" +

                "Detalles del alquiler:\n"+
                "\t- Fecha inicio: \t" + llog.getDataInici() + "\n" +
                "\t- Fecha final: \t\t" + llog.getDataFi() + "\n" +
                "\t- Precio por dia: \t" + llog.getObjecte().getPreu() + "\u20ac \n" +
                "\t- Precio total: \t" + (Double) llog.getObjecte().getPreu()*d.diferenciaDies(llog.getDataInici(), llog.getDataFi()) + "\u20ac \n\n" +

                "\nA partir de ahora, Rentic no se hace responsable, de cualquier accidente que pueda ocurrir una vez acceptada la negociación del alquiler.\n\n" +

                "\n\nSi tiene cualquier duda o comentario, contacte con nosotros en el email: rentic.authinfo@gmail.com \n\n" +
                "Un saludo,\n" +
                "El equipo de Rentic.";
        text.add(bodyText5);

        String bodyText6 = "Estimad@ " + llog.getUser().getNomComplet() + "\n" +
                "\nLo sentimos, peró el propietario del producto "+ llog.getObjecte().getNom() + " ha rechazado el alquiler finalmente.\n\n"+
                "Comentario del propietario:\n\n" +comentari + "\n" +

                "Detalles del alquiler rechazado:\n"+
                "\t- Nombre del producto: \t\t" + llog.getObjecte().getNom() + "\n" +
                "\t- Nombre del propietario: \t" + llog.getUser().getNomComplet() + "\n" +
                "\t- Telefono contacto: \t\t" + llog.getUser().getTelefon() + "\n\n" +
                "\t- Fecha inicio: \t" + llog.getDataInici() + "\n" +
                "\t- Fecha final: \t\t" + llog.getDataFi() + "\n" +
                "\t- Precio por dia: \t" + llog.getObjecte().getPreu() + "\u20ac \n" +
                "\t- Precio total: \t" + (Double) llog.getObjecte().getPreu()*d.diferenciaDies(llog.getDataInici(), llog.getDataFi()) + "\u20ac \n\n" +

                "\n Si tiene cualquier duda sobre el rechazo del alquiler, contacte con el propietario.\n\n" +

                "\n\nSi tiene cualquier duda o comentario, contacte con nosotros en el email: rentic.authinfo@gmail.com \n\n" +
                "Un saludo,\n" +
                "El equipo de Rentic.";
        text.add(bodyText6);

        String bodyText7 = "";
        text.add(bodyText7);


        return text.get(n);

    }
}
