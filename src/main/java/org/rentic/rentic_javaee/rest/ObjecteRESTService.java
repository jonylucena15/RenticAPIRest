package org.rentic.rentic_javaee.rest;

import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.rentic.rentic_javaee.model.Lloguer;
import org.rentic.rentic_javaee.model.Objecte;
import org.rentic.rentic_javaee.service.MailService;
import org.rentic.rentic_javaee.service.ObjecteService;
import org.rentic.rentic_javaee.util.ToJSON;

import javax.ejb.EJBException;
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
import java.util.Collection;
import java.util.List;


/**
 * Created by Jony Lucena.
 */
@Path("/objectes")
@RequestScoped
public class ObjecteRESTService {

    @Inject
    ObjecteService objecteService;

    @Inject
    MailService mailService;

    @Inject
    ToJSON toJSON;


    public String Answer(String code, String data) {
        return "{\"code\":" + code + ", \"message\":" + null + ", \"data\":" + data + "}";
    }

    public static class lloguer {
        public String dataInici;
        public String dataFi;
        public Long idLlogater;
    }


    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getObjecte(
            @Context HttpServletRequest req,
            @Context HttpServletResponse response,
            @PathParam("id") Long id) throws IOException {

        HttpSession session = req.getSession();

        if (session == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", "Error Sessions no soportades!");
        }

        Long userId = (Long) session.getAttribute("rentic_auth_id");

        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", "Error no estas loguejat!");
        }

        try {
            Objecte o = objecteService.getObjecte(id);
            // Check that the user authenticated in the session owns the object it is trying to access
            return Answer("200", toJSON.Object(o));
        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", ex.getMessage());
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String listObjectes(
            @Context HttpServletRequest req,
            @Context HttpServletResponse response,
            @QueryParam("limit") int limit,
            @QueryParam("idUsuari") Long idUser,
            @QueryParam("latitud")  Double latitud,
            @QueryParam("longitud") Double longitud,
            @QueryParam("orderBy") List<String> orderBy) throws IOException {

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

        try {
            List<Objecte> results =null;
            if (idUser!=null) {
                results = objecteService.getObjectesUsuari(idUser);
            }else {
                results = objecteService.getObjectes(limit, orderBy, latitud, longitud);
            }
            return Answer("200", toJSON.Object(results));
        } catch (EJBException ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", "Exception at ObjecteService: " + ex.getMessage());
        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", ex.getMessage());
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public String addObject(
            @Context HttpServletRequest req,
            @Context HttpServletResponse response,
            MultipartFormDataInput input
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
        Objecte o = new Objecte();

        try {
            o = objecteService.addObjecte(input, userId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (o != null) {
            try {
                return Answer("200", toJSON.Object(o));
            } catch (Exception ex) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.flushBuffer();
                return Error.build("500", "Error passant l'objecte a JSON!");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", "Error guardant l'objecte!");
        }
    }

    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteObjecte(
            @Context HttpServletRequest req,
            @Context HttpServletResponse response,
            @PathParam("id") Long id) throws IOException {

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
        try {

            // Check that the user authenticated in the session owns the object it is trying to access
            if ( !objecteService.deleteObjecte(id, userId)) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.flushBuffer();
                return Error.build("500", "Error no ets el propietari d'aquest objecte, o l'objecte no existeix");
            }
            return Answer("200", "{}");
        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", "Error transformant l'objecte a JSON!");
        }
    }

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
        Objecte o = new Objecte();


        try {
            o = objecteService.updateObjecte(input, userId);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", "Error al actualitzar l'objecte!");
        }

        if (o != null) {
            try {
                return Answer("200", toJSON.Object(o));
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
    @POST
    @Path("{id}"+"/lloguer")
    @Produces(MediaType.APPLICATION_JSON)
    public String addLloguer(
            @Context HttpServletRequest req,
            @Context HttpServletResponse response,
            @PathParam("id") Long idObjecte ,
            lloguer ll ) throws IOException, ServletException {

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
        Lloguer llog = new Lloguer();

        try {
            llog = objecteService.addLloguer(ll.dataInici, ll.dataFi, idObjecte, ll.idLlogater, userId);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", "Error creant el lloguer de l'objecte");
        }

        if (llog != null) {
            try {
                mailService.SendMailLloguer(llog, 5, 6,"");
                return Answer("200", toJSON.Object(llog));
            } catch (Exception ex) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.flushBuffer();
                return Error.build("500", "Error transformant l'objecte a JSON");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", "Error no ets el propietari de l'objecte o l'objecte no existeix");
        }
    }
    @DELETE
    @Path("{id}"+"/lloguer/{idLloguer}")
    @Produces(MediaType.APPLICATION_JSON)
    public String eliminarLloguer(
            @Context HttpServletRequest req,
            @Context HttpServletResponse response,
            @PathParam("id") Long idObjecte,
            @PathParam("idLloguer") Long idLloguer) throws IOException, ServletException {

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
        Lloguer eliminat=null;
        try {
           eliminat  = objecteService.eliminarLloguer(idObjecte, idLloguer, userId);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", "Error creant el lloguer de l'objecte");
        }

        if (eliminat!=null) {
            try {
                mailService.SendMailLloguer(eliminat, 6, 6,"");
                return Answer("200", "{}");
            } catch (Exception ex) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.flushBuffer();
                return Error.build("500", "Error transformant l'objecte a JSON");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", "Error no s'ha pogut eliminar el lloguer");
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

        Collection<Lloguer> llog = objecteService.getLloguers(id);

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