package org.rentic.rentic_javaee.rest;

import org.rentic.rentic_javaee.model.User;
import org.rentic.rentic_javaee.service.UserService;
import org.rentic.rentic_javaee.util.ToJSON;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Jony Lucena.
 */
@Path("/usuaris")
@RequestScoped
public class UserRESTService {

    @EJB
    UserService userService;

    @Inject
    ToJSON toJSON;


    public String Answer(String code, String data){
            return "{\"code\":"+code+", \"message\":"+null+", \"data\":"+data+"}" ;
    }

    static class login {
        public String email;
        public String password;
    }
    public static class registre {
        public String email;
        public String password;
        public String nomComplet;
        public String telefon;
        public String fotoPerfil;
        public String facebookId;
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
            registre u) throws IOException {

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
}
