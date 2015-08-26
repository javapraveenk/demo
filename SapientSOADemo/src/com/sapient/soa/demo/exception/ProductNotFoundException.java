package com.sapient.soa.demo.exception;

public class ProductNotFoundException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ProductNotFoundException(String message) {
    super(message);
  }
}
