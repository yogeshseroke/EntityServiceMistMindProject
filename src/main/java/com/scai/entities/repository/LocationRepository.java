package com.scai.entities.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scai.entities.model.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Integer>{

}
