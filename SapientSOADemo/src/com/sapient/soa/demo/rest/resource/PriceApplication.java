package com.sapient.soa.demo.rest.resource;

import java.util.Set;

import javax.ws.rs.core.Application;

import com.google.common.collect.ImmutableSet;
import com.sapient.soa.demo.exception.ProductNotFoundExceptionMapper;


public class PriceApplication extends Application {
  
  @Override
  public Set<Class<?>> getClasses() {
    return ImmutableSet.<Class<?>>of(PricesResource.class, 
      HealthResource.class,
      ProductNotFoundExceptionMapper.class);
  }
}
