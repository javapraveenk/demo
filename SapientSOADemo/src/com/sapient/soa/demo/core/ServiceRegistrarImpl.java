package com.sapient.soa.demo.core;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.UriSpec;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.apache.log4j.Logger;



public class ServiceRegistrarImpl implements ServiceRegistrar {
  private static final Logger LOG = Logger.getLogger(ServiceRegistrarImpl.class);
  private static final String BASE_PATH = "services";
  private final CuratorFramework client;
  
  private ServiceDiscovery<InstanceDetails> serviceDiscovery;
  
  private ServiceInstance<InstanceDetails> thisInstance;
  
  private final JsonInstanceSerializer<InstanceDetails> serializer;
  
 
  
  public ServiceRegistrarImpl(CuratorFramework client,String serviceName) throws NumberFormatException, UnknownHostException, Exception {
    this(client, Integer.parseInt(System.getProperty("port")),serviceName);
  }
  
  public ServiceRegistrarImpl(CuratorFramework client, int servicePort,String serviceName) throws UnknownHostException, Exception {
    this.client = client;
    serializer = new JsonInstanceSerializer<>(InstanceDetails.class);
    
    UriSpec uriSpec = new UriSpec("{scheme}://{address}:{port}");
      
    thisInstance = ServiceInstance.<InstanceDetails>builder().name(serviceName)
      .uriSpec(uriSpec)
      .address(InetAddress.getLocalHost().getHostAddress())
      .payload(new InstanceDetails()).port(servicePort)
      .build();
  }
  
  @Override 
  public void close() throws IOException {
    try {
      serviceDiscovery.close();
    }
    catch (Exception e) {
      LOG.info("Error Closing Discovery", e);
    }
  }

  @Override 
  public void registerService() throws Exception {
    client.blockUntilConnected();
    serviceDiscovery = ServiceDiscoveryBuilder.builder(InstanceDetails.class)
        .client(client)
        .basePath(BASE_PATH).serializer(serializer).thisInstance(thisInstance)
        .build();
    serviceDiscovery.start();
  }
}
