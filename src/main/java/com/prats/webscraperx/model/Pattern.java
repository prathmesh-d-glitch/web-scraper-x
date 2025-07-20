package com.prats.webscraperx.model;

import jakarta.persistence.*;
import jakarta.persistence.Entity;

import java.util.ArrayList;
import java.util.UUID;
import java.util.List;

@Entity
@Table(name = "patterns")
public class Pattern {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true)
    private String patternName;

    private String url;

    private String tagForBody;
    private String entityId;
    private String entityPath;
    private String attrForEntityId;

    private Boolean hasPrescraping = false;
    private String tagForPrescraping;
    private Boolean haveToExplore = false;

    private String tagForInnerBody;
    private Boolean hasAttachments = false;
    private Boolean hasInnerAttachments = false;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "pattern_id")
    private List<PatternObject> patternObjects = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "inner_pattern_id")
    private List<PatternObject> innerPatternObjects = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "attachment_id")
    private List<AttachmentObject> attachmentObject = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "inner_attachment_id")
    private List<AttachmentObject> attachmentInnerObject = new ArrayList<>();
}
