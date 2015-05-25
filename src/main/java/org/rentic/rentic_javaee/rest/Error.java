package org.rentic.rentic_javaee.rest;


/**
 * Created by Jony Lucena.
 */
public class Error {
    public static String build(String code, String msg) {
        return "{\"code\":"+code+",\"message\":\""+msg+"\",\"data\":{}}" ;
    }
}
