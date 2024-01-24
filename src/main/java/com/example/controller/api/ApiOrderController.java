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
@RequestMapping("/api/orders/shipping")
public class ApiOrderController {

	@Autowired
	private OrderService orderService;

	@PutMapping
	public void orderUpdate(@RequestParam("order_id[]") List<Long> orderId) {
		for (Long id : orderId) {
			Order order = orderService.findById(id);
			if (order != null) {
				order.setStatus("shipped");
				orderService.save(order);
				if ("paid".equals(order.getPaymentStatus())) {
					order.setStatus("completed");
					orderService.save(order);
				}
			}
		}
	}
}
