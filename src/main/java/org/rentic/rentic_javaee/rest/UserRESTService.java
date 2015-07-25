package org.rentic.rentic_javaee.rest;

import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.rentic.rentic_javaee.model.Lloguer;
import org.rentic.rentic_javaee.model.User;
import org.rentic.rentic_javaee.service.MailService;
import org.rentic.rentic_javaee.service.UserService;
import org.rentic.rentic_javaee.util.ToJSON;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Jony Lucena.
 */
@Path("/usuaris")
@RequestScoped
public class UserRESTService {

    @Inject
    UserService userService;

    @Inject
    MailService mailService;

    @Inject
    ToJSON toJSON;


    public String Answer(String code, String data){
            return "{\"code\":"+code+", \"message\":"+null+", \"data\":"+data+"}" ;
    }

    static class login {
        public String email;
        public String password;
    }
    public static class usuari {
        public Long idUsuari;
        public String email;
        public String password;
        public String nomComplet;
        public String telefon;
        public String fotoPerfil;
        public String facebookId;
    }

    static class passwords {
        public String oldPassword;
        public String newPassword;
    }

    @POST
    @Path("auth")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String auth(
            @Context HttpServletRequest req,
            @Context final HttpServletResponse response,
            login log) throws IOException {

        // Access to the HTTP session
        HttpSession session = req.getSession();

        if (session == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500","Sessions not supported!");
        }

        // Check if the session has the attribute "rentic_auth_id"
        if (session.getAttribute("rentic_auth_id") == null) {
            // If the user is not authenticated we have to check if the password match
            User u = userService.matchPassword(log.email, log.password);
            if (u != null) {
                try {
                    // The username and password match, add the "rentic_auth_id" attribute to the session
                    // to identify the user in the next calls
                    session.setAttribute("rentic_auth_id", u.getId());
                    session.setMaxInactiveInterval(-1);


                    return Answer("200", toJSON.Object(u));

                } catch (Exception ex) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.flushBuffer();
                    return Error.build("500", ex.getMessage());
                }
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.flushBuffer();
                return Error.build("500", "User or password incorrect!");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", "Already authenticated!");
        }
    }

