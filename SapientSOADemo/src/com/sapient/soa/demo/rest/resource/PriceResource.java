package com.sapient.soa.demo.rest.resource;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.sapient.soa.demo.exception.ProductNotFoundException;
import com.sapient.soa.demo.service.ProductCatalogueService;
import com.sapient.soa.demo.vo.Product;





public class PriceResource {
  private static final Logger LOG = Logger.getLogger(PriceResource.class);
  
  @Inject
  private ProductCatalogueService productCatalogueService;
  
  @GET
  @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public Product get(@PathParam("productId") Long productId) throws ProductNotFoundException {
    LOG.info("Getting product:" + productId);
    Product product = productCatalogueService.get(productId);
    
    if (product == null) {
      LOG.info("Product Id:" + productId + " not found");
      throw new ProductNotFoundException("Not found:" + productId);
    }
    
    return product;
  }


}
