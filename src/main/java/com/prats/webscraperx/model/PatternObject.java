package com.prats.webscraperx.model;

import jakarta.persistence.*;
import jakarta.persistence.Entity;

@Entity

@Table(name = "pattern_objects")
public class PatternObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String elementToScrape;

   @Column(nullable = false)
    private String tagForElementToScrape;

    @Column(nullable = false)
    private Boolean methodForElementToScrape;

    @Column(nullable = true)
    private String attrForElementToScrape;
}
