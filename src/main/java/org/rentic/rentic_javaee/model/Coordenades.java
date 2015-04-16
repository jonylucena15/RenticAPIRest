package org.rentic.rentic_javaee.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Coordenades implements Serializable {

    /**
     * Default value included to remove warning. Remove or modify at will. *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    private float longitud;

    private float latitud;

    @OneToOne(fetch = FetchType.LAZY,optional=false,mappedBy="coordenades")
    @JsonIgnore
    private Objecte objecte;

    public Objecte getObjecte() { return objecte; }

    public Coordenades(){
        longitud=0;
        latitud=0;
    }

    public Coordenades(float longitud, float latitud){
        this.longitud=longitud;
        this.latitud=latitud;
    }

    public float getLongitud() {
        return longitud;
    }

    public void setLongitud(float longitud) {
        this.longitud = longitud;
    }

    public float getLatitud() {
        return latitud;
    }

    public void setLatitud(float latitud) {
        this.latitud = latitud;
    }
}
