package org.rentic.rentic_javaee.rest;


import javafx.util.Pair;
import org.rentic.rentic_javaee.model.Coordenades;
import org.rentic.rentic_javaee.model.Disponibilitat;
import org.rentic.rentic_javaee.model.Objecte;
import org.rentic.rentic_javaee.service.ObjecteService;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Path("/objectes")
@RequestScoped
public class ObjecteRESTService {

    @EJB
    ObjecteService objecteService;

    @Inject
    ToJSON toJSON;

    public static class objecte {
        public String nom;
        public String descripcio;
        public float preu;
        public List<String> tags;
        public float latitud;
        public float longitud;
        public List<Pair<String,String>> dispRang;
        public Boolean dispCapDeSetmana;
        public Boolean dispEntreDeSetmana;
    }

    public String Answer(String code, String data){
        return "{\"code\":"+code+", \"message\":"+null+", \"data\":"+data+"}" ;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String obtenir(@Context HttpServletRequest req, @Context HttpServletResponse response, objecte i) throws Exception {

        HttpSession session = req.getSession();

        if (session == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500","Sessions not supported!");
        }

        if (session.getAttribute("rentic_auth_id") == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500","You are not authenticated!");
        }

        Long userId = (Long) session.getAttribute("simpleapp_auth_id");

        Objecte o = objecteService.addObjecte(i, userId);

        if (o!=null) {
            try {
                return Answer("200", toJSON.Object(o));
            } catch (Exception ex) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.flushBuffer();
                return Error.build("500",ex.getMessage());
            }
        }else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", "No s'ha inserit be l'objecte");
        }

    }
}
