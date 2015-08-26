package com.sapient.soa.demo.rest.resource;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;

import com.sapient.soa.demo.service.ProductCatalogueService;
import com.sapient.soa.demo.vo.Product;
import com.sapient.soa.demo.vo.ProductResult;




@Path("/products")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class ProductsResource {
  private static final Logger LOG = Logger.getLogger(ProductsResource.class);

  private final UriInfo uriInfo;

  private final ProductCatalogueService productCatalogueService;
  
  @Inject
  public ProductsResource(@Context UriInfo uriInfo, ProductCatalogueService productCatalogueService) {
    this.uriInfo = uriInfo;
    this.productCatalogueService = productCatalogueService;
  }
  
  @POST
  public Response add(Product product) {
    LOG.debug("adding a product:" + product);
    ProductResult result = addProduct(product);
    
    return Response.created(result.getLocation()).entity(result).build();
  }
  
  private ProductResult addProduct(Product product) {
    Long productId = productCatalogueService.add(product);
   
    URI createdLocation = UriBuilder.fromPath(uriInfo.getAbsolutePath() + "/" + productId).build();
    ProductResult result = new ProductResult(productId, createdLocation);

    return result;
  }
 
  @GET
  public List<Product> getProducts() {
    return productCatalogueService.getAll();
  }
  
  @Path("/{productId}")
  public ProductResource productResource(@Context ResourceContext context) {
    return context.getResource(ProductResource.class);
  }
}
