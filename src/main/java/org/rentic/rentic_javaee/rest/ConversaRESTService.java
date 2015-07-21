package org.rentic.rentic_javaee.rest;

import org.rentic.rentic_javaee.model.Conversa;
import org.rentic.rentic_javaee.service.ConversaService;
import org.rentic.rentic_javaee.service.UserService;
import org.rentic.rentic_javaee.util.ToJSON;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Created by Jony Lucena.
 */
@Path("/converses")
@RequestScoped
public class ConversaRESTService {

    @EJB
    ConversaService conversaService;

    @EJB
    UserService userService;

    @Inject
    ToJSON toJSON;


    public String Answer(String code, String data) {
        return "{\"code\":" + code + ", \"message\":" + null + ", \"data\":" + data + "}";
    }

    public static class novaConversa {
        public String dataInici;
        public String dataFi;
        public Long idObjecte;
        public Long idArrendador;
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String addConversa(
            @Context HttpServletRequest req,
            @Context HttpServletResponse response,
            novaConversa u) throws IOException {

        HttpSession session = req.getSession();

        if (session == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", "Error sessions no soportades!");
        }

        Long userId = (Long) session.getAttribute("rentic_auth_id");

        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", "Error no estas loguejat!");
        }

        Conversa c = new Conversa();

        try {
            c = conversaService.addConversa(u.dataInici, u.dataFi, u.idObjecte, u.idArrendador, userId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (c != null) {
            try {
                return Answer("200", toJSON.Object(c));
            } catch (Exception ex) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.flushBuffer();
                return Error.build("500", ex.getMessage());
            }
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", "Error creant la conversa");
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getChats(
            @Context HttpServletRequest req,
            @Context HttpServletResponse response) throws IOException {

        // Access to the HTTP session
        HttpSession session = req.getSession();

        if (session == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500","Error Sessions no soportades!");
        }

        Long userid = (Long) session.getAttribute("rentic_auth_id");

        // Check if the user is authenticated
        if (userid == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500","Error No estas loguejat!");
        }

        List<Conversa> c= conversaService.getChats(userid);

        // Check if the user id exists
        if (c == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500","Error l'id d'usuari " + userid + " no existeixt!");
        }

        try {
            return Answer("200", toJSON.Object(c));
        } catch (IOException ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            Logger.getLogger(UserRESTService.class.getName()).log(Level.SEVERE, "Error transformant l'usuari a JSON!", ex);
            return Error.build("500","Error transformant l'usuari a JSON!");
        }
    }

    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String eliminarConversa(
            @Context HttpServletRequest req,
            @Context HttpServletResponse response,
            @PathParam("id") Long idChat
           ) throws IOException, ServletException {

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
        Boolean eliminat=false;
        try {
            eliminat  = conversaService.eliminarConversa(idChat, userId);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", "Error creant el lloguer de l'objecte");
        }

        if (eliminat) {
            try {
                return Answer("200", "{}");
            } catch (Exception ex) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.flushBuffer();
                return Error.build("500", "Error transformant l'objecte a JSON");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", "Error no s'ha pogut eliminar la conversa");
        }
    }
}