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

    static class inserir {
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
    public String obtenir(@Context HttpServletRequest req, @Context HttpServletResponse response, inserir i) throws IOException {

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

        Objecte o = new Objecte(i.nom,i.descripcio,i.preu,i.tags,i.dispCapDeSetmana,i.dispEntreDeSetmana);

        Coordenades coor = new Coordenades(i.latitud,i.longitud);

        o.setCoordenades(coor);

        try {
            for(Pair<String,String> a : i.dispRang) {
                String pattern = "dd-MM-yyyy";
                SimpleDateFormat fmt = new SimpleDateFormat(pattern);

                Date inici = fmt.parse(a.getKey());
                Date fi = fmt.parse(a.getValue());

                Disponibilitat disp=new Disponibilitat(inici,fi);
                o.addDisponibilitat(disp);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        boolean  n = objecteService.inserir(o);

        if (n) {
            try {
                return Answer("200", toJSON.User(o.getUser())); //TODO Comprovar si funciona bé el toJSON amb l'Objecte sencer
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
}
