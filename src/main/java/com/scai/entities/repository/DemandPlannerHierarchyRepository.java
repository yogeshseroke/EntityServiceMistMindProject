package com.scai.entities.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scai.entities.model.DemandPlannerHierarchy;

@Repository
public interface DemandPlannerHierarchyRepository extends JpaRepository<DemandPlannerHierarchy, Integer> {

}
