package org.rentic.rentic_javaee.rest;

import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.rentic.rentic_javaee.model.Lloguer;
import org.rentic.rentic_javaee.model.Objecte;
import org.rentic.rentic_javaee.service.ObjecteService;
import org.rentic.rentic_javaee.util.ToJSON;

import javax.ejb.EJB;
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
import java.util.List;


@Path("/objectes")
@RequestScoped
public class ObjecteRESTService {

    @EJB
    ObjecteService objecteService;


    @Inject
    ToJSON toJSON;


    public String Answer(String code, String data) {
        return "{\"code\":" + code + ", \"message\":" + null + ", \"data\":" + data + "}";
    }

    public static class lloguer {
        public String dataInici;
        public String dataFi;
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
            return Error.build("500", "Sessions not supported!");
        }

        Long userId = (Long) session.getAttribute("rentic_auth_id");

        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", "User not authenticated!");
        }

        try {
            Objecte o = objecteService.getObjecte(id);
            // Check that the user authenticated in the session owns the object it is trying to access
            if (o.getUser().getId() != userId) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.flushBuffer();
                return Error.build("500", "You don't own this task!");
            }
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
            return Error.build("500", "Sessions not supported!");
        }
        Long userId = (Long) session.getAttribute("rentic_auth_id");
        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", "User not authenticated!");
        }

        try {
            List<Objecte> results = objecteService.getObjectes(idUser,limit,orderBy, latitud, longitud);
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
            return Error.build("500", "Sessions not supported!");
        }

        if (session.getAttribute("rentic_auth_id") == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", "You are not authenticated!");
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
                return Error.build("500", ex.getMessage());
            }
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", "Error guardant imatge");
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
            return Error.build("500", "Sessions not supported!");
        }

        Long userId = (Long) session.getAttribute("rentic_auth_id");

        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", "User not authenticated!");
        }

        try {

            // Check that the user authenticated in the session owns the object it is trying to access
            if ( !objecteService.deleteObjecte(id, userId)) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.flushBuffer();
                return Error.build("500", "You aren't the owner of this object, or object not exist");
            }
            return Answer("200", "{}");
        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", ex.getMessage());
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
            return Error.build("500", "Sessions not supported!");
        }

        if (session.getAttribute("rentic_auth_id") == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", "You are not authenticated!");
        }


        Long userId = (Long) session.getAttribute("rentic_auth_id");
        Objecte o = new Objecte();


        try {
            o = objecteService.updateObjecte(input, userId);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (o != null) {
            try {
                return Answer("200", toJSON.Object(o));
            } catch (Exception ex) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.flushBuffer();
                return Error.build("500", ex.getMessage());
            }
        } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.flushBuffer();
                return Error.build("500", "You aren't the owner of this object, or object not exist");
        }
    }
    @POST
    @Path("{id}"+"/lloguer")
    @Produces(MediaType.APPLICATION_JSON)
    public String addlloguer(
            @Context HttpServletRequest req,
            @Context HttpServletResponse response,
            @PathParam("id") Long idObjecte ,
            lloguer ll ) throws IOException, ServletException {

        HttpSession session = req.getSession();

        if (session == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", "Sessions not supported!");
        }

        if (session.getAttribute("rentic_auth_id") == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", "You are not authenticated!");
        }

        Long userId = (Long) session.getAttribute("rentic_auth_id");
        Lloguer llog = new Lloguer();

        try {
            llog = objecteService.addLloguer(ll.dataInici,ll.dataFi,idObjecte,userId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (llog != null) {
            try {
                return Answer("200", toJSON.Object(llog));
            } catch (Exception ex) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.flushBuffer();
                return Error.build("500", ex.getMessage());
            }
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return Error.build("500", "Error guardant imatge");
        }
    }

}