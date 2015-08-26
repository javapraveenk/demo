package com.sapient.soa.demo.core;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.springframework.web.context.support.WebApplicationContextUtils;



public class ServiceRegistrarListener implements ServletContextListener {
  private static final Logger LOG = Logger.getLogger(ServiceRegistrarListener.class);
  
  @Override
  public void contextDestroyed(ServletContextEvent sc) {  
    
  }

  @Override
  public void contextInitialized(ServletContextEvent sc) {
    try {
      WebApplicationContextUtils.getRequiredWebApplicationContext(sc.getServletContext())
      .getBean(ServiceRegistrar.class).registerService();
    }
    catch (Exception e) {
      LOG.error("Error Registering Service", e);
      throw new RuntimeException("Exception Registring Service", e);
    }   
  }
}
