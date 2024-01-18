package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.model.TaxType;

@Repository
public interface TaxTypeRepository extends JpaRepository<TaxType, Long> {}