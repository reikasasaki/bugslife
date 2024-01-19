package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.expression.spel.ast.OpAnd;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.model.TaxType;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaxTypeRepository extends JpaRepository<TaxType, Long> {
	@Query("SELECT t.id FROM TaxType t where t.taxRate = :taxRate AND t.taxIncluded = :taxIncluded AND t.rounding = :rounding")

	Long findByTaxRateAndTaxIncludedAndRounding(@Param("taxRate") Integer rate,
			@Param("taxIncluded") Boolean taxIncluded, @Param("rounding") String rounding);

	List<TaxType> findByTaxRate(Integer taxRate);
}