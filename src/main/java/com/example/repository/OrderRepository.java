package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.model.Order;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
	List<Order> findByStatus(String status);

	List<Order> findByPaymentStatusNot(String status);
}
