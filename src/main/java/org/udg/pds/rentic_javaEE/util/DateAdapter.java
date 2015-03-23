package org.udg.pds.rentic_javaEE.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: imartin
 * Date: 28/03/13
 * Time: 23:40
 * To change this template use File | Settings | File Templates.
 */
public class DateAdapter extends XmlAdapter<String, Date> {

    // the desired format
    private String pattern = "yyyy-MM-dd HH:mm:ss z";

    public String marshal(Date date) throws Exception {
        return new SimpleDateFormat(pattern).format(date);
    }

    public Date unmarshal(String dateString) throws Exception {
        return new SimpleDateFormat(pattern).parse(dateString);
    }
}