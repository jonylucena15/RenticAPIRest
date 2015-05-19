package org.rentic.rentic_javaee.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Jony Lucena on 14/05/2015.
 */
@Entity
public class Conversa implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID_CONVERSA")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;


    @Column(name = "OBJECTE_ID", insertable = false, updatable = false)
    private Long objectId;

    @ManyToOne
    @JoinColumn(name = "OBJECTE_ID", nullable=false)
    @JsonIgnore
    private Objecte objecte;

    @OneToMany(fetch=FetchType.EAGER,cascade = CascadeType.ALL, mappedBy = "conversa")
    private Collection<Missatge> missatges;


    @ManyToMany (cascade = CascadeType.ALL)
    private Collection<User> usuaris;


    public Conversa(){
        objectId=null;
        objecte=null;
        missatges=new ArrayList<Missatge>();
        usuaris=new ArrayList<User>();
    }

    public Long getId() { return id; }

    public void setId(Long id) {  this.id = id; }

    public Collection<User> getUsuaris() { return usuaris; }

    public void setUsuaris(Collection<User> users2) { this.usuaris = users2; }

    public Objecte getObjecte() { return objecte;  }

    public void setObjecte(Objecte objecte) { this.objecte = objecte;  }

    public Long getObjectId() {return objectId;}

    public void setObjectId(Long objectId){this.objectId=objectId;}

    public Collection<Missatge> getMissatges() {
        missatges.size();
        return missatges; }

    public void setMissatges(List<Missatge> missatges) { this.missatges = missatges; }

    public void addMissatge(Missatge missatge) {  missatges.add(missatge); }



}
