/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rentic.rentic_javaee.util;

import org.rentic.rentic_javaee.model.User;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Created by Jony Lucena.
 */
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
                u.setPassword("admin");
                u.setNomComplet("Usuari Administrador");
                u.setEmail("shoni__7@hotmail.com");
                em.persist(u);
                em.flush();
            } else {
                log.log(Level.INFO, "Initial user1 already exists");
            }

            exists = em.find(User.class, 2L);
            if (exists == null) {
                User u = new User();
                u.setPassword("client");
                u.setNomComplet("Client prova");
                u.setEmail("jonylucena15@gmail.com");
                em.persist(u);
                em.flush();
            } else {
                log.log(Level.INFO, "Initial user2 already exists");
            }
        } catch (Exception ex) {
            log.log(Level.INFO, "Error initializing database");
        }
    }
}
