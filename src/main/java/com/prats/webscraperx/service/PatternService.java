package com.prats.webscraperx.service;

import com.prats.webscraperx.repository.PatternRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatternService {

    @Autowired
    PatternRepository patternRepository;
}
