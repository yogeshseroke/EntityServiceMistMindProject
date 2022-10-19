package com.scai.entities.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scai.entities.model.Sku;

@Repository
public interface SkuRepository extends JpaRepository<Sku, Integer> {

	Sku findByLocationCodeAndSkuCodeAndTenantId(String locationName, String skuCode, String tenantId);

}
