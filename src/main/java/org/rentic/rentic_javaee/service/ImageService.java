package org.rentic.rentic_javaee.service;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jony Lucena on 15/06/2015.
 */
@Stateless
@LocalBean
public class ImageService {

    @PersistenceContext
    private EntityManager em;

    private static final String FILE_PATH = "http://servidor-pds3.rhcloud.com/rest/images/";

    public List<String> uploadObjectImage(List<InputPart> inPart) {

        List<String> imatges= new ArrayList<String>();
        int i=0;
        for (InputPart inputPart : inPart) {
            try {
                String fileName = "imatgeObjecte"+i+"_"+Math.random()+".jpg";

                InputStream istream = inputPart.getBody(InputStream.class, null);

                String serverFileName = System.getenv("OPENSHIFT_DATA_DIR/objects/") + fileName;

                saveFile(istream, serverFileName);
                imatges.add(FILE_PATH+fileName);
            } catch (Exception e) {
                return null;
            }
            i++;
        }
        return imatges;
    }


    public String uploadUserImage(List<InputPart> inPart) {


        try {
            String fileName = "imatgeUser_"+Math.random()+".jpg";

            InputStream istream = inPart.get(0).getBody(InputStream.class, null);

            String serverFileName = System.getenv("OPENSHIFT_DATA_DIR/users/") + fileName;

            saveFile(istream, serverFileName);

            return FILE_PATH+fileName;

        } catch (Exception e) {
            return null;
        }
    }

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
