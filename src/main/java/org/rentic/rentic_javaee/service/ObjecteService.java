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




    @XmlRootElement(name="collection")
    @XmlAccessorType(XmlAccessType.FIELD)
    public class ObjectList {
        public Collection<Objecte> objectes;
    }

    public Objecte addObjecte(String i, Long userId,List<InputPart> inPart) throws Exception {

        User user = em.find(User.class, userId);

        Objecte o= FromJSONObject.getObject(Objecte.class, i);
        o.setImatges(uploadImage(inPart));
        o.setUser(user);
        user.addObjecte(o);

        em.persist(o);



        return o;
    }

    public ObjectList getObjectes(Long id) {
        ObjectList o = new ObjectList();
        try {
            User u = em.find(User.class, id);
            o.objectes = u.getObjectes();
            return o;
        }
        catch (Exception ex) {
            // Very important: if you want that an exception reaches the EJB caller, you have to throw an EJBException
            // We catch the normal exception and then transform it in a EJBException
            throw new EJBException(ex.getMessage());
        }
    }

    public  Objecte getObjecte(Long id) throws Exception {
        return em.find(Objecte.class, id);
    }

    public  Boolean deleteObjecte(Long id, Long userID) throws Exception {
        Objecte o= em.find(Objecte.class, id);
        if (o.getUser().getId()==userID){
            em.remove(o);
            return true;

        }
        return false;
    }

    public ObjectList getObjectes(Long idUser, int limit, List<String> orderBy, Double latitud, Double longitud) {
        ObjectList o = new ObjectList();
        Query q=null;

        if (idUser!=null){
            /*if(orderBy!=null){
                q = em.createQuery("select obj from  Objecte obj where obj.userId=:idUsers" );
                q.setParameter("orderby",orderBy[0]);
            }else
             */
            q = em.createQuery("select obj from  Objecte obj where obj.userId=:idUsers");
            q.setParameter("idUsers",idUser);
        }else{
            q = em.createQuery("select obj from  Objecte obj");
        }
        if(limit!=0) {q.setMaxResults(limit);}

        try {
            List<Objecte> obj=q.getResultList();
            Collections.sort(obj, new DistanceComparator(latitud,longitud));
            o.objectes = obj;

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

                // fileName = System.getenv("OPENSHIFT_DATA_DIR") + fileName;
                fileName = "C:\\Users\\Jony Lucena\\" + fileName;

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
