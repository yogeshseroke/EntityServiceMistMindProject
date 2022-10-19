package com.scai.entities.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scai.entities.model.SkuHierarchy;

@Repository
public interface SkuHierarchyRepository extends JpaRepository<SkuHierarchy, Integer> {

}
