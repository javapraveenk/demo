package com.sapient.soa.demo.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;


public class ProductNotFoundExceptionMapper implements ExceptionMapper<ProductNotFoundException> {
  @Override
  public Response toResponse(ProductNotFoundException exception) {
    return Response.status(Status.NOT_FOUND.getStatusCode()).entity(exception.getMessage()).build();
  }
}
