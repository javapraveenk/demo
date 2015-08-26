package com.sapient.soa.demo.service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.collect.Lists;
import com.sapient.soa.demo.vo.Product;


public class ProductCatalogueServiceImpl implements ProductCatalogueService {
  private final ConcurrentHashMap<Long, Product> productMap = new ConcurrentHashMap<Long, Product>();
  private final AtomicLong seq = new AtomicLong();
  
  public ProductCatalogueServiceImpl() {
  }
  
  @Override
  public Long add(Product product) {
    Long productId = seq.incrementAndGet();
    productMap.put(productId, product);  
    
    return productId;
  }
  
  @Override
  public Product get(Long productId) {
    return productMap.get(productId);
  }

  @Override
  public void update(Long id, Product product) {
	  productMap.put(id, product);
  }

  @Override
  public void delete(Long productId) {
	  productMap.remove(productId);
  }

  @Override
  public List<Product> getAll() {
    return Lists.newArrayList(productMap.values());
  }  
}