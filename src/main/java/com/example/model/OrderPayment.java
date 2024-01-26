package com.example.model;

import java.io.Serializable;
import java.lang.String;
import java.sql.Timestamp;

import com.example.enums.PaymentMethod;

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
@Table(name = "order_payments")
public class OrderPayment extends TimeEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "type", nullable = false)
	private String type;

	@Column(name = "paid", nullable = false)
	private Double paid;

	@Column(name = "method", nullable = false)
	private String method;

	@Column(name = "paid_at", nullable = false)
	private Timestamp paidAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	private Order order;

	/**
	 * 支払い方法名を取得する
	 */
	public String getPaymentMethodName() {
		var value = PaymentMethod.getPaymentMethod(this.method);
		if (value != null) {
			return value.getName();
		}
		return "";
	}
}
