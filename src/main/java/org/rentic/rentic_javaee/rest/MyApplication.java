package org.rentic.rentic_javaee.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Collections;
import java.util.Set;


/**
 * Created by Jony Lucena.
 */
@ApplicationPath("/rest")
public class MyApplication extends Application {
  @Override
  public Set<Class<?>> getClasses() {
    return Collections.emptySet();
  }

}
