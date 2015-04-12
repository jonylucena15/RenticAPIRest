package org.rentic.rentic_javaee.rest;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonRawValue;
import org.rentic.rentic_javaee.model.User;
import org.rentic.rentic_javaee.service.UserService;
import org.rentic.rentic_javaee.util.ToJSON;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.json.Json;
import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBContext;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

// This class is used to process all the authentication related URLs
@Path("/usuaris")
@RequestScoped
public class UserRESTService {

    // This is the EJB used to access user data
    @EJB
    UserService userService;

    @Inject
    ToJSON toJSON;



    public String Answer(String code, String data){
            return "{\"code\":"+code+", \"message\":"+null+", \"data\":"+data+"}" ;
    }

    @POST
    @Path("auth")
    @Produces(MediaType.APPLICATION_JSON)
    public String auth(
            @Context HttpServletRequest req,
            @Context final HttpServletResponse response,
            @FormParam("email") String email,
            @FormParam("password") String password) throws IOException {

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
            User u = userService.matchPassword(email, password);
            if (u != null) {
                try {
                    // The username and password match, add the "rentic_auth_id" attribute to the session
                    // to identify the user in the next calls
                    session.setAttribute("rentic_auth_id", u.getId());

                    return Answer("200", toJSON.User(u));

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

    @Path("register")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String register(
            @Context HttpServletRequest req,
            @Context HttpServletResponse response,
            @FormParam("nomComplet") String nomComplet,
            @FormParam("email") String email,
            @FormParam("telefon") String telefon,
            @FormParam("facebookId") String facebookId,
            @FormParam("fotoPerfil") String fotoPerfil,
            @FormParam("password") String password) throws IOException {

        HttpSession session = req.getSession();

        if (session == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500","Sessions not supported!");
        }

        if (session.getAttribute("rentic_auth_id") != null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500","You are already authenticated!");
        }

        User u = new User(nomComplet, email, telefon, facebookId, fotoPerfil,  password);

        boolean  n = userService.register(u);

        if (n) {
            try {
                session.setAttribute("rentic_auth_id", u.getId());

                return Answer("200", toJSON.User(u));
            } catch (Exception ex) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.flushBuffer();
                return Error.build("500",ex.getMessage());
            }
        }else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", "Your email is already registered!");
        }
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getUser(
            @Context HttpServletRequest req,
            @Context HttpServletResponse response,
            @PathParam("id") long id) throws IOException {

        // Access to the HTTP session
        HttpSession session = req.getSession();

        if (session == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500","Sessions not supported!");
        }

        Long userid = (Long) session.getAttribute("rentic_auth_id");

        // Check if the user is authenticated
        if (userid == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500","You are not authenticated!");
        }

        User u = userService.getUser(id);

        // Check if the user id exists
        if (u == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500","User id == " + id + " does not exist!");
        }

        // Check if the user is trying to access other user's data
        if (u.getId() != userid) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500","You cannot access data fromm other users!");
        }
        try {
            return Answer("200", toJSON.User(u));
        } catch (IOException ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            Logger.getLogger(UserRESTService.class.getName()).log(Level.SEVERE, "Error cocerting User to JSON!", ex);
            return Error.build("500","Error cocerting User to JSON!");
        }
    }
}
