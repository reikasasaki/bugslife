package com.example.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tax_type")
public class TaxType {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "tax_rate", nullable = false)
	public Integer taxRate;

	@Column(name = "tax_included", nullable = false)
	private Boolean taxIncluded;

	@Column(name = "rounding", nullable = false)
	private String rounding;

	@OneToMany(orphanRemoval = true)
	@JoinColumn(name = "tax_type", referencedColumnName = "id", insertable = false, updatable = false)
	private List<Product> products;
}
