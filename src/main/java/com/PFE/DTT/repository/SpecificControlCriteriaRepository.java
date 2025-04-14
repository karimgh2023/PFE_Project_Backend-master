package com.PFE.DTT.repository;

import com.PFE.DTT.model.SpecificControlCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpecificControlCriteriaRepository extends JpaRepository<SpecificControlCriteria, Integer> {
    @Query("SELECT s FROM SpecificControlCriteria s WHERE s.protocol.id = :protocolId")
    List<SpecificControlCriteria> findByProtocolId(@Param("protocolId") int protocolId);



}
