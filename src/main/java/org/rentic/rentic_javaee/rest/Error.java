package org.rentic.rentic_javaee.rest;

/**
 * Created with IntelliJ IDEA.
 * User: imartin
 * Date: 28/03/13
 * Time: 20:24
 * To change this template use File | Settings | File Templates.
 */
public class Error {
    public static String build(String code, String msg) {
        return "{\"code\":"+code+",\"message\":\""+msg+"\",\"data\":{}}" ;
    }
}
