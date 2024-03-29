package com.example.controller;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.constants.Message;
import com.example.enums.OrderStatus;
import com.example.enums.PaymentMethod;
import com.example.enums.PaymentStatus;
import com.example.form.OrderForm;
import com.example.model.Order;
import com.example.model.OrderDelivery;
import com.example.model.OrderPayment;
import com.example.service.OrderDeliveryService;
import com.example.service.OrderPaymentService;
import com.example.service.OrderService;
import com.example.service.ProductService;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("/orders")
public class OrderController {

	@Autowired
	private OrderService orderService;

	@Autowired
	private ProductService productService;

	@Autowired
	private OrderDeliveryService orderDeliveryService;

	@Autowired
	private OrderPaymentService orderPaymentService;

	@GetMapping
	public String index(Model model) {
		List<Order> all = orderService.findAllWithOrderProducts();
		model.addAttribute("listOrder", all);
		return "order/index";
	}

	@GetMapping("/{id}")
	public String show(Model model, @PathVariable("id") Long id) {
		if (id != null) {
			Optional<Order> order = orderService.findOne(id);
			model.addAttribute("order", order.get());
		}
		return "order/show";
	}

	@GetMapping(value = "/new")
	public String create(Model model, @ModelAttribute OrderForm.Create entity) {
		model.addAttribute("order", entity);
		model.addAttribute("products", productService.findAll());
		model.addAttribute("paymentMethods", PaymentMethod.values());
		return "order/create";
	}

	@PostMapping
	public String create(@Validated @ModelAttribute OrderForm.Create entity, BindingResult result,
			RedirectAttributes redirectAttributes) {
		Order order = null;
		try {
			order = orderService.create(entity);
			redirectAttributes.addFlashAttribute("success", Message.MSG_SUCESS_INSERT);
			return "redirect:/orders/" + order.getId();
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", Message.MSG_ERROR);
			e.printStackTrace();
			return "redirect:/orders";
		}
	}

	@GetMapping("/{id}/edit")
	public String update(Model model, @PathVariable("id") Long id) {
		try {
			if (id != null) {
				Optional<Order> entity = orderService.findOne(id);
				model.addAttribute("order", entity.get());
				model.addAttribute("paymentMethods", PaymentMethod.values());
				model.addAttribute("paymentStatus", PaymentStatus.values());
				model.addAttribute("orderStatus", OrderStatus.values());
			}
		} catch (Exception e) {
			throw new ServiceException(e.getMessage());
		}
		return "order/form";
	}

	@PutMapping
	public String update(@Validated @ModelAttribute Order entity, BindingResult result,
			RedirectAttributes redirectAttributes) {
		Order order = null;
		try {
			order = orderService.save(entity);
			redirectAttributes.addFlashAttribute("success", Message.MSG_SUCESS_UPDATE);
			return "redirect:/orders/" + order.getId();
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", Message.MSG_ERROR);
			e.printStackTrace();
			return "redirect:/orders";
		}
	}

