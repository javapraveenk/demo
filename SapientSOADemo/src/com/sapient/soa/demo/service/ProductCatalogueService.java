package com.sapient.soa.demo.service;

import java.util.List;

import com.sapient.soa.demo.vo.Product;



public interface ProductCatalogueService {
  Long add(Product product);

  Product get(Long id);

  List<Product> getAll();

  void update(Long id, Product product);

  void delete(Long productId);
}
