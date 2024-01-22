package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.example.enums.CampaignStatus;
import com.example.enums.DiscountType;
import com.example.enums.OrderStatus;
import com.example.enums.PaymentStatus;
import com.example.form.OrderForm;
import com.example.model.Campaign;
import com.example.model.Order;
import com.example.model.OrderDelivery;
import com.example.model.OrderPayment;
import com.example.model.OrderProduct;
import com.example.repository.OrderRepository;
import com.example.repository.ProductRepository;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;

@Service
@Transactional(readOnly = true)
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private ProductRepository productRepository;

	public List<Order> findAll() {
		return orderRepository.findAll();
	}

	public Optional<Order> findOne(Long id) {
		return orderRepository.findById(id);
	}

	@Transactional(readOnly = false)
	public Order save(Order entity) {
		return orderRepository.save(entity);
	}

	@Transactional(readOnly = false)
	public Order create(OrderForm.Create entity) {
		Order order = new Order();
		order.setCustomerId(entity.getCustomerId());
		order.setShipping(entity.getShipping());
		order.setNote(entity.getNote());
		order.setPaymentMethod(entity.getPaymentMethod());
		order.setStatus(OrderStatus.ORDERED);
		order.setPaymentStatus(PaymentStatus.UNPAID);
		order.setPaid(0.0);

		var orderProducts = new ArrayList<OrderProduct>();
		entity.getOrderProducts().forEach(p -> {
			var product = productRepository.findById(p.getProductId()).get();
			var orderProduct = new OrderProduct();
			orderProduct.setProductId(product.getId());
			orderProduct.setCode(product.getCode());
			orderProduct.setName(product.getName());
			orderProduct.setQuantity(p.getQuantity());
			orderProduct.setPrice((double)product.getPrice());
			orderProduct.setDiscount(p.getDiscount());
			// orderProduct.setTaxType(Tax.get(product.getTaxType()));
			orderProducts.add(orderProduct);
		});

		// 計算
		var total = 0.0;
		var totalTax = 0.0;
		var totalDiscount = 0.0;
		for (var orderProduct : orderProducts) {
			var price = orderProduct.getPrice();
			var quantity = orderProduct.getQuantity();
			var discount = orderProduct.getDiscount();
			var tax = 0.0;
			/**
			 * 税額を計算する
			 */
			if (orderProduct.getTaxIncluded()) {
				// 税込みの場合
				tax = price * quantity * orderProduct.getRate() / (100 + orderProduct.getRate());
			} else {
				// 税抜きの場合
				tax = price * quantity * orderProduct.getRate() / 100;
			}
			// 端数処理
			tax = switch (orderProduct.getTaxRounding()) {
			case "3" -> Math.round(tax);
			case "2" -> Math.ceil(tax);
			case "1" -> Math.floor(tax);
			default -> tax;
			};
			var subTotal = price * quantity + tax - discount;
			total += subTotal;
			totalTax += tax;
			totalDiscount += discount;
		}
		order.setTotal(total);
		order.setTax(totalTax);
		order.setDiscount(totalDiscount);
		order.setGrandTotal(total + order.getShipping());
		order.setOrderProducts(orderProducts);

		orderRepository.save(order);

		return order;

	}

	@Transactional()
	public void delete(Order entity) {
		orderRepository.delete(entity);
	}

	@Transactional(readOnly = false)
	public void createPayment(OrderForm.CreatePayment entity) {
		var order = orderRepository.findById(entity.getOrderId()).get();
		/**
		 * 新しい支払い情報を登録する
		 */
		var payment = new OrderPayment();
		payment.setType(entity.getType());
		payment.setPaid(entity.getPaid());
		payment.setMethod(entity.getMethod());
		payment.setPaidAt(entity.getPaidAt());

		/**
		 * 支払い情報を更新する
		 */
		// orderのorderPaymentsに追加
		order.getOrderPayments().add(payment);
		// 支払い済み金額を計算
		var paid = order.getOrderPayments().stream().mapToDouble(p -> p.getPaid()).sum();
		// 合計金額から支払いステータスを判定
		var paymentStatus = paid > order.getGrandTotal() ? PaymentStatus.OVERPAID
				: paid < order.getGrandTotal() ? PaymentStatus.PARTIALLY_PAID : PaymentStatus.PAID;

		// 更新
		order.setPaid(paid);
		order.setPaymentStatus(paymentStatus);
		orderRepository.save(order);
	}

	public List<Order> findByStatus(String status) {
		return orderRepository.findByStatus(status);
	}

	/**
	 * CSVインポート処理
	 *
	 * @param file
	 * @throws IOException
	 */
	@Transactional
	public void importCSV(MultipartFile file) throws IOException {
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
			String line = br.readLine(); // 1行目はヘッダーなので読み飛ばす
			// 一括更新用のリストを作成
			List<OrderDelivery> orderDeliveries = new ArrayList<>();

			while ((line = br.readLine()) != null) {
				final String[] split = line.replace("\"", "").split(",");
				final OrderDelivery orderDelivery = new OrderDelivery();
				orderDeliveries.add(orderDelivery);
			}

			// 一括更新処理
			batchInsert(orderDeliveries);

		} catch (IOException e) {
			throw new RuntimeException("ファイルが読み込めません", e);
		}
	}

	/**
	 * 一括更新処理実行
	 *
	 * @param orderDeliveries
	 */
	@SuppressWarnings("unused")
	private int[] batchInsert(List<OrderDelivery> orderDeliveries) {
		String sql = "INSERT INTO orderDeliveries (orderId, shippingCode, shippingDate, deliveryDate, deliveryTimezone)"
				+ " VALUES(:orderId, :shippingCode, :shippingDate, :deliveryDate, :deliveryTimezone)";
		// FIXME: ここでエラーが出る インサート文の問題？
		return JdbcTemplate.batchUpdate(sql,
				orderDeliveries.stream()
						.map(o -> new MapSqlParameterSource()
								.addValue("orderId", o.getOrder().getId(), Types.INTEGER)
								.addValue("shippingCode", o.getShippingCode(), Types.VARCHAR)
								.addValue("shippingDate", o.getShippingDate(), Types.DATE)
								.addValue("deliveryDate", o.getDeliveryDate(), Types.DATE)
								.addValue("deliveryTimezone", o.getDeliveryTimezone(), Types.VARCHAR))
						.toArray(SqlParameterSource[]::new));
	}
}
