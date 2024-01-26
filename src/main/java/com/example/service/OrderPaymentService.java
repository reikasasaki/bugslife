package com.example.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.model.OrderPayment;
import com.example.repository.OrderPaymentRepository;

@Service
@Transactional(readOnly = true)
public class OrderPaymentService {

	@Autowired
	private OrderPaymentRepository orderPaymentRepository;

	public Optional<OrderPayment> findByOrderId(Long orderId) {
		return orderPaymentRepository.findByOrderId(orderId);
	}

}
