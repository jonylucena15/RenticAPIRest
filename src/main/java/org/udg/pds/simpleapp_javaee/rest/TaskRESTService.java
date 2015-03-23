package org.udg.pds.simpleapp_javaee.rest;

import java.util.Date;

import javax.ejb.EJB;

import javax.ejb.EJBException;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.udg.pds.simpleapp_javaee.model.Event;
import org.udg.pds.simpleapp_javaee.model.Task;
import org.udg.pds.simpleapp_javaee.service.TaskService;
import org.udg.pds.simpleapp_javaee.util.ToJSON;

@Path("/tasks")
@RequestScoped
public class TaskRESTService {

    @EJB
    TaskService taskService;

    @Inject
    ToJSON toJSON;

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getTask(@Context HttpServletRequest req,
            @PathParam("id") Long id) {
        HttpSession session = req.getSession();

        if (session == null) {
            return Error.build("Sessions not supported!");
        }

        Long userId = (Long) session.getAttribute("simpleapp_auth_id");

        if (userId == null) {
            return Error.build("User not authenticated!");
        }

        try {
            Task t = taskService.getTask(id);
            // Check that the user authenticated in the session owns the task it is trying to access
            if (t.getUser().getId() != userId) {
                return Error.build("You don't own this task!");
            }
            //return toJSON.Task(t);
            return toJSON.Object(t);
        } catch (Exception ex) {
            return Error.build(ex.getMessage());
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String listAllTasks(@Context HttpServletRequest req) {
        HttpSession session = req.getSession();

        if (session == null) {
            return Error.build("Sessions not supported!");
        }
        Long userId = (Long) session.getAttribute("simpleapp_auth_id");
        if (userId == null) {
            return Error.build("User not authenticated!");
        }

        try {
            taskService.event();
            Event e = taskService.event2();

            final TaskService.TaskList results = taskService.getTasks(userId);
            return toJSON.Object(results);
        } catch (EJBException ex) {
            return Error.build("Exception at taskService: " + ex.getMessage());
        } catch (Exception ex) {
            return Error.build(ex.getMessage());
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String addTask(@Context HttpServletRequest req,
            @FormParam("text") String text,
            @FormParam("dateCreated") Date dateCreated,
            @FormParam("dateLimit") Date dateLimit) {

        HttpSession session = req.getSession();

        if (session == null) {
            return Error.build("Sessions not supported!");
        }
        
        Long userId = (Long) session.getAttribute("simpleapp_auth_id");
        
        if (userId == null) {
            return Error.build("User not authenticated!");
        }

        if (text == null) {
            return Error.build("No text supplied");
        }
        if (dateCreated == null) {
            return Error.build("No creation date supplied");
        }
        if (dateLimit == null) {
            return Error.build("No limit date supplied");
        }

        try {
            Task t = taskService.addTask(text, userId, dateCreated, dateLimit);
            return toJSON.Object(t);
        } catch (Exception ex) {
            return Error.build(ex.getMessage());
        }
    }
}
