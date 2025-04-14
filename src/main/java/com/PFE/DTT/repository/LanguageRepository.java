package com.PFE.DTT.repository;

import com.PFE.DTT.model.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Long> {
    Optional<Language> findByCode(String code);
    Optional<Language> findByIsDefaultTrue();
} 