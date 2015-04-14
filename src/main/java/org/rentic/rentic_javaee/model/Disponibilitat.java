package org.rentic.rentic_javaee.model;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;
@Entity
public class Disponibilitat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Temporal(javax.persistence.TemporalType.DATE)
    private Date inici;

    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fi;

    @Column(name = "idObjecte", insertable = false, updatable = false)
    private Long idObjecte;

    @ManyToOne
    @JoinColumn(name = "idObjecte", nullable=false)
    @JsonIgnore
    private Objecte objecte;

    public Objecte getObjecte() {
        return objecte;
    }

    public void setObjecte(Objecte objecte) {
        this.objecte = objecte;
    }


    public Disponibilitat(){
        this.inici=null;
        this.fi=null;
    }

    public Disponibilitat(Date inici, Date fi) {
        this.inici = inici;
        this.fi = fi;
    }

    public Date getInici() {
        return inici;
    }

    public void setInici(Date inici) {
        this.inici = inici;
    }

    public Date getFi() {
        return fi;
    }

    public void setFi(Date fi) {
        this.fi = fi;
    }
}
