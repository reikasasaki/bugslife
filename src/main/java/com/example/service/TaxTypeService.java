package com.example.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.TaxType;
import com.example.repository.TaxTypeRepository;

import jakarta.transaction.Transactional;

@Service
public class TaxTypeService {

	@Autowired
	private TaxTypeRepository taxTypeRepository;

	public List<TaxType> findAll() {
		return taxTypeRepository.findAll();
	}

	/**
	 * Rounding
	 */
	public static final String FLOOR = "floor";
	public static final String ROUND = "round";
	public static final String CEIL = "ceil";

	public List<TaxType> save(Integer rates) {
		List<TaxType> existingTaxTypes = taxTypeRepository.findByTaxRate(rates);

		if (existingTaxTypes.isEmpty()) {
			List<TaxType> taxTypes = new ArrayList<>();
			boolean[] taxIncludeds = { false, true };
			String[] roundings = { FLOOR, ROUND, CEIL };
			for (boolean taxIncluded : taxIncludeds) {
				for (String rounding : roundings) {
					TaxType taxType = new TaxType();
					// IDは自動生成されるため、ここでは設定不要
					taxType.setTaxRate(rates);
					taxType.setTaxIncluded(taxIncluded);
					taxType.setRounding(rounding);

					taxTypeRepository.save(taxType);
					taxTypes.add(taxType);
				}
			}
			return taxTypes;
		} else {
			// 同じ税率のデータが存在する場合は何もしないか、エラーをハンドリングするなどの処理を行う
			throw new RuntimeException("同じ税率のデータが既に存在します。");
		}
	}

	public Optional<TaxType> findOne(Long id) {
		return taxTypeRepository.findById(id);
	}

	@Transactional
	public List<List<TaxType>> getGroupedTaxTypes() {
		List<TaxType> taxTypes = taxTypeRepository.findAll();
		Map<Integer, List<TaxType>> groupedByTaxRate = taxTypes.stream()
				.collect(Collectors.groupingBy(TaxType::getTaxRate));

		return new ArrayList<>(groupedByTaxRate.values());
	}

	public List<TaxType> findByTaxRate(Integer rate) {
		return taxTypeRepository.findByTaxRate(rate);
	}

	public void delete(Integer rate) {
		List<TaxType> taxTypes = taxTypeRepository.findByTaxRate(rate);
		if (!taxTypes.isEmpty()) {
			taxTypeRepository.deleteAll(taxTypes);
		}
	}
}
