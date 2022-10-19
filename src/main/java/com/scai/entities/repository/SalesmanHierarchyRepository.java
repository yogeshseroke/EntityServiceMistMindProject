package com.scai.entities.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scai.entities.model.SalesmanHierarchy;

@Repository
public interface SalesmanHierarchyRepository extends JpaRepository<SalesmanHierarchy, Integer> {

}
