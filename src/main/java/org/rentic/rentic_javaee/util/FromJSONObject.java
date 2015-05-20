package org.rentic.rentic_javaee.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by Jony Lucena on 20/04/2015.
 */
public class FromJSONObject {


    public static  <T> T getObject(Class <?>  c, String o ) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return  (T) mapper.readValue(o, c);

    }
}
