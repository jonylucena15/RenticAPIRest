package org.rentic.rentic_javaee.rest;


public class Coordenades {

    private float longitud;
    private float latitud;

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
