package com.PFE.DTT.repository;

import com.PFE.DTT.model.Protocol;
import com.PFE.DTT.model.StandardControlCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProtocolRepository extends JpaRepository<Protocol, Long> {

    // Removing the query as StandardControlCriteria does not have a protocol attribute
    // We need to create a separate method or adjust the relationship between classes
}
