package com.sapient.soa.demo.rest.resource;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.sapient.soa.demo.exception.ProductNotFoundException;
import com.sapient.soa.demo.service.ProductCatalogueService;
import com.sapient.soa.demo.vo.Product;




@Path("/prices")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class PricesResource {
  private static final Logger LOG = Logger.getLogger(PricesResource.class);
  
  @Inject
  private ProductCatalogueService productCatalogueService;
  
  @Path("/{productId}")
  public PriceResource priceResource(@Context ResourceContext context) {
    return context.getResource(PriceResource.class);
  }


}
