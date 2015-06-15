package org.rentic.rentic_javaee.rest;


import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.File;


/**
 * Created by Jony Lucena.
 */
@Path("/images")
@RequestScoped
public class ImageRESTService {

    @GET
    @Path("{id}")
    @Produces("image/png")
    public Response getFile(
            @PathParam("id") String id) {

        File file = new File(System.getenv("OPENSHIFT_DATA_DIR") + id);

        Response.ResponseBuilder response = Response.ok((Object) file);
        response.header("Content-Disposition", "attachment; filename="+id);
        return response.build();
    }
}
