package com.sapient.soa.demo.test;

import java.util.Set;

import com.sapient.soa.demo.exception.ProductNotFoundException;




/**
 * A Price Client Contract
 */
public interface PriceClient  {
  Set<String> getServiceInstancesUsed();
  Long getPriceByProductId(Long productId) throws ProductNotFoundException;
}
