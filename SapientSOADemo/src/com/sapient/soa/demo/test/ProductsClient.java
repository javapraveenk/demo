package com.sapient.soa.demo.test;

import java.util.List;
import java.util.Set;

import com.sapient.soa.demo.exception.ProductNotFoundException;
import com.sapient.soa.demo.vo.Product;
import com.sapient.soa.demo.vo.ProductResult;




/**
 * A Products Client Contract
 */
public interface ProductsClient  {
  Set<String> getServiceInstancesUsed();
  
  ProductResult add(Product product);
  
  List<Product> getAll();

  void update(Long productId, Product product);

  Product get(Long productId) throws ProductNotFoundException;

  void delete(Long productId);
}
