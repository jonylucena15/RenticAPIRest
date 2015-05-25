package org.rentic.rentic_javaee.rest;

import org.rentic.rentic_javaee.model.Conversa;
import org.rentic.rentic_javaee.service.ConversaService;
import org.rentic.rentic_javaee.util.ToJSON;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;


/**
 * Created by Jony Lucena.
 */
@Path("/converses")
@RequestScoped
public class ConversaRESTService {

    @EJB
    ConversaService conversaService;


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
            return Error.build("500", "Sessions not supported!");
        }

        Long userId = (Long) session.getAttribute("rentic_auth_id");

        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", "User not authenticated!");
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
            return Error.build("500", "Error crean la conversa");
        }
    }
}