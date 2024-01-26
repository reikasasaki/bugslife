package com.example.controller.api;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.Order;
import com.example.service.OrderService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/orders/payment")
public class ApiOrderPaymentController {

	@Autowired
	private OrderService orderService;

	@PutMapping
	public void paymentUpdate(@RequestParam("order_id[]") List<Long> orderId,
			@RequestParam("paid[]") List<Double> paid, @RequestParam("method[]") List<String> method) {
		for (int i = 0; i < orderId.size(); i++) {
			Long id = orderId.get(i);
			Order order = orderService.findById(id);
			if (order != null) {
				Double grandTotal = order.getGrandTotal();
				Double paidAmount = paid.get(i);

				String methodUpdate = method.get(i);
				if (grandTotal.equals(paidAmount)) {
					order.setPaymentStatus("paid");
					if ("shipped".equals(order.getStatus())) {
						order.setStatus("completed");
					}
				} else if (grandTotal < paidAmount) {
					order.setPaymentStatus("overpaid");
				} else if (grandTotal - paidAmount > 0) {
					order.setPaymentStatus("partially_paid");
				} else if (grandTotal - paidAmount < 0) {
					order.setPaymentStatus("refunded");
				}
				order.setPaymentMethod(methodUpdate);
				order.setPaid(paidAmount);
				orderService.save(order);

			}
		}
	}
}
