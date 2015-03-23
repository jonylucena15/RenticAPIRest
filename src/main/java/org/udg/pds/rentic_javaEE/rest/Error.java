package org.udg.pds.rentic_javaEE.rest;

/**
 * Created with IntelliJ IDEA.
 * User: imartin
 * Date: 28/03/13
 * Time: 20:24
 * To change this template use File | Settings | File Templates.
 */
public class Error {
    public static String build(String msg) {
        return "{\"msg\":\"" + msg + "\"}";
    }
}
