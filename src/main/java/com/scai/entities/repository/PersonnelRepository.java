package com.scai.entities.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scai.entities.model.Personnel;
import com.scai.entities.model.Sku;

@Repository
public interface PersonnelRepository extends JpaRepository<Personnel, Integer> {

}
