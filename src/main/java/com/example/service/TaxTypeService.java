package com.example.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.TaxType;
import com.example.repository.TaxTypeRepository;

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

	public List<TaxType> save(Long id, Integer rates) {
		List<TaxType> taxTypes = new ArrayList<>();
		boolean[] taxIncludeds = { false, true };
		String[] roundings = { FLOOR, ROUND, CEIL };

		for (boolean taxIncluded : taxIncludeds) {
			for (String rounding : roundings) {
				TaxType taxType = new TaxType(id++, rates, taxIncluded, rounding);
				taxTypes.add(taxType);
				taxTypeRepository.save(taxType);
			}
		}
		return taxTypes;
	}
}
