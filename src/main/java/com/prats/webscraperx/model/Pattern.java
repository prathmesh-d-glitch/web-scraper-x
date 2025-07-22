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
    private Long id;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPatternName() {
        return patternName;
    }

    public void setPatternName(String patternName) {
        this.patternName = patternName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTagForBody() {
        return tagForBody;
    }

    public void setTagForBody(String tagForBody) {
        this.tagForBody = tagForBody;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getEntityPath() {
        return entityPath;
    }

    public void setEntityPath(String entityPath) {
        this.entityPath = entityPath;
    }

    public String getAttrForEntityId() {
        return attrForEntityId;
    }

    public void setAttrForEntityId(String attrForEntityId) {
        this.attrForEntityId = attrForEntityId;
    }

    public Boolean getHasPrescraping() {
        return hasPrescraping;
    }

    public void setHasPrescraping(Boolean hasPrescraping) {
        this.hasPrescraping = hasPrescraping;
    }

    public String getTagForPrescraping() {
        return tagForPrescraping;
    }

    public void setTagForPrescraping(String tagForPrescraping) {
        this.tagForPrescraping = tagForPrescraping;
    }

    public Boolean getHaveToExplore() {
        return haveToExplore;
    }

    public void setHaveToExplore(Boolean haveToExplore) {
        this.haveToExplore = haveToExplore;
    }

    public String getTagForInnerBody() {
        return tagForInnerBody;
    }

    public void setTagForInnerBody(String tagForInnerBody) {
        this.tagForInnerBody = tagForInnerBody;
    }

    public Boolean getHasAttachments() {
        return hasAttachments;
    }

    public void setHasAttachments(Boolean hasAttachments) {
        this.hasAttachments = hasAttachments;
    }

    public Boolean getHasInnerAttachments() {
        return hasInnerAttachments;
    }

    public void setHasInnerAttachments(Boolean hasInnerAttachments) {
        this.hasInnerAttachments = hasInnerAttachments;
    }

    public List<PatternObject> getPatternObjects() {
        return patternObjects;
    }

    public void setPatternObjects(List<PatternObject> patternObjects) {
        this.patternObjects = patternObjects;
    }

    public List<PatternObject> getInnerPatternObjects() {
        return innerPatternObjects;
    }

    public void setInnerPatternObjects(List<PatternObject> innerPatternObjects) {
        this.innerPatternObjects = innerPatternObjects;
    }

    public List<AttachmentObject> getAttachmentObject() {
        return attachmentObject;
    }

    public void setAttachmentObject(List<AttachmentObject> attachmentObject) {
        this.attachmentObject = attachmentObject;
    }

    public List<AttachmentObject> getAttachmentInnerObject() {
        return attachmentInnerObject;
    }

    public void setAttachmentInnerObject(List<AttachmentObject> attachmentInnerObject) {
        this.attachmentInnerObject = attachmentInnerObject;
    }
}