    @Path("logout")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String logout(
            @Context HttpServletRequest req,
            @Context final HttpServletResponse response) throws IOException {

        HttpSession session = req.getSession();

        if (session == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500","Sessions not supported!");
        }

        if (session.getAttribute("rentic_auth_id") != null) {
            try {

                session.setAttribute("rentic_auth_id",null);

                return Answer("200", "{}");
            } catch (Exception ex) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.flushBuffer();
                return Error.build("500",ex.getMessage());
            }
        }else{
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500","You aren't already authenticated!");
        }

    }

    @Path("update")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String UpdateUser(
            @Context HttpServletRequest req,
            @Context HttpServletResponse response,
            usuari input) throws IOException {

        HttpSession session = req.getSession();

        if (session == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", "Error sessions no soportades!");
        }

        if (session.getAttribute("rentic_auth_id") == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", "Error no estas loguejat!");
        }


        Long userId = (Long) session.getAttribute("rentic_auth_id");
        User u=null;


        try {
            u = userService.updateUser(input, userId);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", "Error al actualitzar l'objecte!");
        }

        if (u != null) {
            try {
                return Answer("200", toJSON.Object(u));
            } catch (Exception ex) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.flushBuffer();
                return Error.build("500", "Error passant l'objecte a JSON!");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", "Error no ets el propietari de l'objecte, o l'objecte no existeix!");
        }
    }

    @Path("updateImage")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public String UpdateObject(
            @Context HttpServletRequest req,
            @Context HttpServletResponse response,
            MultipartFormDataInput input) throws IOException {

        HttpSession session = req.getSession();

        if (session == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", "Error sessions no soportades!");
        }

        if (session.getAttribute("rentic_auth_id") == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", "Error no estas loguejat!");
        }

        Long userId = (Long) session.getAttribute("rentic_auth_id");
        User u=null;


        try {
            u = userService.updateImage(input, userId);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", "Error al pujar l'imatge de perfil!");
        }

        if (u != null) {
            try {
                return Answer("200", toJSON.Object(u));
            } catch (Exception ex) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.flushBuffer();
                return Error.build("500", "Error passant l'objecte a JSON!");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", "Error no ets el propietari de l'objecte, o l'objecte no existeix!");
        }
    }

    @Path("updatePassword")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String UpdatePassword(
            @Context HttpServletRequest req,
            @Context HttpServletResponse response,
            passwords passwords) throws IOException {

        HttpSession session = req.getSession();

        if (session == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", "Error sessions no soportades!");
        }

        if (session.getAttribute("rentic_auth_id") == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", "Error no estas loguejat!");
        }


        Long userId = (Long) session.getAttribute("rentic_auth_id");
        User u =null;


        try {
            u = userService.updatePassword(passwords.oldPassword, passwords.newPassword, userId);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", "Error al actualitzar l'objecte!");
        }

        if (u != null) {
            try {
                mailService.SendMailUser(u,6,5);
                return Answer("200", toJSON.Object(u));
            } catch (Exception ex) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.flushBuffer();
                return Error.build("500", "Error passant l'objecte a JSON!");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", "Error no ets el propietari de l'objecte, o l'objecte no existeix!");
        }
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getUser(
            @Context HttpServletRequest req,
            @Context HttpServletResponse response,
            @PathParam("id") Long id) throws IOException {

        // Access to the HTTP session
        HttpSession session = req.getSession();

        if (session == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500","Error sessions no soportades!");
        }

        Long userid = (Long) session.getAttribute("rentic_auth_id");

        // Check if the user is authenticated
        if (userid == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500","Error no estas loguejat!");
        }

        // Check if the user is trying to access other user's data
        if (id.intValue() != userid.intValue() ) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500","Error no pots accedir a les dades d'un altre usuari!");
        }

        User u = userService.getUser(id);

        // Check if the user id exists
        if (u == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500","L'usuari amb ID " + id + " no existeix!");
        }


        try {
            return Answer("200", toJSON.Object(u));
        } catch (IOException ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            Logger.getLogger(UserRESTService.class.getName()).log(Level.SEVERE, "Error transformant l'usuari a JSON!", ex);
            return Error.build("500","Error transformant l'usuari a JSON!");
        }
    }

    @Path("register")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String register(
            @Context HttpServletRequest req,
            @Context HttpServletResponse response,
            usuari u) throws IOException {

        HttpSession session = req.getSession();

        if (session == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500","Error sessions no soportades!");
        }

        if (session.getAttribute("rentic_auth_id") != null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500","Error ja estas loguejat!");
        }

        User nu = userService.register(u);

        if (nu!=null) {
            try {

                session.setAttribute("rentic_auth_id", nu.getId());
                session.setMaxInactiveInterval(-1);
                mailService.SendMailUser(nu,5,0);
                return Answer("200", toJSON.Object(nu));
            } catch (Exception ex) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.flushBuffer();
                return Error.build("500","Error transformant l'usuari a JSON!");
            }
        }else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", "Error el email ja esta registrat!");
        }
    }

    @GET
    @Path("{id}"+"/lloguersRealitzatsFin")
    @Produces(MediaType.APPLICATION_JSON)
    public String getLloguersRealitzatsFin(
            @Context HttpServletRequest req,
            @Context HttpServletResponse response,
            @PathParam("id") Long id) throws IOException {


        HttpSession session = req.getSession();

        if (session == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", "Error sessions no soportades!");
        }

        if (session.getAttribute("rentic_auth_id") == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", "Error no estas loguejat!");
        }

        Long userid = (Long) session.getAttribute("rentic_auth_id");

        // Check if the user is authenticated
        if (userid == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500","Error no estas loguejat!");
        }

        // Check if the user is trying to access other user's data
        if (id.intValue() != userid.intValue() ) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500","Error no pots accedir a les dades d'un altre usuari!");
        }

        Collection<Lloguer> u = userService.getLloguersRealitzats(id, true);

        // Check if the user id exists
        if (u == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500","L'usuari amb ID " + id + " no existeix o no tens cap lloguer realitzat!");
        }


        try {
            return Answer("200", toJSON.Object(u));
        } catch (IOException ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            Logger.getLogger(UserRESTService.class.getName()).log(Level.SEVERE, "Error transformant l'usuari a JSON!", ex);
            return Error.build("500","Error transformant l'usuari a JSON!");
        }
    }


    @GET
    @Path("{id}"+"/lloguersRealitzats")
    @Produces(MediaType.APPLICATION_JSON)
    public String getLloguersRealitzats(
            @Context HttpServletRequest req,
            @Context HttpServletResponse response,
            @PathParam("id") Long id) throws IOException {


            HttpSession session = req.getSession();

            if (session == null) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.flushBuffer();
                return Error.build("500", "Error sessions no soportades!");
            }

            if (session.getAttribute("rentic_auth_id") == null) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.flushBuffer();
                return Error.build("500", "Error no estas loguejat!");
            }

        Long userid = (Long) session.getAttribute("rentic_auth_id");

        // Check if the user is authenticated
        if (userid == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500","Error no estas loguejat!");
        }

        // Check if the user is trying to access other user's data
        if (id.intValue() != userid.intValue() ) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500","Error no pots accedir a les dades d'un altre usuari!");
        }

        Collection<Lloguer> u = userService.getLloguersRealitzats(id, false);

        // Check if the user id exists
        if (u == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500","L'usuari amb ID " + id + " no existeix o no tens cap lloguer realitzat!");
        }


        try {
            return Answer("200", toJSON.Object(u));
        } catch (IOException ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            Logger.getLogger(UserRESTService.class.getName()).log(Level.SEVERE, "Error transformant l'usuari a JSON!", ex);
            return Error.build("500","Error transformant l'usuari a JSON!");
        }
    }

    @GET
    @Path("{id}"+"/lloguersRebuts")
    @Produces(MediaType.APPLICATION_JSON)
    public String getLloguersRebuts(
            @Context HttpServletRequest req,
            @Context HttpServletResponse response,
            @PathParam("id") Long id) throws IOException {


        HttpSession session = req.getSession();

        if (session == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", "Error sessions no soportades!");
        }

        if (session.getAttribute("rentic_auth_id") == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", "Error no estas loguejat!");
        }

        Long userid = (Long) session.getAttribute("rentic_auth_id");

        // Check if the user is authenticated
        if (userid == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500","Error no estas loguejat!");
        }

        // Check if the user is trying to access other user's data
        if (id.intValue() != userid.intValue() ) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500","Error no pots accedir a les dades d'un altre usuari!");
        }

        Collection<Lloguer> u = userService.getLloguersRebuts(id, false);

        // Check if the user id exists
        if (u == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500","L'usuari amb ID " + id + " no existeix o no tens cap lloguer realitzat!");
        }


        try {
            return Answer("200", toJSON.Object(u));
        } catch (IOException ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            Logger.getLogger(UserRESTService.class.getName()).log(Level.SEVERE, "Error transformant l'usuari a JSON!", ex);
            return Error.build("500","Error transformant l'usuari a JSON!");
        }
    }

    @GET
    @Path("{id}"+"/lloguersRebutsFin")
    @Produces(MediaType.APPLICATION_JSON)
    public String getLloguersRebutsFinalitzats(
            @Context HttpServletRequest req,
            @Context HttpServletResponse response,
            @PathParam("id") Long id) throws IOException {


        HttpSession session = req.getSession();

        if (session == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", "Error sessions no soportades!");
        }

        if (session.getAttribute("rentic_auth_id") == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", "Error no estas loguejat!");
        }

        Long userid = (Long) session.getAttribute("rentic_auth_id");

        // Check if the user is authenticated
        if (userid == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500","Error no estas loguejat!");
        }

        // Check if the user is trying to access other user's data
        if (id.intValue() != userid.intValue() ) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500","Error no pots accedir a les dades d'un altre usuari!");
        }

        Collection<Lloguer> u = userService.getLloguersRebuts(id, true);

        // Check if the user id exists
        if (u == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500","L'usuari amb ID " + id + " no existeix o no tens cap lloguer realitzat!");
        }


        try {
            return Answer("200", toJSON.Object(u));
        } catch (IOException ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            Logger.getLogger(UserRESTService.class.getName()).log(Level.SEVERE, "Error transformant l'usuari a JSON!", ex);
            return Error.build("500","Error transformant l'usuari a JSON!");
        }
    }

    @GET
    @Path("{id}/lloguers")
    @Produces(MediaType.APPLICATION_JSON)
    public String getLloguers(
            @Context HttpServletRequest req,
            @Context HttpServletResponse response,
            @PathParam("id") Long id) throws Exception {

        // Access to the HTTP session
        HttpSession session = req.getSession();

        if (session == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500","Error sessions no soportades!");
        }

        Long userid = (Long) session.getAttribute("rentic_auth_id");

        // Check if the user is authenticated
        if (userid == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500","Error no estas loguejat!");
        }

        // Check if the user is trying to access other user's data
        if (id.intValue() != userid.intValue() ) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500","Error no pots accedir a les dades d'un altre usuari!");
        }

        Collection<Lloguer> llog = userService.getLloguers(id);

        // Check if the user id exists
        if (llog == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500","L'usuari amb ID " + id + " no existeix!");
        }

        try {
            return Answer("200", toJSON.Object(llog));
        } catch (IOException ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500","Error transformant l'usuari a JSON!");
        }
    }
}
