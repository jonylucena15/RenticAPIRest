package org.rentic.rentic_javaee.rest;

import java.lang.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import org.rentic.rentic_javaee.model.Task;
import org.rentic.rentic_javaee.service.TaskService;
import org.rentic.rentic_javaee.util.ToJSON;

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
            return Error.build("300","Sessions not supported!");
        }

        Long userId = (Long) session.getAttribute("rentic_auth_id");

        if (userId == null) {
            return Error.build("300","User not authenticated!");
        }

        try {
            Task t = taskService.getTask(id);
            // Check that the user authenticated in the session owns the task it is trying to access
            if (t.getUser().getId() != userId) {
                return Error.build("300","You don't own this task!");
            }
            return toJSON.Object(t);
        } catch (Exception ex) {
            return Error.build("300",ex.getMessage());
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String listAllTasks(@Context HttpServletRequest req) {
        HttpSession session = req.getSession();

        if (session == null) {
            return Error.build("300","Sessions not supported!");
        }
        Long userId = (Long) session.getAttribute("rentic_auth_id");
        if (userId == null) {
            return Error.build("300","User not authenticated!");
        }

        try {
            final TaskService.TaskList results = taskService.getTasks(userId);
            return toJSON.Object(results);
        } catch (EJBException ex) {
            return Error.build("300","Exception at taskService: " + ex.getMessage());
        } catch (Exception ex) {
            return Error.build("300",ex.getMessage());
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String addTask(R_Task task, @Context HttpServletRequest req) throws ParseException {

        HttpSession session = req.getSession();

        if (session == null) {
            return Error.build("300","Sessions not supported!");
        }

        Long userId = (Long) session.getAttribute("rentic_auth_id");

        if (userId == null) {
            return Error.build("300","User not authenticated!");
        }

        if (task.text == null) {
            return Error.build("300","No text supplied");
        }
        if (task.dateCreated == null) {
            return Error.build("300","No creation date supplied");
        }
        if (task.dateLimit == null) {
            return Error.build("300","No limit date supplied");
        }


        String pattern = "dd-MM-yyyy HH:mm";
        SimpleDateFormat fmt = new SimpleDateFormat(pattern);

        Date dateCreated = fmt.parse(task.dateCreated);
        Date dateLimit = fmt.parse(task.dateLimit);

        try {
            Task t = taskService.addTask(task.text, userId, dateCreated, dateLimit);
            return toJSON.Object(t);
        } catch (Exception ex) {
            return Error.build("300",ex.getMessage());
        }
    }

    static class R_Task {
        public String text;
        public String dateCreated;
        public String dateLimit;
    }

}
