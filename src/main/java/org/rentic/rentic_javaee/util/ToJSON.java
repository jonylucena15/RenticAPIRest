/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rentic.rentic_javaee.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.rentic.rentic_javaee.model.Task;
import org.rentic.rentic_javaee.model.User;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author imartin
 */
@Singleton
public class ToJSON {


    @PostConstruct
    void init() {
        try {
            User = JAXBContext.newInstance(org.rentic.rentic_javaee.model.User.class).createMarshaller();
            //User.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
        } catch (JAXBException ex) {
            Logger.getLogger(ToJSON.class.getName()).log(Level.SEVERE, "Cannot initialize User marshaller!", ex);
        }
    }



    public String User(User u) throws IOException {
        StringWriter sw = new StringWriter();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(sw,u);

        return sw.toString();

    }

    public String Task(Task u) throws IOException {
        StringWriter sw = new StringWriter();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(sw, u);
        return sw.toString();

    }

    public String Object(Object o) throws IOException {
        StringWriter sw = new StringWriter();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(sw, o);
        return sw.toString();

    }

    public static Marshaller User;
}
