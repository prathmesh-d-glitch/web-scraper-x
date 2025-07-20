package com.prats.webscraperx.service;

import com.prats.webscraperx.model.Pattern;
import com.prats.webscraperx.repository.PatternRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatternService {

    @Autowired
    private PatternRepository patternRepository;

    public List<Pattern> getAllPatterns() {
        return patternRepository.findAll();
    }

    public Optional<Pattern> getPatternById(Long id) {
        return patternRepository.findById(id);
    }

    public Pattern createPattern(Pattern pattern) {
        return patternRepository.save(pattern);
    }

    public Pattern updatePattern(Pattern pattern) {
        return patternRepository.save(pattern);
    }

    public void deletePattern(Long id) {
        patternRepository.deleteById(id);
    }

    public boolean patternExists(Long id) {
        return patternRepository.existsById(id);
    }

    public void deleteAllPatterns() {
        patternRepository.deleteAll();
    }

}
