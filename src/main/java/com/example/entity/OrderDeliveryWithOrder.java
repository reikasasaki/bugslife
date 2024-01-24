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

}
