package com.sapient.soa.demo.core;

import java.net.UnknownHostException;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;

import com.sapient.soa.demo.service.ProductCatalogueHealthService;
import com.sapient.soa.demo.service.ProductCatalogueHealthServiceImpl;
import com.sapient.soa.demo.service.ProductCatalogueService;
import com.sapient.soa.demo.service.ProductCatalogueServiceImpl;



@Configuration
@PropertySource("classpath:/application.properties")
public class ServiceConfigPrice {
  
	 public static final String SERVICE_NAME = "prices";
  
  @Bean
  public ProductCatalogueService productCatalogueServiceImpl() {
    return new ProductCatalogueServiceImpl();
  }
  
  @Bean
  public ProductCatalogueHealthService health() {
    return new ProductCatalogueHealthServiceImpl();
  }
  
  @Bean(initMethod = "start", destroyMethod = "close")
  @Scope("prototype")
  public CuratorFramework curatorClient() {
	  return CuratorFrameworkFactory.newClient("127.0.0.1", new ExponentialBackoffRetry(3000,3));
  }
  
  @Bean(destroyMethod = "close")
  public ServiceRegistrar serviceRegistrar() throws UnknownHostException, Exception {
    return new ServiceRegistrarImpl(curatorClient(),SERVICE_NAME);
  }
}
