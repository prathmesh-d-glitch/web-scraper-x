package com.prats.webscraperx.model;



import jakarta.persistence.*;

import java.util.*;

@jakarta.persistence.Entity
@Table(name = "entities")
public class Entity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Entity_id")
    private String entityId;

    @Column(name = "base_path")
    private String basePath;

    @Column(name = "path")
    private String path;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_scaping")
    private Date lastScaping;

    @ElementCollection
    @CollectionTable(name = "entity_content", joinColumns = @JoinColumn(name = "Entity_id"))
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    private Map<String, String> content = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "entity_attachments", joinColumns = @JoinColumn(name = "entity_id"))
    @Column(name = "attachment_id")
    private List<UUID> attachmentIds = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "entity_info", joinColumns = @JoinColumn(name = "entity_id"))
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    private Map<String, String> info = new HashMap<>();

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Date getLastScaping() {
        return lastScaping;
    }

    public void setLastScaping(Date lastScaping) {
        this.lastScaping = lastScaping;
    }

    public Map<String, String> getContent() {
        return content;
    }

    public void setContent(Map<String, String> content) {
        this.content = content;
    }

    public List<UUID> getAttachmentIds() {
        return attachmentIds;
    }

    public void setAttachmentIds(List<UUID> attachmentIds) {
        this.attachmentIds = attachmentIds;
    }

    public Map<String, String> getInfo() {
        return info;
    }

    public void setInfo(Map<String, String> info) {
        this.info = info;
    }

    public void setEntityInfo(String key, String value) {
        this.info.put(key, value);
    }

    public void setEntityContent(String key, String value) {
        this.content.put(key, value);
    }

    public void setAttachmentId(UUID attachmentId) {
        this.attachmentIds.add(attachmentId);
    }

    public void addAttachmentId(UUID uuid) {
        if (uuid != null && !this.attachmentIds.contains(uuid)) {
            this.attachmentIds.add(uuid);
        }
    }
}