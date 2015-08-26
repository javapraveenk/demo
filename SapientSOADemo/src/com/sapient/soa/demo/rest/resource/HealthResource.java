package com.sapient.soa.demo.rest.resource;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.sapient.soa.demo.service.ProductCatalogueHealthService;





@Path("/health")
public class HealthResource {
  private final ProductCatalogueHealthService healthService;

  @Inject
  public HealthResource(ProductCatalogueHealthService healthService) {
    this.healthService = healthService;
  }
  
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String isHealthy() {
    return healthService.isHealthy() ? "OK" : "Not OK";
  }
}
