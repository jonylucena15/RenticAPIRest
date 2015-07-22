package org.rentic.rentic_javaee.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by Jony Lucena.
 */
public class DateAdapter extends XmlAdapter<String, Date> {

    public String marshal(Date date) throws Exception {
        SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd");
        return formate.format(date);
    }

    public Date unmarshal(String dateString) throws Exception {
        SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd");

        return  formate.parse(dateString);
    }

    public String compararDate(String data1, String dataActual) throws Exception{
        DateAdapter d = new DateAdapter();
        String resultat="";
        java.util.Date dataDate1 = d.unmarshal(data1);
        java.util.Date dataDate2 = d.unmarshal(dataActual);

        if ( dataDate1.before(dataDate2) )
        resultat=data1;
        else
        resultat=dataActual;

        return resultat;
    }

    public Double diferenciaDies(String data1, String data2)throws Exception{

        DateAdapter d = new DateAdapter();
        java.util.Date dataDate1 = d.unmarshal(data1);
        java.util.Date dataDate2 = d.unmarshal(data2);


        return Double.valueOf((dataDate2.getTime() - dataDate1.getTime())/ (1000 * 60 * 60 * 24));





    }
}