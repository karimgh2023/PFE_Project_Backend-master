package com.PFE.DTT.service;

import com.PFE.DTT.dto.LanguageDTO;
import com.PFE.DTT.model.Language;
import com.PFE.DTT.repository.LanguageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class    LanguageService {

    @Autowired
    private LanguageRepository languageRepository;

    public List<LanguageDTO> getAllLanguages() {
        return languageRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public LanguageDTO getLanguageByCode(String code) {
        return languageRepository.findByCode(code)
                .map(this::convertToDTO)
                .orElse(null);
    }

    public LanguageDTO getDefaultLanguage() {
        return languageRepository.findByIsDefaultTrue()
                .map(this::convertToDTO)
                .orElse(null);
    }

    private LanguageDTO convertToDTO(Language language) {
        return new LanguageDTO(
                language.getId(),
                language.getCode(),
                language.getName(),
                language.getFlagUrl(),
                language.isDefault()
        );
    }
} 