package com.sapient.soa.demo.rest.resource;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.context.annotation.Import;

import com.sapient.soa.demo.core.ServiceConfig;

@Retention(RetentionPolicy.RUNTIME)
@Import(ServiceConfig.class)
public @interface EnableProductsService {
}
