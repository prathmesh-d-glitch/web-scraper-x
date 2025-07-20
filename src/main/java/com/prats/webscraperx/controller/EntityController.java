package com.prats.webscraperx.controller;

import com.prats.webscraperx.service.EntityService;
import com.prats.webscraperx.service.PatternService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webscraperx/api")
public class EntityController {

    @Autowired
    private EntityService EntityService;

    @Autowired
    private PatternService patternService;



}
