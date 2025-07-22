package com.prats.webscraperx.controller;

import com.prats.webscraperx.model.Pattern;
import com.prats.webscraperx.service.PatternService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/webscraperx/api/pattern")
public class PatternController {

    @Autowired
    private PatternService patternService;

    @GetMapping("/getAllPatterns")
    public List<Pattern> getAllPatterns(){
        return patternService.getAllPatterns();
    }

    @GetMapping("/getPattern/{id}")
    public Pattern getPatternById(@PathVariable("id") Long id) {
        return patternService.getPatternById(id).orElse(null);
    }

    @PostMapping("/createPattern")
    public String createPattern(@RequestBody Pattern pattern) {
        if (patternService.patternExists(pattern.getPatternName())) {
            return "Pattern already exists";
        } else {
            patternService.createPattern(pattern);
            return "Pattern succesfully created.";
        }
    }

    @PutMapping("/updatePattern/{id}")
    public String updatePattern(@PathVariable("id") Long id, @RequestBody Pattern pattern) {
        if (patternService.patternExists(id)) {
            pattern.setId(id);
            patternService.updatePattern(pattern);
            return "Pattern successfully updated.";
        } else {
            return "Pattern not found.";
        }
    }

    @DeleteMapping("/deletePattern/{id}")
    public String deletePattern(@PathVariable("id") Long id) {
        if (patternService.patternExists(id)) {
            patternService.deletePattern(id);
            return "Pattern successfully deleted.";
        } else {
            return "Pattern not found.";
        }
    }

    @DeleteMapping("/deleteAllPatterns")
    public String deleteAllPatterns() {
        patternService.deleteAllPatterns();
        return "All patterns successfully deleted.";
    }
}
