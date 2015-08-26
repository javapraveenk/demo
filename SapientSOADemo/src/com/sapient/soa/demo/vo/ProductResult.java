package com.sapient.soa.demo.vo;

import java.net.URI;

import javax.xml.bind.annotation.XmlRootElement;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@XmlRootElement
@AutoProperty
public class ProductResult {
  private Long productId;

  private URI location;

  private Product product;

 
public ProductResult() {}

  public ProductResult(Long productId, URI location) {
    this.productId = productId;
    this.location = location;
  }

  public void setLocation(URI location) {
    this.location = location;
  }

  public URI getLocation() {
    return location;
  }

  public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	
  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public Long getProductId() {
    return productId;
  }

  @Override
  public String toString() {
    return Pojomatic.toString(this);
  }

  @Override
  public boolean equals(Object other) {
    return Pojomatic.equals(this, other);
  }

  @Override
  public int hashCode() {
    return Pojomatic.hashCode(this);
  }
}
