package com.example.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.model.OrderDelivery;

@Repository
public interface OrderDeliveryRepository extends JpaRepository<OrderDelivery, Long> {
	public Optional<OrderDelivery> findByOrderId(Long orderId);
}
