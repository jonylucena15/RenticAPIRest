package org.rentic.rentic_javaee.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Created with IntelliJ IDEA.
 * User: imartin
 * Date: 28/03/13
 * Time: 13:27
 * To change this template use File | Settings | File Templates.
 */

public class LongAdapter extends XmlAdapter<String,Long> {
    public Long unmarshal(String val) throws Exception {
        return Long.parseLong(val);
    }
    public String marshal(Long val) throws Exception {
        return val.toString();
    }
}
