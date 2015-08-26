package com.sapient.soa.demo.vo;

import javax.xml.bind.annotation.XmlRootElement;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@XmlRootElement
@AutoProperty
public class Product {
	private String productId;

	private String content;

	private String productName;

	private String productType;

	private Long productPrice;

	public Product() {
	}
	
	public Product(String productId, String content) {
		this.productId = productId;
		this.content = content;
	}
	
	public Product(String productId, String content,String productName,String productType,Long productPrice) {
		this.productId = productId;
		this.content = content;
		this.productName = productName;
		this.productType = productType;
		this.productPrice = productPrice;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public Long getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(Long productPrice) {
		this.productPrice = productPrice;
	}



	public String getProductId() {
		return productId;
	}

	public void setProduct(String productId) {
		this.productId = productId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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
