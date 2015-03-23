/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.udg.pds.rentic_javaEE.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.udg.pds.rentic_javaee.model.User;

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
