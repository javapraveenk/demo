package com.sapient.soa.demo.vo;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@XmlRootElement
@AutoProperty
public class ProductsResultContainer {
  public List<ProductResult> getProductResult() {
    return productResult;
  }

  public void setUserResult(List<ProductResult> productResult) {
    this.productResult = productResult;
  }

  public void addUserResult(ProductResult result) {
	  productResult.add(result);
  }

  private List<ProductResult> productResult = new ArrayList<ProductResult>();

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
