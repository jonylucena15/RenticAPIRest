package org.rentic.rentic_javaee.rest;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


  @Provider
  public class DefaultException implements ExceptionMapper<Exception> {


    @Override
    public Response toResponse(Exception e) {
      // For simplicity I am preparing error xml by hand.
      // Ideally we should create an ErrorResponse class to hold the error info.
      return Response.serverError().entity("{\"error\": \"Resource not exists in API\"}").type(MediaType.APPLICATION_JSON_TYPE).build();
    }


  }

