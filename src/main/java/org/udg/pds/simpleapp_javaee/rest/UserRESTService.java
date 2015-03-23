package org.udg.pds.simpleapp_javaee.rest;

import java.io.IOException;
import org.udg.pds.simpleapp_javaee.model.User;
import org.udg.pds.simpleapp_javaee.service.UserService;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Path;
import javax.ws.rs.POST;
import javax.ws.rs.FormParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBContext;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.udg.pds.simpleapp_javaee.util.InitDB;
import org.udg.pds.simpleapp_javaee.util.ToJSON;

// This class is used to process all the authentication related URLs
@Path("/users")
@RequestScoped
public class UserRESTService {

    // This is the EJB used to access user data
    @EJB
    UserService userService;

    @Inject
    ToJSON toJSON;

    @POST
    @Path("auth")
    @Produces(MediaType.APPLICATION_JSON)
    public String auth(
            @Context HttpServletRequest req,
            @FormParam("username") String username,
            @FormParam("password") String password) {

        // Access to the HTTP session
        HttpSession session = req.getSession();

        if (session == null) {
            return Error.build("Sessions not supported!");
        }

        // Check if the session has the attribute "simpleapp_auth_id"
        if (session.getAttribute("simpleapp_auth_id") == null) {
            // If the user is not authenticated we have to check if the password match
            User u = userService.matchPassword(username, password);
            if (u != null) {
                try {
                    // The username and password match, add the "simpleapp_auth_id" attribute to the session
                    // to identify the user in the next calls
                    session.setAttribute("simpleapp_auth_id", u.getId());
                    return toJSON.User(u);
                } catch (Exception ex) {
                    return Error.build(ex.getMessage());
                }
            } else {
                return Error.build("Authentication error!");
            }
        } else {
            return Error.build("Already authenticated!");
        }
    }

    @Path("register")
    @POST
    @Produces("text/xml")
    public String register(
            @Context HttpServletRequest req,
            @FormParam("username") String username,
            @FormParam("email") String email,
            @FormParam("password") String password) {

        HttpSession session = req.getSession();

        if (session == null) {
            return Error.build("Sessions not supported!");
        }

        if (session.getAttribute("simpleapp_auth_id") != null) {
            return Error.build("You are already authenticated!");
        }

        User u = userService.register(username, email, password);
        if (u != null) {
            try {
                StringWriter sw = new StringWriter();
                JAXBContext.newInstance(User.class).createMarshaller().marshal(u, sw);
                return sw.toString();
            } catch (Exception ex) {
                return Error.build(ex.getMessage());
            }
        }

        return Error.build("Your email is already registered!");

    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getUser(
            @Context HttpServletRequest req,
            @PathParam("id") long id) {

        // Access to the HTTP session
        HttpSession session = req.getSession();

        if (session == null) {
            return Error.build("Sessions not supported!");
        }

        Long userid = (Long) session.getAttribute("simpleapp_auth_id");

        // Check if the user is authenticated
        if (userid == null) {
            return Error.build("You are not authenticated!");
        }

        User u = userService.getUser(id);

        // Check if the user id exists
        if (u == null) {
            return Error.build("User id == " + id + " does not exist!");
        }

        // Check if the user is trying to access other user's data
        if (u.getId() != userid) {
            return Error.build("You cannot access data fromm other users!");
        }
        try {
            return toJSON.User(u);
        } catch (IOException ex) {
            Logger.getLogger(UserRESTService.class.getName()).log(Level.SEVERE, "Error cocerting User to JSON!", ex);
            return Error.build("Error cocerting User to JSON!");
        }
    }

}
