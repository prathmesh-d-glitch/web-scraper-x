package com.prats.webscraperx.model;

import jakarta.persistence.*;
import jakarta.persistence.Entity;

@Entity
@Table(name = "attachment_objects")
public class AttachmentObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tagForElementToScrape;

    @Column(nullable = false)
    private String attrForElementToScrape;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTagForElementToScrape() {
        return tagForElementToScrape;
    }

    public void setTagForElementToScrape(String tagForElementToScrape) {
        this.tagForElementToScrape = tagForElementToScrape;
    }

    public String getAttrForElementToScrape() {
        return attrForElementToScrape;
    }

    public void setAttrForElementToScrape(String attrForElementToScrape) {
        this.attrForElementToScrape = attrForElementToScrape;
    }
}
