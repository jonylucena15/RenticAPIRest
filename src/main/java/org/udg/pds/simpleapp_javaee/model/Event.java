package org.udg.pds.simpleapp_javaee.model;

import org.udg.pds.simpleapp_javaee.util.LongAdapter;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: imartin
 * Date: 9/03/13
 * Time: 20:32
 * To change this template use File | Settings | File Templates.
 */

@Entity
public class Event implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "BEFORE_ID", insertable = false, updatable = false)
    private Long beforeid;

    @OneToOne(optional=true, fetch = FetchType.LAZY)
    @JoinColumn(name = "BEFORE_ID")
    private Event before;

    @OneToOne(optional=true, mappedBy="before", fetch = FetchType.LAZY)
    private Event after;

    public void setEventBefore(Event e) {
        before = e;
    }

    public Event getAfter() {
        return after;
    }

    public Long getId() {
        return id;
    }
}