	@DeleteMapping("/{id}")
	public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
		try {
			if (id != null) {
				Optional<Order> entity = orderService.findOne(id);
				orderService.delete(entity.get());
				redirectAttributes.addFlashAttribute("success", Message.MSG_SUCESS_DELETE);
			}
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", Message.MSG_ERROR);
			throw new ServiceException(e.getMessage());
		}
		return "redirect:/orders";
	}

	@PostMapping("/{id}/payments")
	public String createPayment(@Validated @ModelAttribute OrderForm.CreatePayment entity, BindingResult result,
			RedirectAttributes redirectAttributes) {
		try {
			orderService.createPayment(entity);
			redirectAttributes.addFlashAttribute("success", Message.MSG_SUCESS_PAYMENT_INSERT);
			return "redirect:/orders/" + entity.getOrderId();
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", Message.MSG_ERROR);
			e.printStackTrace();
			return "redirect:/orders";
		}
	}

	@GetMapping("/shipping")
	public String shipping() {
		return "order/shipping";
	}

	/**
	 * CSVインポート処理
	 *
	 * @param uploadFile
	 * @param redirectAttributes
	 * @return
	 */
	@PostMapping("/shipping")
	public String uploadFile(@RequestParam("file") MultipartFile uploadFile, RedirectAttributes redirectAttributes,
			Model model) {
		if (uploadFile.isEmpty()) {
			// ファイルが存在しない場合
			redirectAttributes.addFlashAttribute("error", "ファイルを選択してください。");
			return "redirect:/orders/shipping";
		}
		if (!"text/csv".equals(uploadFile.getContentType())) {
			// CSVファイル以外の場合
			redirectAttributes.addFlashAttribute("error", "CSVファイルを選択してください。");
			return "redirect:/orders/shipping";
		}
		try {
			List<OrderDelivery> orderShipping = orderService.getCSV(uploadFile);
			model.addAttribute("orderShippingList", orderShipping);
		} catch (Throwable e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
			e.printStackTrace();
			return "redirect:/orders/shipping";
		}

		return "order/shipping";
	}

	/**
	 * CSVテンプレートダウンロード処理
	 *
	 * @param response
	 * @param redirectAttributes
	 * @return
	 */
	@PostMapping("/shipping/download")
	public String download(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try (BufferedWriter bw = new BufferedWriter(
				new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8))) {

			String attachment = "attachment; filename=orderDelivery_" + new Date().getTime() + ".csv";

			// コンテンツの種類をCSVに設定
			response.setContentType("text/csv");
			// ダウンロード時のファイル名を指定
			response.setHeader("Content-Disposition", attachment);

			// レスポンスヘッダーをフラッシュして、設定を即座に反映
			response.flushBuffer();

			List<Order> orders = orderService.findByStatus("ordered");

			for (Order order : orders) {
				// 未発送のデータのみをCSVに書き込む
				Optional<OrderDelivery> orderDeliveryOptional = orderDeliveryService.findByOrderId(order.getId());
				if (orderDeliveryOptional.isPresent()) {
					OrderDelivery orderDelivery = orderDeliveryOptional.get();
					String csvLine = order.getId() + "," + orderDelivery.getShippingCode() + ","
							+ orderDelivery.getShippingDate() + "," + orderDelivery.getDeliveryDate() + ","
							+ orderDelivery.getDeliveryTimezone();

					bw.write(csvLine);
					bw.newLine();
				}
			}
			// フラッシュしてデータをクライアントに送信
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@GetMapping("/payment")
	public String paymentList() {
		return "order/payment";
	}

	/**
	 * 入金CSVテンプレートダウンロード処理
	 *
	 * @param response
	 * @param redirectAttributes
	 * @return
	 */
	@PostMapping("/payment/download")
	public String downloadPaid(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try (BufferedWriter bw = new BufferedWriter(
				new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8))) {
			String attachment = "attachment; filename=orderPayment_" + new Date().getTime() + ".csv";

			response.setContentType("text/csv");
			response.setHeader("Content-Disposition", attachment);
			response.flushBuffer();

			List<Order> orders = orderService.findByPaymentStatusNot("paid");

			for (Order order : orders) {
				Optional<OrderPayment> orderPaymentOptional = orderPaymentService.findByOrderId(order.getId());
				if (orderPaymentOptional.isPresent()) {
					OrderPayment orderPayment = orderPaymentOptional.get();
					String csvLine = order.getId() + "," + orderPayment.getPaid() + "," + order.getPaymentStatus() + ","
							+ orderPayment.getMethod();

					bw.write(csvLine);
					bw.newLine();
				}
			}

			bw.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * CSVインポート処理
	 *
	 * @param uploadFile
	 * @param redirectAttributes
	 * @return
	 */
	@PostMapping("/payment")
	public String upload(@RequestParam("file") MultipartFile uploadFile, RedirectAttributes redirectAttributes,
			Model model) {
		if (uploadFile.isEmpty()) {
			// ファイルが存在しない場合
			redirectAttributes.addFlashAttribute("error", "ファイルを選択してください。");
			return "redirect:/orders/payment";
		}
		if (!"text/csv".equals(uploadFile.getContentType())) {
			// CSVファイル以外の場合
			redirectAttributes.addFlashAttribute("error", "CSVファイルを選択してください。");
			return "redirect:/orders/payment";
		}
		try {
			List<OrderPayment> orderPayments = orderService.getCsvList(uploadFile);
			model.addAttribute("orderPaymentList", orderPayments);
		} catch (Throwable e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
			e.printStackTrace();
			return "redirect:/orders/payment";
		}
		return "order/payment";
	}
}
