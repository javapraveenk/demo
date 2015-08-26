package com.sapient.soa.demo.test;

import java.util.List;
import java.util.Set;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;

import com.google.common.collect.Sets;
import com.sapient.soa.demo.core.InstanceDetails;
import com.sapient.soa.demo.core.ServiceDiscoverer;
import com.sapient.soa.demo.exception.ProductNotFoundException;
import com.sapient.soa.demo.vo.Product;
import com.sapient.soa.demo.vo.ProductResult;

/**
 * Implementation of the Products Client
 */
public class ProductsClientImpl implements ProductsClient {
  private static final Logger LOG = Logger.getLogger(ProductsClientImpl.class);

  public static final String SERVICE_NAME = "products";

  private final Client webServiceClient;

  private final ServiceDiscoverer serviceDiscoverer;

  private Set<String> urlsUsed = Sets.newHashSet();
   
  public ProductsClientImpl() {
	  ClientConfig config = new ClientConfig();
	  webServiceClient = ClientBuilder.newClient(config);
	  serviceDiscoverer= null;
  }
  /**
   * @param getProductsServerUrl() Server URI
   * @throws Exception
   */
  public ProductsClientImpl(String zookeeperAddress) throws Exception {
    serviceDiscoverer = new ServiceDiscoverer(zookeeperAddress, SERVICE_NAME);
    serviceDiscoverer.start();

    ClientConfig config = new ClientConfig();
    webServiceClient = ClientBuilder.newClient(config);
  }
  
  private String markInstanceUsed(ServiceInstance<InstanceDetails> instance) {
    String uri = instance.buildUriSpec();
    LOG.info("Connecting to:" + uri);
    urlsUsed.add(uri);    
    return uri;
  }

  @Override
  public ProductResult add(Product product) {
    ServiceInstance<InstanceDetails> instance = serviceDiscoverer.getServiceUrl();
    
    try {
      return webServiceClient
          .target(UriBuilder.fromUri(markInstanceUsed(instance)).path("/products").build())
          .request(MediaType.APPLICATION_XML)
          .post(Entity.entity(product, MediaType.APPLICATION_XML_TYPE), ProductResult.class);
    }
    catch (ProcessingException e) {
      serviceDiscoverer.noteError(instance);
      throw e;
    }
  }
  


  /**
   * Gets a Product using the preferred representation type of JSON
   */
  @Override
  public Product get(Long id) throws ProductNotFoundException {
    ServiceInstance<InstanceDetails> instance = serviceDiscoverer.getServiceUrl();
  
    try {
      Response response = webServiceClient
          .target(UriBuilder.fromUri(markInstanceUsed(instance)).path("/products/" + id).build())
          .request(MediaType.APPLICATION_JSON).get();

      if (response.getStatus() == Status.NOT_FOUND.getStatusCode()) {
        throw new ProductNotFoundException(response.readEntity(String.class));
      }

      return response.readEntity(Product.class);
    }
    catch (ProcessingException e) {
      serviceDiscoverer.noteError(instance);
      throw e;
    }
  }

  @Override
  public void update(Long productId, Product product) {
    ServiceInstance<InstanceDetails> instance = serviceDiscoverer.getServiceUrl();

    try {
      webServiceClient
          .target(UriBuilder.fromUri(markInstanceUsed(instance)).path("/products/" + productId).build())
          .request().put(Entity.entity(product, MediaType.APPLICATION_XML));
    }
    catch (ProcessingException e) {
      serviceDiscoverer.noteError(instance);
      throw e;
    }
  }

  @Override
  public void delete(Long productId) {
    ServiceInstance<InstanceDetails> instance = serviceDiscoverer.getServiceUrl();
    try {
      webServiceClient
          .target(UriBuilder.fromUri(markInstanceUsed(instance)).path("/products/" + productId).build())
          .request().delete();
    }
    catch (ProcessingException e) {
      serviceDiscoverer.noteError(instance);
      throw e;
    }
  }

  @Override
  public List<Product> getAll() {
    ServiceInstance<InstanceDetails> instance = serviceDiscoverer.getServiceUrl();
    try {
      return webServiceClient
          .target(UriBuilder.fromUri(markInstanceUsed(instance)).path("/products").build())
          .request(MediaType.APPLICATION_XML).get(new GenericType<List<Product>>() {});

    }
    catch (ProcessingException e) {
      serviceDiscoverer.noteError(instance);
      throw e;
    }
  }

  @Override
  public Set<String> getServiceInstancesUsed() {
    return urlsUsed;
  }
}
