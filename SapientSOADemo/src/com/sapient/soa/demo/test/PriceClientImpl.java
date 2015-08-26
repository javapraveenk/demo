package com.sapient.soa.demo.test;

import java.util.Set;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
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

/**
 * Implementation of the Price Client
 */
public class PriceClientImpl implements PriceClient {
  private static final Logger LOG = Logger.getLogger(PriceClientImpl.class);

  public static final String SERVICE_NAME = "prices";

  private final Client webServiceClient;

  private final ServiceDiscoverer serviceDiscoverer;

  private Set<String> urlsUsed = Sets.newHashSet();
   
  public PriceClientImpl() {
	  ClientConfig config = new ClientConfig();
	  webServiceClient = ClientBuilder.newClient(config);
	  serviceDiscoverer= null;
  }
  /**
   * @param getProductsServerUrl() Server URI
   * @throws Exception
   */
  public PriceClientImpl(String zookeeperAddress) throws Exception {
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




  /**
   * Gets a Price using the preferred representation type of JSON
   */
  @Override
  public Long getPriceByProductId(Long id) throws ProductNotFoundException {
    ServiceInstance<InstanceDetails> instance = serviceDiscoverer.getServiceUrl();
  
    try {
      Response response = webServiceClient
          .target(UriBuilder.fromUri(markInstanceUsed(instance)).path("/prices/" + id).build())
          .request(MediaType.APPLICATION_JSON).get();

      if (response.getStatus() == Status.NOT_FOUND.getStatusCode()) {
        throw new ProductNotFoundException(response.readEntity(String.class));
      }

      return response.readEntity(Product.class).getProductPrice();
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
