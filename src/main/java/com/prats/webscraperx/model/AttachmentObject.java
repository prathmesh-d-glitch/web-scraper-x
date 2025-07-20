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
}
