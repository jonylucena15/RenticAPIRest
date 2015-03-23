package org.udg.pds.rentic_javaEE.rest;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.enterprise.context.RequestScoped;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.io.*;
import java.util.List;
import java.util.Map;

@Path("/images")
@RequestScoped
public class ImageRESTService {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public String upload(@Context HttpServletRequest req,
                         MultipartFormDataInput input) {

      String fileName = "";

      Map<String, List<InputPart>> formParts = input.getFormDataMap();

      List<InputPart> inPart = formParts.get("file");

      for (InputPart inputPart : inPart) {

        try {

          // Retrieve headers, read the Content-Disposition header to obtain the original name of the file
          MultivaluedMap<String, String> headers = inputPart.getHeaders();
          fileName = parseFileName(headers);

          // Handle the body of that part with an InputStream
          InputStream istream = inputPart.getBody(InputStream.class,null);

          fileName = System.getenv("IMAGE_DATA_DIR") + fileName;

          saveFile(istream,fileName);

        } catch (IOException e) {
          e.printStackTrace();
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
                        String serverLocation) {

    try {
      int read;
      byte[] bytes = new byte[1024];

      OutputStream outpuStream = new FileOutputStream(new File(serverLocation));
      while ((read = uploadedInputStream.read(bytes)) != -1) {
        outpuStream.write(bytes, 0, read);
      }
      outpuStream.flush();
      outpuStream.close();
    } catch (IOException e) {

      e.printStackTrace();
    }
  }
}
