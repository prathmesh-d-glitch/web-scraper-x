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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getElementToScrape() {
        return elementToScrape;
    }

    public void setElementToScrape(String elementToScrape) {
        this.elementToScrape = elementToScrape;
    }

    public String getTagForElementToScrape() {
        return tagForElementToScrape;
    }

    public void setTagForElementToScrape(String tagForElementToScrape) {
        this.tagForElementToScrape = tagForElementToScrape;
    }

    public Boolean getMethodForElementToScrape() {
        return methodForElementToScrape;
    }

    public void setMethodForElementToScrape(Boolean methodForElementToScrape) {
        this.methodForElementToScrape = methodForElementToScrape;
    }

    public String getAttrForElementToScrape() {
        return attrForElementToScrape;
    }

    public void setAttrForElementToScrape(String attrForElementToScrape) {
        this.attrForElementToScrape = attrForElementToScrape;
    }
}
