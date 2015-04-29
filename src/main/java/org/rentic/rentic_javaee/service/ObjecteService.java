package org.rentic.rentic_javaee.service;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.rentic.rentic_javaee.model.Objecte;
import org.rentic.rentic_javaee.model.User;
import org.rentic.rentic_javaee.util.DistanceComparator;
import org.rentic.rentic_javaee.util.FromJSONObject;

import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Stateless
@LocalBean
public class ObjecteService {

    @PersistenceContext
    private EntityManager em;

    private static final String FILE_PATH = "http://rentic-rentic.rhcloud.com/rest/images/";
    //private static final String FILE_PATH = "http://localhost:8080/rest/images/";

    public Objecte addObjecte(MultipartFormDataInput input, Long userId) throws Exception {

        Map<String, List<InputPart>> formParts = input.getFormDataMap();
        String objecte = formParts.get("objecte").get(0).getBodyAsString();

        Boolean fi=false;
        int i=0;
        List<InputPart> inPart=new ArrayList<InputPart>();
        while (!fi) {
            String file="file"+i;
            List<InputPart> imatge = formParts.get(file);
            if (imatge!=null) {
                inPart.add(imatge.get(0));
                i++;
            } else
                fi=true;
        }

        User user = em.find(User.class, userId);

        Objecte o= FromJSONObject.getObject(Objecte.class, objecte);
        o.setUser(user);
        if(!inPart.isEmpty())
            o.setImatges(uploadImage(inPart,o.getNom()));


        user.addObjecte(o);

        em.persist(o);


        return o;
    }

    public Objecte updateObjecte(MultipartFormDataInput input, Long userId) throws Exception {

        Map<String, List<InputPart>> formParts = input.getFormDataMap();
        String objecte = formParts.get("objecte").get(0).getBodyAsString();

        Boolean fi=false;
        int i=0;
        List<InputPart> inPart=new ArrayList<InputPart>();
        while (!fi) {
            String file="file"+i;
            List<InputPart> imatge = formParts.get(file);
            if (imatge!=null) {
                inPart.add(imatge.get(0));
                i++;
            } else
                fi=true;
        }

        Objecte o= FromJSONObject.getObject(Objecte.class, objecte);
        Objecte aux= em.find(Objecte.class, o.getId());

        if(aux.getUser().getId()==userId) {
            em.detach(o);
            if (!inPart.isEmpty())
                o.setImatges(uploadImage(inPart, o.getNom()));
            em.merge(o);

            return o;
        }else
            return null;

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

    public List<String> uploadImage(List<InputPart> inPart, String nom) {

        List<String> imatges= new ArrayList<String>();
        int i=0;
        for (InputPart inputPart : inPart) {
            try {

                String fileName = "imatgeObjecte"+i+"_"+nom+"_"+Math.random()+".jpg";

                // Handle the body of that part with an InputStream
                InputStream istream = inputPart.getBody(InputStream.class, null);

                String serverFileName = System.getenv("OPENSHIFT_DATA_DIR") + fileName;

                saveFile(istream, serverFileName);
                imatges.add(FILE_PATH+fileName);
            } catch (Exception e) {
                return null;
            }
            i++;
        }
        return imatges;
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
