package org.rentic.rentic_javaee.service;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.rentic.rentic_javaee.model.Disponibilitat;
import org.rentic.rentic_javaee.model.Lloguer;
import org.rentic.rentic_javaee.model.Objecte;
import org.rentic.rentic_javaee.model.User;
import org.rentic.rentic_javaee.util.DateAdapter;
import org.rentic.rentic_javaee.util.DistanceComparator;
import org.rentic.rentic_javaee.util.FromJSONObject;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.*;
import java.util.*;


/**
 * Created by Jony Lucena.
 */
@Stateless
@LocalBean
public class ObjecteService {

    @PersistenceContext
    private EntityManager em;

    private static final String FILE_PATH = "http://servidor-pds3.rhcloud.com/rest/images/";


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
            o.setImatges(uploadImage(inPart));

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
        User u=aux.getUser();
        if(aux.getUser().getId().intValue()==userId.intValue()) {
            em.detach(o);
            if (!inPart.isEmpty())
                o.setImatges(uploadImage(inPart));
            o.setUser(u);
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
            if (o.getUser().getId().intValue() == userID.intValue()) {
                em.remove(o);
                return true;
            }
        }
        return false;
    }
    public List<Objecte> getObjectesUsuari(Long idUser) {
        Query q = null;
        List<Objecte> obj =  new ArrayList<Objecte>();
        try {

            q = em.createQuery("select obj from  Objecte obj where obj.userId=:idUsers");
            q.setParameter("idUsers", idUser);
            obj = q.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return obj;
    }


    public List<Objecte> getObjectes(int limit, List<String> orderBy, Double latitud, Double longitud) {
        List <Objecte> o = new ArrayList<Objecte>();
        DateAdapter d = new DateAdapter();
        Query q=null;
        try {
            q = em.createQuery("select obj from  Objecte obj");
            List<Objecte> obj=q.getResultList();

            Date dataSistema = new Date();
            String dSistema= d.marshal(dataSistema);

            int n=obj.size();
            int i=0;
            while(i<n) {
                Boolean compleix = (obj.get(i).getDispCapDeSetmana() == true || obj.get(i).getDispEntreSetmana() == true);

                if (!compleix) {
                    List<Disponibilitat> disp = obj.get(i).getDispRangs();
                    int nx = 0;
                    while (!compleix && nx < disp.size()) {

                        compleix =disp.get(nx).getDataFi().equals(dSistema) || !(d.compararDate(disp.get(nx).getDataFi(), dSistema).equals(disp.get(nx).getDataFi()));
                        nx++;
                    }
                }

                if (compleix) {
                    List<Lloguer> l = (List<Lloguer>) obj.get(i).getLloguers();

                    List<Lloguer> aux = new ArrayList<Lloguer>();
                    for (int j = 0; j < l.size(); j++) {
                        if (l.get(j).getDataFi().equals(dSistema) || !d.compararDate(l.get(j).getDataFi(), dSistema).equals(l.get(j).getDataFi()))
                            aux.add(l.get(j));
                    }
                    obj.get(i).setLloguers(aux);
                    i++;
                } else {
                    obj.remove(i);
                    n--;
                }
            }

            if(!orderBy.isEmpty() && orderBy.get(0).equals("distancia")){
                Collections.sort(obj, new DistanceComparator(latitud, longitud));
            }
            if(limit!=0) {
                for (int t=0; t<limit&&t<obj.size();t++){
                    o.add(obj.get(t));
                }
            }else
                o= obj;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return o;
    }


    public Lloguer addLloguer(String dataInici, String dataFi, Long idObjecte, Long idUsuari, Long idArrendador) throws Exception {
        User userP=em.find(User.class, idArrendador);
        Objecte objecte = em.find(Objecte.class, idObjecte);
        if (objecte.getUser().getId().intValue()==userP.getId().intValue()) {
            User user = em.find(User.class, idUsuari);

            Lloguer llog = new Lloguer();
            llog.setDataFi(dataFi);
            llog.setDataInici(dataInici);
            llog.setUser(user);
            llog.setObjecte(objecte);

            user.addLloguer(llog);
            objecte.addLloguer(llog);

            em.persist(llog);

            return llog;
        }else
            return null;
    }

    private List<String> uploadImage(List<InputPart> inPart) {

        List<String> imatges= new ArrayList<String>();
        int i=0;
        for (InputPart inputPart : inPart) {
            try {
                String fileName = "imatgeObjecte"+i+"_"+Math.random()+".jpg";

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
