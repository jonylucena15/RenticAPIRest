package org.rentic.rentic_javaee.model;

import javax.persistence.*;

@Entity
public class Coordenades {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    private float longitud;

    private float latitud;

    @OneToOne(optional=false)
    @JoinColumn(name="idObjecte", unique=true, nullable=false, updatable=false)
    public Objecte objecte;
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
