package com.example.controller.api;

import com.example.service.CategoryService;
import com.example.service.TransactionAmountService;
import com.example.model.Category;
import com.example.model.TransactionAmount;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
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
		List<TransactionAmount> aaa = new ArrayList<TransactionAmount>();
		// csvファイルのインポート処理
		try {
			aaa = transactionAmountService.importCSV(csvFile, companyId);
		} catch (Throwable t) {
			redirectAttributes.addFlashAttribute("error", t.getMessage());
			t.printStackTrace();
			return "transactionAmounts/{id}";
		}

		return aaa;
	}

}
