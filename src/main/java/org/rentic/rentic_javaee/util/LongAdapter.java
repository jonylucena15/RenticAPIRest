package org.rentic.rentic_javaee.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;


/**
 * Created by Jony Lucena.
 */
public class LongAdapter extends XmlAdapter<String,Long> {
    public Long unmarshal(String val) throws Exception {
        return Long.parseLong(val);
    }
    public String marshal(Long val) throws Exception {
        return val.toString();
    }
}
