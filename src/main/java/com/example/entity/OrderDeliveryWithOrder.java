package com.example.entity;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderDeliveryWithOrder {

	private Long orderId;

	private String shippingCode;

	private Timestamp shippingDate;

	private Timestamp deliveryDate;

	private String deliveryTimezone;

	public ProductWithCategoryName(Long id, String code, String name, Integer weight, Integer height, Double price,
			String categoryName) {
		this.setId(id);
		this.setCode(code);
		this.setName(name);
		this.setWeight(weight);
		this.setHeight(height);
		this.setPrice(price);
		this.setCategoryName(categoryName);
	}
}
