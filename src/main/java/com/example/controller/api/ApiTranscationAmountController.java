package com.example.controller.api;

import com.example.service.CategoryService;
import com.example.service.CompanyService;
import com.example.service.TransactionAmountService;
import com.example.constants.Message;
import com.example.model.Category;
import com.example.model.Company;
import com.example.model.TransactionAmount;

import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.Map;

@RestController
@RequestMapping("/api/transactionAmounts")
public class ApiTranscationAmountController {

	@Autowired
	private TransactionAmountService transactionAmountService;

	@Autowired
	private CompanyService companyService;

	/**
	 * 取引金額CSVインポート処理
	 *
	 * @param csvFile
	 * @param redirectAttributes
	 * @return
	 */
	@PostMapping("/{c_id}/upload_csv")
	public Object uploadCSVFile(@PathVariable("c_id") Long companyId, @RequestParam("csv_file") MultipartFile csvFile,
			RedirectAttributes redirectAttributes) {
		List<TransactionAmount> transactionAmounts = new ArrayList<>();
		Company company = companyService.findOne(companyId).get();
		if (csvFile.isEmpty()) {
			// ファイルが存在しない場合
			redirectAttributes.addFlashAttribute("error", "ファイルを選択してください。");
			return "transactionAmounts/{id}";
		}
		if (!"text/csv".equals(csvFile.getContentType())) {
			// CSVファイル以外の場合
			redirectAttributes.addFlashAttribute("error", "CSVファイルを選択してください。");
			return "transactionAmounts/{id}";
		}
		// csvファイルのインポート処理
		try {
			transactionAmountService.importCSV(csvFile, companyId);
			transactionAmounts = transactionAmountService.findByCompany(company);
		} catch (Throwable t) {
			redirectAttributes.addFlashAttribute("error", t.getMessage());
			t.printStackTrace();
			return "transactionAmounts/{id}";
		}

		return transactionAmounts;
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable("id") Long id) {
		try {
			if (id != null) {
				Optional<TransactionAmount> tAmountOpt = transactionAmountService.findOne(id);
				if (tAmountOpt.isPresent()) {
					transactionAmountService.delete(tAmountOpt.get());
				}
			}
		} catch (Exception e) {
			throw new ServiceException(e.getMessage());
		}
	}
}
