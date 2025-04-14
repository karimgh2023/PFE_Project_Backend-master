package com.PFE.DTT.controller;

import com.PFE.DTT.dto.LanguageDTO;
import com.PFE.DTT.service.LanguageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/languages")
@CrossOrigin(origins = "*")
public class LanguageController {

    @Autowired
    private LanguageService languageService;

    @GetMapping
    public ResponseEntity<List<LanguageDTO>> getAllLanguages() {
        return ResponseEntity.ok(languageService.getAllLanguages());
    }

    @GetMapping("/{code}")
    public ResponseEntity<LanguageDTO> getLanguageByCode(@PathVariable String code) {
        LanguageDTO language = languageService.getLanguageByCode(code);
        if (language != null) {
            return ResponseEntity.ok(language);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/default")
    public ResponseEntity<LanguageDTO> getDefaultLanguage() {
        LanguageDTO language = languageService.getDefaultLanguage();
        if (language != null) {
            return ResponseEntity.ok(language);
        }
        return ResponseEntity.notFound().build();
    }
} 