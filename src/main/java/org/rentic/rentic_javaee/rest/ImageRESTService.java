package org.rentic.rentic_javaee.rest;


import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.enterprise.context.RequestScoped;
import org.json.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.List;
import java.util.Map;

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

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public String upload(@Context HttpServletRequest req,
                         @Context HttpServletResponse response,
                         MultipartFormDataInput input) throws IOException {

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


        Map<String, List<InputPart>> formParts = input.getFormDataMap();
        String objecte = formParts.get("objecte").get(0).getBodyAsString();
        System.out.println(objecte);
        JSONObject obj = new JSONObject(objecte);

        List<InputPart> inPart = formParts.get("file");
        for (InputPart inputPart : inPart) {
            try {

                // Retrieve headers, read the Content-Disposition header to obtain the original name of the file
                MultivaluedMap<String, String> headers = inputPart.getHeaders();
                String fileName = parseFileName(headers);

                // Handle the body of that part with an InputStream
                InputStream istream = inputPart.getBody(InputStream.class, null);

                // fileName = System.getenv("OPENSHIFT_DATA_DIR") + fileName;
                fileName = "C:\\Users\\Jony Lucena\\" + fileName;

                saveFile(istream, fileName);

            } catch (Exception e) {
                return Error.build("500", "Error saving file: " + e.getMessage());
            }
        }

        return "{}";
    }

    // Parse Content-Disposition header to get the original file name
    private String parseFileName(MultivaluedMap<String, String> headers) {

        String[] contentDispositionHeader = headers.getFirst("Content-Disposition").split(";");

        for (String name : contentDispositionHeader) {
            if ((name.trim().startsWith("filename"))) {
                String[] tmp = name.split("=");
                String fileName = tmp[1].trim().replaceAll("\"","");

                return fileName;
            }
        }
        return "randomName";
    }

    // save uploaded file to a defined location on the server
    private void saveFile(InputStream uploadedInputStream,
                          String serverLocation) throws IOException {

        int read;
        byte[] bytes = new byte[1024];

        OutputStream outpuStream = new FileOutputStream(new File(serverLocation));
        while ((read = uploadedInputStream.read(bytes)) != -1) {
            outpuStream.write(bytes, 0, read);
        }
        outpuStream.flush();
        outpuStream.close();
    }
}
