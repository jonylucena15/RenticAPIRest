package org.udg.pds.rentic_javaEE.model;

import javax.persistence.*;
import java.io.Serializable;

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
