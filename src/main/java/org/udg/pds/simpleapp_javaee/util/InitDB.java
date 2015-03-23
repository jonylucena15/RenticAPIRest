/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.udg.pds.simpleapp_javaee.util;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.management.relation.Role;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.udg.pds.simpleapp_javaee.model.User;

@Singleton
@Startup
public class InitDB {

    @Inject
    private Logger log;

    @PersistenceContext
    private EntityManager em;

    @PostConstruct
    private void init() {
        log.log(Level.INFO, "INIT PDS DATABASE");

        try {
            User exists = em.find(User.class, 1L);
            if (exists == null) {
                User u = new User();
                u.setUsername("yo");
                u.setPassword("malament");
                u.setEmail("yo@hotmail.com");
                em.persist(u);
                em.flush();
            } else {
                log.log(Level.INFO, "Initial user already exists");
            }

        } catch (Exception ex) {
            log.log(Level.INFO, "Error initializing database");
        }
    }
}
