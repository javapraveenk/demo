package com.sapient.soa.demo.rest.resource;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.glassfish.jersey.servlet.ServletContainer;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import com.sapient.soa.demo.core.ServiceRegistrarListener;


@Order(Ordered.HIGHEST_PRECEDENCE)
public class PriceApplicationInitializer implements WebApplicationInitializer {

  @Override
  public void onStartup(ServletContext ctx) throws ServletException {
    // Listeners
    ctx.addListener(ContextLoaderListener.class);
    ctx.addListener(ServiceRegistrarListener.class);
    ctx.setInitParameter(ContextLoader.CONTEXT_CLASS_PARAM,
      AnnotationConfigWebApplicationContext.class.getName());
    ctx.setInitParameter(ContextLoader.CONFIG_LOCATION_PARAM, SpringConfigPrice.class.getName());

    // Register Jersey 2.0 servlet
    ServletRegistration.Dynamic servletRegistration = ctx.addServlet("Prices",
      ServletContainer.class.getName());

    servletRegistration.addMapping("/*");
    servletRegistration.setLoadOnStartup(1);
    servletRegistration.setInitParameter("javax.ws.rs.Application",
    ProductsApplication.class.getName());
  }
}
