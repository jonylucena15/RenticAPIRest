package org.rentic.rentic_javaee.util;

import org.rentic.rentic_javaee.model.Objecte;

import java.util.Comparator;


/**
 * Created by Jony Lucena.
 */
public class DistanceComparator implements Comparator<Objecte>  {
    Double latitud;
    Double longitud;

    public DistanceComparator(Double latitud, Double longitud){
        this.latitud=latitud;
        this.longitud=longitud;
    }

    @Override
    public int compare(Objecte obj1, Objecte obj2) {
        if(distFrom(obj1.getLatitud(), obj1.getLongitud(), latitud, longitud) < distFrom(obj2.getLatitud(), obj2.getLongitud(), latitud, longitud))
            return -1;
        else if (distFrom(obj1.getLatitud(), obj1.getLongitud(), latitud, longitud) > distFrom(obj2.getLatitud(), obj2.getLongitud(),latitud, longitud))
            return 1;
        else return 0;
    }

    public double distFrom(Number lat1, Number lng1, double lat2, double lng2) {
        double earthRadius = 6371;//kilometers
        double dLat = Math.toRadians((Double) lat2 - (Double) lat1);
        double dLng = Math.toRadians((Double)lng2 - (Double)lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)* Math.cos(Math.toRadians((Double)lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;

        return dist;
    }
}