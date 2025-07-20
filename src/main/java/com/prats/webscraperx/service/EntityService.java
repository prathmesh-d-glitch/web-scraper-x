package com.prats.webscraperx.service;

import com.prats.webscraperx.model.Entity;
import com.prats.webscraperx.repository.EntityRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EntityService {

    @Autowired
    EntityRepository entityRepository;

    private final String storageDir = "uploads/";

    public List<Entity> getList() {
        return entityRepository.findAll();
    }

    public Optional<Entity> getEntityById(Long id) {
        return entityRepository.findById(id);
    }

    public Entity createEntity(Entity entity) {
        return entityRepository.save(entity);
    }

    public Entity updateEntity(Entity Entity) {
        return entityRepository.save(Entity);
    }

    public Entity updateScraping(Entity entity, String url) {
        Date now = new Date();
        List<Entity> entities = getByUrl(url);
        if (entity.getEntityId() != null) {
            if (entities.isEmpty()) {
                return entityRepository.save(entity);
            }
            else {
                for (Entity e : entities) {
                    if (e.getEntityId().equals(entity.getEntityId())&&
                    e.getLastScaping().before(now)) {
                        entity.setEntityId(e.getEntityId());
                        return entityRepository.save(entity);
                    }
                }
            }
            return entityRepository.save(entity);
        }
        return null;
    }

    public void delete(Long id) {
        entityRepository.deleteById(id);
    }

    public void deleteAll() {
        entityRepository.deleteAll();
    }

    public List<Entity> getByUrl(String url) {
        return entityRepository.findByBasePath(url);
    }

    @Transactional
    public UUID saveAttachment(String attachmentLink, String entityId, String basePath) {
        try {
            String originalName = attachmentLink.substring(attachmentLink.lastIndexOf("/") + 1);

            URL url = new URL(attachmentLink);
            URLConnection connection = url.openConnection();
            InputStream inputStream = connection.getInputStream();

            File dir = new File(storageDir);
            if(!dir.exists()) {
                dir.mkdirs();
            }
            UUID fileID = UUID.randomUUID();
            File outputFile = new File(storageDir, fileID + "_" + originalName);

            try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                inputStream.transferTo(outputStream);
            }

            List<Entity> entities = getByUrl(basePath);
            for (Entity entity : entities) {
                if (entity.getEntityId().equals(entityId)) {
                    entity.getAttachmentIds().add(fileID);
                    entityRepository.save(entity);
                    break;
                }
            }
            return fileID;
        } catch (Exception ex) {
            System.out.println("Attachment download failed: " + ex.getMessage());
        }
        return null;
    }

    public String downloadAttachment(Long fileId, String fileName, String downloadPath) throws IOException {
        File file = new File(storageDir + fileId + "_" + fileName);
        File dest = new File(downloadPath + fileName);

        try (InputStream in = new FileInputStream(file);
             OutputStream out = new FileOutputStream(dest)) {
            in.transferTo(out);
        }

        return "File downloaded to: " + downloadPath + fileName;
    }

    public InputStream showAttachment(Long fileId, String fileName) throws IOException {
        File file = new File(storageDir + fileId + "_" + fileName);
        return new FileInputStream(file);
    }
}
