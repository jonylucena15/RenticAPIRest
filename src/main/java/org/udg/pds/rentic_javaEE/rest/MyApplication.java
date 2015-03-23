package org.udg.pds.rentic_javaEE.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Collections;
import java.util.Set;


@ApplicationPath("/rest")
public class MyApplication extends Application {
  @Override
  public Set<Class<?>> getClasses() {
    return Collections.emptySet();
  }

}
