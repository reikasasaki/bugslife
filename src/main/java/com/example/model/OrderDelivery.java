package com.example.model;

import java.io.Serializable;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "order_delivery")
public class OrderDelivery {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "shipping_code", nullable = false)
	private String shippingCode;

	@Column(name = "shipping_date", nullable = false)
	private Timestamp shippingDate;

	@Column(name = "delivery_date", nullable = false)
	private Timestamp deliveryDate;

	@Column(name = "delivery_timezone", nullable = false)
	private String deliveryTimezone;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	private Order order;
}
