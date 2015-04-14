package org.rentic.rentic_javaee.rest;


import javax.persistence.Temporal;
import java.util.Date;

public class Disponibilitat {

    @Temporal(javax.persistence.TemporalType.DATE)
    private Date inici;

    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fi;

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
