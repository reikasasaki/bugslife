package com.example.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.model.OrderDelivery;
import com.example.repository.OrderDeliveryRepository;

@Service
@Transactional(readOnly = true)
public class OrderDeliveryService {
	@Autowired
	private OrderDeliveryRepository orderDeliveryRepository;

	public Optional<OrderDelivery> findByOrderId(Long orderId) {
		return orderDeliveryRepository.findByOrderId(orderId);
	}
}
