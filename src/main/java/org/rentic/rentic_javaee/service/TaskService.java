package org.rentic.rentic_javaee.service;

import java.util.Collection;
import java.util.Date;
import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.rentic.rentic_javaee.model.Task;
import org.rentic.rentic_javaee.model.User;

@Stateless
@LocalBean
public class TaskService {
    @PersistenceContext
    protected EntityManager em;

    @XmlRootElement(name="collection")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class TaskList {
        public Collection<Task> tasks;
    }

    public TaskList getTasks(Long id) {
        TaskList tl = new TaskList();
        try {
            User u = em.find(User.class, id);
            tl.tasks = u.getTasks();
            return tl;
        }
        catch (Exception ex) {
            // Very important: if you want that an exception reaches the EJB caller, you have to throw an EJBException
            // We catch the normal exception and then transform it in a EJBException
            throw new EJBException(ex.getMessage());
        }
    }

    public Task getTask(Long id) throws Exception {
        return em.find(Task.class, id);
    }

    public Task addTask(String text, Long userId,
                        Date created, Date limit) throws Exception {

        User user = em.find(User.class, userId);

        Task task = new Task();

        task.setUser(user);
        task.setText(text);
        task.setDateCreated(created);
        task.setDateLimit(limit);
        task.setCompleted(false);
        
        user.addTask(task);

        em.persist(task);
        return task;
    }
}
