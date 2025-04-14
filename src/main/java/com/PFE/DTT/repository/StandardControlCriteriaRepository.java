package com.PFE.DTT.repository;

import com.PFE.DTT.model.StandardControlCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface StandardControlCriteriaRepository extends JpaRepository<StandardControlCriteria, Integer> {

    List<StandardControlCriteria> findAll();


}