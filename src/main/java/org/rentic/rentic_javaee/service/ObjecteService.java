package org.rentic.rentic_javaee.service;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.rentic.rentic_javaee.model.Objecte;
import org.rentic.rentic_javaee.model.User;
import org.rentic.rentic_javaee.util.FromJSONObject;


import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Stateless
@LocalBean
public class ObjecteService {

    @PersistenceContext
    private EntityManager em;

    public Objecte addObjecte(String i, Long userId,List<InputPart> inPart) throws Exception {

        User user = em.find(User.class, userId);

        Objecte o= FromJSONObject.getObject(Objecte.class, i);
        o.setImatges(uploadImage(inPart));
        o.setUser(user);
        user.addObjecte(o);

        em.persist(o);

        return o;
    }

    public Objecte updateObjecte(String i, Long userId,List<InputPart> inPart) throws Exception {

        Objecte o= FromJSONObject.getObject(Objecte.class, i);

        em.detach(o);
        o.setImatges(uploadImage(inPart));

        em.merge(o);

        return o;
    }

    public  Objecte getObjecte(Long id) throws Exception {
        return em.find(Objecte.class, id);
    }

    public  Boolean deleteObjecte(Long id, Long userID) throws Exception {
        Objecte o= em.find(Objecte.class, id);
        if(o!=null) {
            if (o.getUser().getId() == userID) {
                em.remove(o);
                return true;
            }
        }
        return false;
    }

    public List<Objecte> getObjectes(Long idUser, int limit, List<String> orderBy, Double latitud, Double longitud) {
        List <Objecte> o = new ArrayList<Objecte>();

        Query q=null;
        try {
            if (idUser!=null){
                q = em.createQuery("select obj from  Objecte obj where obj.userId=:idUsers");
                q.setParameter("idUsers",idUser);
            }else{
                q = em.createQuery("select obj from  Objecte obj");
            }

            List<Objecte> obj=q.getResultList();
            if(orderBy.get(0).equals("distancia")){
                Collections.sort(obj, new DistanceComparator(latitud, longitud));
            }
            if(limit!=0) {
                for (int i=0; i<limit&&i<obj.size();i++){
                    o.add(obj.get(i));
                }
            }else
                o= obj;

            return o;
        }
        catch (Exception ex) {
            // Very important: if you want that an exception reaches the EJB caller, you have to throw an EJBException
            // We catch the normal exception and then transform it in a EJBException
            throw new EJBException(ex.getMessage());
        }
    }

    public List<String> uploadImage(List<InputPart> inPart) {

        List<String> imatges= new ArrayList<String>();
        for (InputPart inputPart : inPart) {
            try {

                // Retrieve headers, read the Content-Disposition header to obtain the original name of the file
                MultivaluedMap<String, String> headers = inputPart.getHeaders();
                String fileName = parseFileName(headers);

                // Handle the body of that part with an InputStream
                InputStream istream = inputPart.getBody(InputStream.class, null);

                fileName = System.getenv("OPENSHIFT_DATA_DIR") + fileName;
                //fileName = "C:\\Users\\Jony Lucena\\" + fileName;

                saveFile(istream, fileName);
                imatges.add(fileName);
            } catch (Exception e) {
                return null;
            }
        }
        return imatges;
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
