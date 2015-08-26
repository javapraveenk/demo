package com.sapient.soa.demo.core;

import java.io.Closeable;
import java.util.List;

import jersey.repackaged.com.google.common.collect.Lists;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceProvider;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.apache.log4j.Logger;



public class ServiceDiscoverer {
  private static final Logger LOG = Logger.getLogger(ServiceDiscovery.class);

  private final CuratorFramework curatorClient;

  private final ServiceDiscovery<InstanceDetails> serviceDiscovery;

  private final ServiceProvider<InstanceDetails> serviceProvider;

  private final JsonInstanceSerializer<InstanceDetails> serializer;

  private final List<Closeable> closeAbles = Lists.newArrayList();

  public ServiceDiscoverer(String zookeeperAddress, String serviceName) throws Exception {
    curatorClient = CuratorFrameworkFactory.newClient(zookeeperAddress,
      new ExponentialBackoffRetry(1000, 3));

    serializer = new JsonInstanceSerializer<>(InstanceDetails.class);
    serviceDiscovery = ServiceDiscoveryBuilder.builder(InstanceDetails.class).client(curatorClient)
        .basePath("services").serializer(serializer).build();
    serviceProvider = serviceDiscovery.serviceProviderBuilder().serviceName(serviceName).build();
  }

  public void start() {
    try {
      curatorClient.start();
      closeAbles.add(curatorClient);
      curatorClient.blockUntilConnected();
      serviceDiscovery.start();
      closeAbles.add(0, serviceDiscovery);
      serviceProvider.start();
      closeAbles.add(0, serviceProvider);
    }
    catch (Exception e) {
      throw new RuntimeException("Error starting Service Discoverer", e);
    }
  }

  public void close() {
    for (Closeable closeable : closeAbles) {
      try {
        closeable.close();
      }
      catch (Exception e) {
        LOG.warn("Failed to close cleanly:" + closeable, e);
      }
    }
  }
  
  public void noteError(ServiceInstance<InstanceDetails> instance) {
    serviceProvider.noteError(instance);
  }

  public ServiceInstance<InstanceDetails> getServiceUrl() {
    try {
      ServiceInstance<InstanceDetails> instance = null;
      while ((instance = serviceProvider.getInstance()) == null) {        
        Thread.sleep(200);
      }
      return instance;
    }
    catch (Exception e) {
      throw new RuntimeException("Error obtaining service url", e);
    }
  }
}
